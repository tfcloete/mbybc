/*
 */
package mb;

import com.sun.rowset.FilteredRowSetImpl;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.FilteredRowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.RowSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 *
 * @author Tertius Cloete
 */
public class DownloadPastelCustomers {

    static Properties properties;
    static Properties version;
    // ** Queries **
    static String pastelQuery = "select " 
        +"cust.Category, "   
        +"cust.CustomerCode, "		
        +"cust.CustomerDesc, "
        +"cust.BalanceThis01, "
        +"cust.BalanceThis02, "
        +"cust.BalanceThis03, "
        +"cust.BalanceThis04, "
        +"cust.BalanceThis05, "
        +"cust.BalanceThis06, "
        +"cust.BalanceThis07, "
        +"cust.BalanceThis08, "
        +"cust.BalanceThis09, "
        +"cust.BalanceThis10, "
        +"cust.BalanceThis11, "
        +"cust.BalanceThis12, "
        +"cust.BalanceThis13, "
        +"cust.BalanceLast01, "
        +"cust.BalanceLast02, "
        +"cust.BalanceLast03, "
        +"cust.BalanceLast04, "
        +"cust.BalanceLast05, "
        +"cust.BalanceLast06, "
        +"cust.BalanceLast07, "
        +"cust.BalanceLast08, "
        +"cust.BalanceLast09, "
        +"cust.BalanceLast10, "
        +"cust.BalanceLast11, "
        +"cust.BalanceLast12, "
        +"cust.BalanceLast13, "
        +"cust.SalesThis01, "
        +"cust.SalesThis02, "
        +"cust.SalesThis03, "
        +"cust.SalesThis04, "
        +"cust.SalesThis05, "
        +"cust.SalesThis06, "
        +"cust.SalesThis07, "
        +"cust.SalesThis08, "
        +"cust.SalesThis09, "
        +"cust.SalesThis10, "
        +"cust.SalesThis11, "
        +"cust.SalesThis12, "
        +"cust.SalesThis13, "
        +"cust.SalesLAst01, "
        +"cust.SalesLAst02, "
        +"cust.SalesLAst03, "
        +"cust.SalesLAst04, "
        +"cust.SalesLAst05, "
        +"cust.SalesLAst06, "
        +"cust.SalesLAst07, "
        +"cust.SalesLAst08, "
        +"cust.SalesLAst08, "
        +"cust.SalesLAst10, "
        +"cust.SalesLAst11, "
        +"cust.SalesLAst12, "
        +"cust.SalesLAst13, "
        +"cust.PostAddress01, "
        +"cust.PostAddress02, "
        +"cust.PostAddress03, "
        +"cust.PostAddress04, "
        +"cust.PostAddress05, "
        +"cust.TaxCode, "  
        +"cust.ExemptRef, "
        +"cust.SettlementTerms, "
        +"cust.PaymentTerms, "
        +"cust.Discount, "
        +"cust.LastCrDate, "
        +"cust.LastCrAmount, "
        +"cust.Blocked, "///
        +"cust.OpenItem, "
        +"cust.OverRideTax, "
        +"cust.MonthOrDay, "
        +"cust.IncExc, "
        +"cust.CountryCode, "
        +"cust.CurrencyCode, "
        +"cust.CreditLimit, "
        +"cust.InterestAfter, "
        +"cust.PriceRegime, "
        +"cust.UseAgedMessages, "
        +"cust.CurrBalanceThis01, "
        +"cust.CurrBalanceThis02, "
        +"cust.CurrBalanceThis03, "
        +"cust.CurrBalanceThis04, "
        +"cust.CurrBalanceThis05, "
        +"cust.CurrBalanceThis06, "
        +"cust.CurrBalanceThis07, "
        +"cust.CurrBalanceThis08, "
        +"cust.CurrBalanceThis09, "
        +"cust.CurrBalanceThis10, "
        +"cust.CurrBalanceThis11, "
        +"cust.CurrBalanceThis12, "
        +"cust.CurrBalanceThis13, "
        +"cust.CurrBalanceLast01, "
        +"cust.CurrBalanceLast02, "
        +"cust.CurrBalanceLast03, "
        +"cust.CurrBalanceLast04, "
        +"cust.CurrBalanceLast05, "
        +"cust.CurrBalanceLast06, "
        +"cust.CurrBalanceLast07, "
        +"cust.CurrBalanceLast08, "
        +"cust.CurrBalanceLast09, "
        +"cust.CurrBalanceLast10, "
        +"cust.CurrBalanceLast11, "
        +"cust.CurrBalanceLast12, "
        +"cust.CurrBalanceLast13, "
        +"cust.UserDefined01, "
        +"cust.UserDefined02, "
        +"cust.UserDefined03, "
        +"cust.UserDefined04, "
        +"cust.UserDefined05, "
        +"cust.Ageing01, "
        +"cust.Ageing02, "
        +"cust.Ageing03, "
        +"cust.Ageing04, "
        +"cust.Ageing05, "
        +"cust.StatPrintorEmail, "
        +"cust.DocPrintorEmail, "
        +"cust.InterestPer, "
        +"cust.Freight01, "
        +"cust.Ship, "
        //+"cust.Password, "
        +"cust.LinkWeb, "
        +"cust.LoyaltyProg, "
        +"cust.LCardNumber, "
        +"cust.UpdatedOn, "
        +"cust.CashAccount, "
        +"cust.AcceptMail, "
        +"cust.CreateDate, "
        +"cust.SoleProprietor, "
        +"cust.CustName, "
        +"cust.CustSurname, "
        +"cust.CustID, "
        +"cust.BankName, "
        +"cust.BankType, "
        +"cust.BankBranch, "
        +"cust.BankAccNumber, "
        //+"cust.GUID, "
        +"cust.BankAccRelation, "
        +"cust.ThirdPartyID, "
        +"cust.PassportNumber, "
        +"cat.CCDesc, "
        +"del.Contact, "
        +"del.Telephone, "
        +"del.Cell, "
        +"del.Fax, "
        +"del.Email "
            + "from CustomerMaster as cust "
            + "inner join CustomerCategories as cat "
            + "on cust.Category = cat.CCCode "
            + "inner join DeliveryAddresses as del "
            + "on cust.CustomerCode = del.CustomerCode ";
            //+ "where Category=1 ";
            //+ "where Category<>27 and Category<>30 "
            //+ "and Category<>50 ";

