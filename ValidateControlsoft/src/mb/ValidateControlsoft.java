/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import com.sun.rowset.FilteredRowSetImpl;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.FilteredRowSet;
import com.beust.jcommander.JCommander;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.RowSet;

/**
 *
 * @author PLATINUM
 */
public class ValidateControlsoft {

    static Properties properties;
    static Properties version;
    static CommandlineParameters cargs;
    static int currentPeriod;
    // ** Queries **
    static String pastelQuery = "select Category, CustomerCode, CustomerDesc, "
            + "CurrBalanceLast01 as p1, "
            + "CurrBalanceLast02 as p2, "
            + "CurrBalanceLast03 as p3, "
            + "CurrBalanceLast04 as p4, "
            + "CurrBalanceLast05 as p5, "
            + "CurrBalanceLast06 as p6, "
            + "CurrBalanceLast07 as p7, "
            + "CurrBalanceLast08 as p8, "
            + "CurrBalanceLast09 as p9, "
            + "CurrBalanceLast10 as p10, "
            + "CurrBalanceLast11 as p11, "
            + "CurrBalanceLast12 as p12, "
//            + "CurrBalanceLast13 as p13, "
            + "CurrBalanceThis01 as p13, "
            + "CurrBalanceThis02 as p14, "
            + "CurrBalanceThis03 as p15, "
            + "CurrBalanceThis04 as p16, "
            + "CurrBalanceThis05 as p17, "
            + "CurrBalanceThis06 as p18, "
            + "CurrBalanceThis07 as p19, "
            + "CurrBalanceThis08 as p20, "
            + "CurrBalanceThis09 as p21, "
            + "CurrBalanceThis10 as p22, "
            + "CurrBalanceThis11 as p23, "
            + "CurrBalanceThis12 as p24, "
            + "CurrBalanceThis13 as p25, "
            + "(CurrBalanceLast01 "
            + "+ CurrBalanceLast02 "
            + "+ CurrBalanceLast03 "
            + "+ CurrBalanceLast04 "
            + "+ CurrBalanceLast05 "
            + "+ CurrBalanceLast06 "
            + "+ CurrBalanceLast07 "
            + "+ CurrBalanceLast08 "
            + "+ CurrBalanceLast09 "
            + "+ CurrBalanceLast10 "
            + "+ CurrBalanceLast11 "
            + "+ CurrBalanceLast12 "
            + "+ CurrBalanceLast13 "
            + "+ CurrBalanceThis01 "
            + "+ CurrBalanceThis02 "
            + "+ CurrBalanceThis03 "
            + "+ CurrBalanceThis04 "
            + "+ CurrBalanceThis05 "
            + "+ CurrBalanceThis06 "
            + "+ CurrBalanceThis07 "
            + "+ CurrBalanceThis08 "
            + "+ CurrBalanceThis09 "
            + "+ CurrBalanceThis10 "
            + "+ CurrBalanceThis11 "
            + "+ CurrBalanceThis12 "
            + "+ CurrBalanceThis13) as balance, "
            + "UserDefined01 "
            + "from CustomerMaster as cust "
            + "inner join CustomerCategories as cat "
            + "on cust.Category = cat.CCCode "
            + "where Category<>27 and Category<>30 "
            + "and Category<>50 "
            + "order by CustomerCode";
    static String platinumDebtorQuery = "SELECT * FROM Debtors";
    static String controlsoftQuery = "SELECT * FROM Employee";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /**
         ********************
         ** Get properties **
         */
        properties = new Properties();
        version = new Properties();
        List<String> PastelMemberTags = new ArrayList<String>();
        try {
            properties.load(new FileInputStream("mbybc.properties"));
            version.load(new FileInputStream("src/version.properties"));
            //parse Pastel member tags
            String temp[] = properties.getProperty("PastelMemberTags").split(",");
            for(String x: temp){PastelMemberTags.add(x.trim());}
            //Calculate current period
            GregorianCalendar calendar = new GregorianCalendar();
            currentPeriod = calendar.get(Calendar.MONTH)-5;
            if(currentPeriod<1) currentPeriod += 12;
            
        } catch (Exception ex) {
            Logger.getLogger(ValidateControlsoft.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * **********************************
         ** Process command line arguments **
         * **********************************
         */
        //Parse the commad line arguments
        cargs = new CommandlineParameters();
        new JCommander(cargs, args);


        /**
         * *********************
         ** Manage Validation **
         */
        List<ValidationItem> results = null;
        try (FilteredRowSet pastelRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumDebtorRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumPriceRowSet = new FilteredRowSetImpl();
                RowSet controlsoftRowSet = new FilteredRowSetImpl()) {


            // Get pastel data
            Class.forName("com.pervasive.jdbc.v2.Driver");
            pastelRowSet.setUrl(properties.getProperty("pastelURL"));
            pastelRowSet.setCommand(pastelQuery);
            pastelRowSet.execute();

            // Get platinum data
            platinumDebtorRowSet.setUrl(properties.getProperty("platinumURL"));
            platinumDebtorRowSet.setCommand(platinumDebtorQuery);
            platinumDebtorRowSet.execute();
            
            // Get controlsoft data
            controlsoftRowSet.setUrl(properties.getProperty("controlsoftURL"));
            controlsoftRowSet.setCommand(controlsoftQuery);
            controlsoftRowSet.execute();

            //Run the various validations according to the commandline arguments
            ControlsoftValidator validator = new ControlsoftValidator(pastelRowSet,  
                    controlsoftRowSet, PastelMemberTags, currentPeriod);
              if (cargs.ValidateAll) {
                results = validator.validateEmployeeByItself();
                results.addAll(validator.validateByPastel());
            } else {
                results = validator.validateEmployeeByItself();
                results.addAll(validator.validateByPastel());
            }

            Collections.sort(results);
            printSummary(results, validator.validationTypes);
            if (!cargs.PrintSummaryOnly) {
                printDetail(results);
            }
            
                        
            System.out.println("Done...");

        } catch (Exception ex) {
            Logger.getLogger(ValidateControlsoft.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        if (results.isEmpty())   System.exit(1);
        else System.exit(0);
    }

    private static void printSummary(List<ValidationItem> results, String[][] validationTypes) {

        Integer[] count = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        // ** Count **
        for (ValidationItem item : results) {
            count[item.itemTypeNumber]++;
        }

        // ** Print
        System.out.printf("ValidateControlsoft, build number: %s%n%n", version.getProperty("BUILD"));
        System.out.println("*** Validation type summary ***");
        for (Integer i = 1; i < 15; i++) {
            if (count[i] > 0) {
                System.out.printf("Validation type: %d = %d, (%s)%n", i, count[i], validationTypes[i][1]);
            }
        }
        System.out.printf("*** end ***%n%n");
    }

    private static void printDetail(List<ValidationItem> results) {
        for (ValidationItem item : results) {
            //writer.write(String.format("%d, %s",item.itemTypeNumber, item.description));
            System.out.printf("%d, %s", item.itemTypeNumber, item.description);
        }
    }
}
