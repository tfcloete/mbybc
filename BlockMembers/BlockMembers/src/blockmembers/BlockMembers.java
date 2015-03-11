/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package blockmembers;

import com.beust.jcommander.JCommander;
import com.sun.rowset.FilteredRowSetImpl;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;
import mb.MemberFilter;
import mb.ValidationItem;

/**
 *
 * @author tertius
 */
public class BlockMembers {

    static Properties properties;
    static Properties version;
    static CommandlineParameters cargs;
    static int currentPeriod;
    // ** Queries **
    static String pastelQuery = "select CustomerCode, CustomerDesc, \n" +
    "    UserDefined01, \n" +
    "    UserDefined02\n" +
    "    from CustomerMaster as cust \n" +
    "    where UserDefined01 like '%df%'\n" +
    "    or UserDefined02 like '%df%'\n" +
    "    order by CustomerCode;";

    //static String platinumDebtorQuery = "SELECT * FROM Debtors";
    static String platinumDebtorQuery = "SELECT Debtor_No, Debtor_Name, Contact_Person, "
            + "Balance,Blocked,SwipeCard,Price_Level from Debtors";
    //NOTE: Contact_Person is Member Number

    static String controlsoftQuery = "SELECT * FROM Employee order by Number";
    
    
    /****************************************************
     *                                                  *
     * Find and update the Controlsoft Employee entries *
     *                                                  *
     ****************************************************/    
    public static void blockControlsoft(FilteredRowSet pastelCustomers,
            FilteredRowSet controlsoftEmployees) {

        MemberFilter pastelFilter = new MemberFilter("CustomerCode", null);

        try {
            System.out.println("Blocking Controlsoft employees...");
            pastelCustomers.setFilter(pastelFilter);
            controlsoftEmployees.beforeFirst();
            while (controlsoftEmployees.next()) {
                //Only work with unblocked cards
                if (controlsoftEmployees.getBoolean("Enabled")) {

                    //Don't evaluate records without member number or swipe card. 
                    boolean hasNoMemberNo = controlsoftEmployees.getString("Number").trim().isEmpty();
                    if (hasNoMemberNo) {
                        continue;
                    }

                    // If the member is in the Pastel rowset he/she is defauler
                    pastelFilter.memberNumber = controlsoftEmployees.getString("Number");
                    if (pastelCustomers.first()) {

                        //controlsoftEmployees.updateBoolean("Enabled",false);
                        controlsoftEmployees.updateInt("Enabled",0);
                        controlsoftEmployees.updateRow();
                        System.out.printf("%s, %s%n", controlsoftEmployees.getString("Number"),
                                                controlsoftEmployees.getString("Surname"));
                        

                    } 
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BlockMembers.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    
    /****************************************************
     *                                                  *
     * Find and update the Platinum Customers entries *
     *                                                  *
     ****************************************************/    
    public static void blockPlatinum(FilteredRowSet pastelCustomers,
            FilteredRowSet platinumDebtor) {

        MemberFilter pastelFilter = new MemberFilter("CustomerCode", null);

        try {
            System.out.println("Blocking Platinum bebtors...");
            pastelCustomers.setFilter(pastelFilter);
            platinumDebtor.beforeFirst();
            while (platinumDebtor.next()) {
                //Only work with unblocked debtors
                if (!platinumDebtor.getBoolean("Blocked")) {

                    //Don't evaluate records without member number or swipe card. 
                    boolean hasNoMemberNo = platinumDebtor.getString("Contact_Person").trim().isEmpty();
                    if (hasNoMemberNo) {
                        continue;
                    }

                    // If the member is in the Pastel rowset he/she is defauler
                    pastelFilter.memberNumber = platinumDebtor.getString("Contact_Person").trim();
                    if (pastelCustomers.first()) {

                        //controlsoftEmployees.updateBoolean("Enabled",false);
                        platinumDebtor.updateInt("Blocked",1);
                        platinumDebtor.updateRow();
                        System.out.printf("%s, %s%n", platinumDebtor.getString("Contact_Person"),
                                                platinumDebtor.getString("Debtor_Name"));
                        

                    } 
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BlockMembers.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /********************
         ** Get properties **
         ********************/
        properties = new Properties();
        version = new Properties();
        List<String> PastelMemberTags = new ArrayList<String>();
        try {
            properties.load(new FileInputStream("mbybc.properties"));
            //version.load(new FileInputStream("src/version.properties"));
            //parse Pastel member tags
            String temp[] = properties.getProperty("PastelMemberTags").split(",");
            for(String x: temp){PastelMemberTags.add(x.trim());}
            //Calculate current period
            GregorianCalendar calendar = new GregorianCalendar();
            currentPeriod = calendar.get(Calendar.MONTH)-5;
            if(currentPeriod<1) currentPeriod += 12;
            
        } catch (Exception ex) {
            Logger.getLogger(BlockMembers.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * **********************************
         ** Process command line arguments **
         * **********************************
         */
        //Parse the commad line arguments
        cargs = new CommandlineParameters();
        new JCommander(cargs, args);


        /***********************
         ** Manage Blocking **
         ***********************/
        try (FilteredRowSet pastelRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumDebtorRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumPriceRowSet = new FilteredRowSetImpl();
                FilteredRowSet controlsoftRowSet = new FilteredRowSetImpl()) {


            // Get pastel data
            Class.forName("com.pervasive.jdbc.v2.Driver");
            pastelRowSet.setUrl(properties.getProperty("pastelURL"));
            pastelRowSet.setCommand(pastelQuery);
            pastelRowSet.execute();
            
            // Get Platinum data
            platinumDebtorRowSet.setUrl(properties.getProperty("platinumURL"));
            platinumDebtorRowSet.setCommand(platinumDebtorQuery);
            controlsoftRowSet.setTableName("Debtors");
            int [] platinumKeys = {1};
            controlsoftRowSet.setKeyColumns(platinumKeys);
            platinumDebtorRowSet.execute();
            
            // Get Controlsoft data
            controlsoftRowSet.setUrl(properties.getProperty("controlsoftURL"));
            controlsoftRowSet.setCommand(controlsoftQuery);
            controlsoftRowSet.setTableName("Employee");
            int [] controlsoftKeys = {1};
            controlsoftRowSet.setKeyColumns(controlsoftKeys);
            controlsoftRowSet.execute();

            //Block the defaulters
            blockControlsoft(pastelRowSet, controlsoftRowSet);
            blockPlatinum(pastelRowSet, platinumDebtorRowSet);
            
            //Print the list
            //printList(controlsoftRowSet);
            
            //Update the database
            controlsoftRowSet.acceptChanges();
            platinumDebtorRowSet.acceptChanges();
            
            System.out.println("Done...");

        } catch (Exception ex) {
            Logger.getLogger(BlockMembers.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        System.exit(0);
    }

    private static void printList(FilteredRowSet controlsoftEmployees ) {

        // ** Print
        try{
            controlsoftEmployees.beforeFirst();
            while (controlsoftEmployees.next()) {
                //Only work with blocked cards
                if (!controlsoftEmployees.getBoolean("Enabled")) {
                    System.out.printf("%s,%s%n", controlsoftEmployees.getString("Number"),
                                                controlsoftEmployees.getString("Surname"));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BlockMembers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