    //static String platinumDebtorQuery = "SELECT * FROM Debtors";
    //static String controlsoftQuery = "SELECT * FROM Employee";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /******************************************
         ** Get properties from mbybc.properties **
         ******************************************/
        properties = new Properties();
        version = new Properties();
        List<String> PastelMemberTags = new ArrayList<String>();
        try {
            properties.load(new FileInputStream("mbybc.properties"));
            //version.load(new FileInputStream("src/version.properties"));
        } catch (Exception ex) {
            Logger.getLogger(DownloadPastelCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        /***********************************
         ** Run query and build json file **
         ***********************************/
        List<ValidationItem> results = null;
        try (FilteredRowSet pastelRowSet = new FilteredRowSetImpl()) {

            // Get pastel data
            Class.forName("com.pervasive.jdbc.v2.Driver");
            pastelRowSet.setUrl(properties.getProperty("pastelURL"));
            pastelRowSet.setCommand(pastelQuery);
            //System.out.println("Getting Pastel rows ...");
            pastelRowSet.execute();
            pastelRowSet.last();
            int count = pastelRowSet.getRow();
            pastelRowSet.beforeFirst();
            //System.out.printf("Got: %d rows\n", count);
            
            // Create json
            //System.out.println("Writing json ...");
            json(pastelRowSet);
                        
            //System.out.println("Done...");

        } catch (Exception ex) {
            Logger.getLogger(DownloadPastelCustomers.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    public static void json(FilteredRowSet pastelRowSet)
    {
        try {
            JSONObject customers = new JSONObject();
            JSONArray list = new JSONArray();
            pastelRowSet.beforeFirst();
            while (pastelRowSet.next()) {
                JSONObject cust = new JSONObject();
                //cust.put("Category", pastelRowSet.getString("Category"));
                //cust.put("CustomerCode", pastelRowSet.getString("CustomerCode"));
                //cust.put("CustomerDesc", pastelRowSet.getString("CustomerDesc"));
                
//******* This was built by the .py script *****                
cust.put("Category", pastelRowSet.getString("Category"));
cust.put("CustomerCode", pastelRowSet.getString("CustomerCode"));
cust.put("CustomerDesc", pastelRowSet.getString("CustomerDesc"));
cust.put("BalanceThis01", pastelRowSet.getString("BalanceThis01"));
cust.put("BalanceThis02", pastelRowSet.getString("BalanceThis02"));
cust.put("BalanceThis03", pastelRowSet.getString("BalanceThis03"));
cust.put("BalanceThis04", pastelRowSet.getString("BalanceThis04"));
cust.put("BalanceThis05", pastelRowSet.getString("BalanceThis05"));
cust.put("BalanceThis06", pastelRowSet.getString("BalanceThis06"));
cust.put("BalanceThis07", pastelRowSet.getString("BalanceThis07"));
cust.put("BalanceThis08", pastelRowSet.getString("BalanceThis08"));
cust.put("BalanceThis09", pastelRowSet.getString("BalanceThis09"));
cust.put("BalanceThis10", pastelRowSet.getString("BalanceThis10"));
cust.put("BalanceThis11", pastelRowSet.getString("BalanceThis11"));
cust.put("BalanceThis12", pastelRowSet.getString("BalanceThis12"));
cust.put("BalanceThis13", pastelRowSet.getString("BalanceThis13"));
cust.put("BalanceLast01", pastelRowSet.getString("BalanceLast01"));
cust.put("BalanceLast02", pastelRowSet.getString("BalanceLast02"));
cust.put("BalanceLast03", pastelRowSet.getString("BalanceLast03"));
cust.put("BalanceLast04", pastelRowSet.getString("BalanceLast04"));
cust.put("BalanceLast05", pastelRowSet.getString("BalanceLast05"));
cust.put("BalanceLast06", pastelRowSet.getString("BalanceLast06"));
cust.put("BalanceLast07", pastelRowSet.getString("BalanceLast07"));
cust.put("BalanceLast08", pastelRowSet.getString("BalanceLast08"));
cust.put("BalanceLast09", pastelRowSet.getString("BalanceLast09"));
cust.put("BalanceLast10", pastelRowSet.getString("BalanceLast10"));
cust.put("BalanceLast11", pastelRowSet.getString("BalanceLast11"));
cust.put("BalanceLast12", pastelRowSet.getString("BalanceLast12"));
cust.put("BalanceLast13", pastelRowSet.getString("BalanceLast13"));
cust.put("SalesThis01", pastelRowSet.getString("SalesThis01"));
cust.put("SalesThis02", pastelRowSet.getString("SalesThis02"));
cust.put("SalesThis03", pastelRowSet.getString("SalesThis03"));
cust.put("SalesThis04", pastelRowSet.getString("SalesThis04"));
cust.put("SalesThis05", pastelRowSet.getString("SalesThis05"));
cust.put("SalesThis06", pastelRowSet.getString("SalesThis06"));
cust.put("SalesThis07", pastelRowSet.getString("SalesThis07"));
cust.put("SalesThis08", pastelRowSet.getString("SalesThis08"));
cust.put("SalesThis09", pastelRowSet.getString("SalesThis09"));
cust.put("SalesThis10", pastelRowSet.getString("SalesThis10"));
cust.put("SalesThis11", pastelRowSet.getString("SalesThis11"));
cust.put("SalesThis12", pastelRowSet.getString("SalesThis12"));
cust.put("SalesThis13", pastelRowSet.getString("SalesThis13"));
cust.put("SalesLAst01", pastelRowSet.getString("SalesLAst01"));
cust.put("SalesLAst02", pastelRowSet.getString("SalesLAst02"));
cust.put("SalesLAst03", pastelRowSet.getString("SalesLAst03"));
cust.put("SalesLAst04", pastelRowSet.getString("SalesLAst04"));
cust.put("SalesLAst05", pastelRowSet.getString("SalesLAst05"));
cust.put("SalesLAst06", pastelRowSet.getString("SalesLAst06"));
cust.put("SalesLAst07", pastelRowSet.getString("SalesLAst07"));
cust.put("SalesLAst08", pastelRowSet.getString("SalesLAst08"));
cust.put("SalesLAst08", pastelRowSet.getString("SalesLAst08"));
cust.put("SalesLAst10", pastelRowSet.getString("SalesLAst10"));
cust.put("SalesLAst11", pastelRowSet.getString("SalesLAst11"));
cust.put("SalesLAst12", pastelRowSet.getString("SalesLAst12"));
cust.put("SalesLAst13", pastelRowSet.getString("SalesLAst13"));
cust.put("PostAddress01", pastelRowSet.getString("PostAddress01"));
cust.put("PostAddress02", pastelRowSet.getString("PostAddress02"));
cust.put("PostAddress03", pastelRowSet.getString("PostAddress03"));
cust.put("PostAddress04", pastelRowSet.getString("PostAddress04"));
cust.put("PostAddress05", pastelRowSet.getString("PostAddress05"));
cust.put("TaxCode", pastelRowSet.getString("TaxCode"));
cust.put("ExemptRef", pastelRowSet.getString("ExemptRef"));
cust.put("SettlementTerms", pastelRowSet.getString("SettlementTerms"));
cust.put("PaymentTerms", pastelRowSet.getString("PaymentTerms"));
cust.put("Discount", pastelRowSet.getString("Discount"));
cust.put("LastCrDate", pastelRowSet.getString("LastCrDate"));
cust.put("LastCrAmount", pastelRowSet.getString("LastCrAmount"));
cust.put("Blocked", pastelRowSet.getString("Blocked"));
cust.put("OpenItem", pastelRowSet.getString("OpenItem"));
cust.put("OverRideTax", pastelRowSet.getString("OverRideTax"));
cust.put("MonthOrDay", pastelRowSet.getString("MonthOrDay"));
cust.put("IncExc", pastelRowSet.getString("IncExc"));
cust.put("CountryCode", pastelRowSet.getString("CountryCode"));
cust.put("CurrencyCode", pastelRowSet.getString("CurrencyCode"));
cust.put("CreditLimit", pastelRowSet.getString("CreditLimit"));
cust.put("InterestAfter", pastelRowSet.getString("InterestAfter"));
cust.put("PriceRegime", pastelRowSet.getString("PriceRegime"));
cust.put("UseAgedMessages", pastelRowSet.getString("UseAgedMessages"));
cust.put("CurrBalanceThis01", pastelRowSet.getString("CurrBalanceThis01"));
cust.put("CurrBalanceThis02", pastelRowSet.getString("CurrBalanceThis02"));
cust.put("CurrBalanceThis03", pastelRowSet.getString("CurrBalanceThis03"));
cust.put("CurrBalanceThis04", pastelRowSet.getString("CurrBalanceThis04"));
cust.put("CurrBalanceThis05", pastelRowSet.getString("CurrBalanceThis05"));
cust.put("CurrBalanceThis06", pastelRowSet.getString("CurrBalanceThis06"));
cust.put("CurrBalanceThis07", pastelRowSet.getString("CurrBalanceThis07"));
cust.put("CurrBalanceThis08", pastelRowSet.getString("CurrBalanceThis08"));
cust.put("CurrBalanceThis09", pastelRowSet.getString("CurrBalanceThis09"));
cust.put("CurrBalanceThis10", pastelRowSet.getString("CurrBalanceThis10"));
cust.put("CurrBalanceThis11", pastelRowSet.getString("CurrBalanceThis11"));
cust.put("CurrBalanceThis12", pastelRowSet.getString("CurrBalanceThis12"));
cust.put("CurrBalanceThis13", pastelRowSet.getString("CurrBalanceThis13"));
cust.put("CurrBalanceLast01", pastelRowSet.getString("CurrBalanceLast01"));
cust.put("CurrBalanceLast02", pastelRowSet.getString("CurrBalanceLast02"));
cust.put("CurrBalanceLast03", pastelRowSet.getString("CurrBalanceLast03"));
cust.put("CurrBalanceLast04", pastelRowSet.getString("CurrBalanceLast04"));
cust.put("CurrBalanceLast05", pastelRowSet.getString("CurrBalanceLast05"));
cust.put("CurrBalanceLast06", pastelRowSet.getString("CurrBalanceLast06"));
cust.put("CurrBalanceLast07", pastelRowSet.getString("CurrBalanceLast07"));
cust.put("CurrBalanceLast08", pastelRowSet.getString("CurrBalanceLast08"));
cust.put("CurrBalanceLast09", pastelRowSet.getString("CurrBalanceLast09"));
cust.put("CurrBalanceLast10", pastelRowSet.getString("CurrBalanceLast10"));
cust.put("CurrBalanceLast11", pastelRowSet.getString("CurrBalanceLast11"));
cust.put("CurrBalanceLast12", pastelRowSet.getString("CurrBalanceLast12"));
cust.put("CurrBalanceLast13", pastelRowSet.getString("CurrBalanceLast13"));
cust.put("UserDefined01", pastelRowSet.getString("UserDefined01"));
cust.put("UserDefined02", pastelRowSet.getString("UserDefined02"));
cust.put("UserDefined03", pastelRowSet.getString("UserDefined03"));
cust.put("UserDefined04", pastelRowSet.getString("UserDefined04"));
cust.put("UserDefined05", pastelRowSet.getString("UserDefined05"));
cust.put("Ageing01", pastelRowSet.getString("Ageing01"));
cust.put("Ageing02", pastelRowSet.getString("Ageing02"));
cust.put("Ageing03", pastelRowSet.getString("Ageing03"));
cust.put("Ageing04", pastelRowSet.getString("Ageing04"));
cust.put("Ageing05", pastelRowSet.getString("Ageing05"));
cust.put("StatPrintorEmail", pastelRowSet.getString("StatPrintorEmail"));
cust.put("DocPrintorEmail", pastelRowSet.getString("DocPrintorEmail"));
cust.put("InterestPer", pastelRowSet.getString("InterestPer"));
cust.put("Freight01", pastelRowSet.getString("Freight01"));
cust.put("Ship", pastelRowSet.getString("Ship"));
cust.put("LinkWeb", pastelRowSet.getString("LinkWeb"));
cust.put("LoyaltyProg", pastelRowSet.getString("LoyaltyProg"));
cust.put("LCardNumber", pastelRowSet.getString("LCardNumber"));
cust.put("UpdatedOn", pastelRowSet.getString("UpdatedOn"));
cust.put("CashAccount", pastelRowSet.getString("CashAccount"));
cust.put("AcceptMail", pastelRowSet.getString("AcceptMail"));
cust.put("CreateDate", pastelRowSet.getString("CreateDate"));
cust.put("SoleProprietor", pastelRowSet.getString("SoleProprietor"));
cust.put("CustName", pastelRowSet.getString("CustName"));
cust.put("CustSurname", pastelRowSet.getString("CustSurname"));
cust.put("CustID", pastelRowSet.getString("CustID"));
cust.put("BankName", pastelRowSet.getString("BankName"));
cust.put("BankType", pastelRowSet.getString("BankType"));
cust.put("BankBranch", pastelRowSet.getString("BankBranch"));
cust.put("BankAccNumber", pastelRowSet.getString("BankAccNumber"));
cust.put("BankAccRelation", pastelRowSet.getString("BankAccRelation"));
cust.put("ThirdPartyID", pastelRowSet.getString("ThirdPartyID"));
cust.put("PassportNumber", pastelRowSet.getString("PassportNumber"));
cust.put("CCDesc", pastelRowSet.getString("CCDesc"));
                //(fields **** .py script ****)
cust.put("Contact", pastelRowSet.getString("Contact"));
cust.put("Telephone", pastelRowSet.getString("Telephone"));
cust.put("Cell", pastelRowSet.getString("Cell"));
cust.put("Fax", pastelRowSet.getString("Fax"));
cust.put("Email", pastelRowSet.getString("Email"));
                list.add(cust);
            }
            customers.put("list", list);
            System.out.print(customers.toJSONString());

            //FileWriter file = new FileWriter("customers.json");
            //file.write(customers.toJSONString());
            //file.flush();
            //file.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }    
    
}
