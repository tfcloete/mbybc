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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author PLATINUM
 */
public class ValidatePlatinum {

    static Properties properties;
    static Properties version;
    static CommandlineParameters cargs;
    // ** Queries **
    static String pastelQuery = "select Category, CustomerCode, CustomerDesc, "
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
            + "where Category<>27 and Category<>30 and Category<>50 "
            + "order by CustomerCode";
    //static String pastelQuery = "select Category, CustomerCode, CustomerDesc, "
    //        + "Ageing01, Ageing02, Ageing03, Ageing04, Ageing05 "
    //        + "from CustomerMaster"
    //        + " where Category<>27 and Category<>30 and Category<>50 order by CustomerCode";
    static String platinumDebtorQuery = "SELECT * FROM Debtors";
    static String controlsoftQuery = "SELECT * FROM Employee";
    
    static String platinumPriceQuery = 
    "SELECT prod.Product_Code as Code "
    +  ",[Short_Description] as Name "
    +  ",dep.Short_Name as Dept "
    +  ",[Unit_Size] as Size "
    +  ",[Unit_of_Measure] as UOM "
    +  ",[Touch_Item] "
    +  ",[Landed_Cost] as Cost "
    +  ",prod.Ave_Cost "
    +  ",[Selling_Price] as Price1 "
    +  ",price.Price2 "
    +  ",price.Price3 "
    +  ",price.Price4 "
    +  ",price.Price5 "
    +  ",price.Price6 "
    +  ",sum(sales.Qty) as QtySum "
 + "FROM [Platinum].[dbo].[Products] as prod "
 + "Inner Join [Platinum].[dbo].[Product_Prices] as price "
 + "on prod.Product_Code = price.Product_Code "
 + "Inner Join [Platinum].[dbo].[Departments] as dep "
 + "on prod.Department_No = dep.Department_No "
 + "left Join [Platinum].[dbo].[Sales_Journal] as sales "
 + "on prod.Product_Code = sales.Product_Code "
 + "where prod.Sales_Item = 1 "
 + "group by prod.Product_Code "
 + "      ,[Short_Description] "
 + "    ,dep.Short_Name "
 + "    ,[Unit_Size] "
 + "    ,[Unit_of_Measure] "
 + "    ,[Touch_Item] "
 + "    ,[Landed_Cost] "
 + "    ,prod.Ave_Cost "
 + "    ,[Selling_Price] "
 + "    ,price.Price2 "
 + "    ,price.Price3 "
 + "    ,price.Price4 "
 + "    ,price.Price5 "
 + "    ,price.Price6 ";
    
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
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
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
                FilteredRowSet controlsoftRowSet = new FilteredRowSetImpl()) {


            // Get pastel data
            Class.forName("com.pervasive.jdbc.v2.Driver");
            pastelRowSet.setUrl(properties.getProperty("pastelURL"));
            pastelRowSet.setCommand(pastelQuery);
            pastelRowSet.execute();

            // Get platinum data
            platinumDebtorRowSet.setUrl(properties.getProperty("platinumURL"));
            platinumDebtorRowSet.setCommand(platinumDebtorQuery);
            platinumDebtorRowSet.execute();
            
            platinumPriceRowSet.setUrl(properties.getProperty("platinumURL"));
            platinumPriceRowSet.setCommand(platinumPriceQuery);
            platinumPriceRowSet.execute();

            // Get controlsoft data
            controlsoftRowSet.setUrl(properties.getProperty("controlsoftURL"));
            controlsoftRowSet.setCommand(controlsoftQuery);
            controlsoftRowSet.execute();

            //Run the various validations according to the commandline arguments
            PlatinumValidator validator = new PlatinumValidator(pastelRowSet, platinumDebtorRowSet, 
                    controlsoftRowSet, platinumPriceRowSet,0.42, PastelMemberTags);
            if (cargs.ValidateDebtors) {
                results = validator.validateDebtorsByItself();
                results.addAll(validator.validateByPastel());
                results.addAll(validator.validateByControlsoft());
                results.addAll(validator.validatePriceLevel());
            } else if (cargs.ValidatePrices) {
                results = validator.validatePriceLists();
            } else if (cargs.ValidateAll) {
                results = validator.validateDebtorsByItself();
                results.addAll(validator.validateByPastel());
                results.addAll(validator.validateByControlsoft());
                results.addAll(validator.validatePriceLevel());
                results.addAll(validator.validatePriceLists());
            } else {
                results = validator.validateDebtorsByItself();
                results.addAll(validator.validateByPastel());
                results.addAll(validator.validateByControlsoft());
                results.addAll(validator.validatePriceLevel());
                results.addAll(validator.validatePriceLists());
            }

            Collections.sort(results);
            printSummary(results, validator.validationTypes);
            if (!cargs.PrintSummaryOnly) {
                printDetail(results);
            }

            System.out.println("Done...");

        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        if (results.isEmpty())   System.exit(1);
        else System.exit(0);
    }

    private static void exportDataToXML(String fileName, String URL, String query) {
        /*
         //Interpret command line parameters
         if (cargs.xml) {
         System.out.println("Exporting Pastel");
         exportDataToXML("pastelXml", properties.getProperty("pastelURL"), pastelQuery);

         String pxq = "select Blocked, "
         + "cast(Debtor_No as VARCHAR) as Debtor_No, "
         + "cast(Debtor_Name as VARCHAR) as Debtor_Name, "
         + "cast(Contact_Person as VARCHAR) as Contact_Person, "
         + "cast(Business_Tel as VARCHAR) as Business_Tel, "
         + "cast(Mobile as VARCHAR) as Mobile, "
         + "cast(Fax_Tel as VARCHAR) as Fax_Tel, "
         + "cast(Address as VARCHAR) as Address, "
         + "cast(E_Mail as VARCHAR) as E_Mail, "
         + "cast(Balance as VARCHAR) as Balance, "
         + "cast(SwipeCard as VARCHAR) as SwipeCard "
         + "from Debtors";

         //System.out.println("Exporting Platinum");
         //exportDataToXML("platinumXml", properties.getProperty("platinumURL"), pxq);

         String cxq = "select EmployeeID, Enabled, "
         + "cast(TagID as VARCHAR) as TagID, "
         + "cast(Number as VARCHAR) as Number, "
         + "cast(Title as VARCHAR) as Tile, "
         + "cast(FirstNames as VARCHAR) as FirstNames, "
         + "cast(Surname as VARCHAR) as Surname, "
         + "cast(IDNumber as VARCHAR) as IDNumber, "
         + "cast(Gender as VARCHAR) as Gender, "
         + "cast(Address1 as VARCHAR) as Address1, "
         + "cast(Address2 as VARCHAR) as Address2, "
         + "cast(Suburb as VARCHAR) as Suburb, "
         + "cast(City as VARCHAR) as City, "
         + "cast(Zip as VARCHAR) as Zip, "
         + "cast(HomeTel as VARCHAR) as HomeTel, "
         + "cast(WorkTel as VARCHAR) as WorkTel, "
         + "cast(CellTel as VARCHAR) as CellTel, "
         + "cast(OtherTel as VARCHAR) as OtherTel, "
         + "cast(Created as DATE) as Created "
         + "from Employee";

         //String cxq1 = "select top 10 cast(FirstNames as VARCHAR) as FirstNames from Employee";

         //System.out.println("Exporting Controlsoft");
         //exportDataToXML("controlsoftXml", properties.getProperty("controlsoftProURL"), cxq);
         System.exit(0);
         }

        
        
        
        
         System.out.println("Getting data ...");
         try (WebRowSet rowSet = new FilteredRowSetImpl();
         OutputStream xmlStream = new FileOutputStream(fileName)) {

         // Get data
         rowSet.setCommand(query);
         rowSet.setUrl(URL);
         rowSet.execute();

         //Save to xml file
         System.out.println("Saving data ...");
         rowSet.writeXml(xmlStream);
         System.out.println("Done...");



         } catch (Exception ex) {
         Logger.getLogger(ValidatePlatinum.class
         .getName()).log(Level.SEVERE, null, ex);
         System.exit(
         -1);
         }*/
    }

    private static void printSummary(List<ValidationItem> results, String[][] validationTypes) {

        Integer[] count = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        // ** Count **
        for (ValidationItem item : results) {
            count[item.itemTypeNumber]++;
        }

        // ** Print
        System.out.printf("ValidatePlatinum, build number: %s%n%n", version.getProperty("BUILD"));
        System.out.println("*** Validation type summary ***");
        for (Integer i = 1; i < 14; i++) {
            if (count[i] > 0) {
                System.out.printf("Validation type: %d = %d, (%s).\r\n", i, count[i], validationTypes[i][1]);
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
