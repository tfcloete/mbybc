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
import com.sun.rowset.WebRowSetImpl;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.RowSet;
import sun.org.mozilla.javascript.json.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 


/**
 *
 * @author PLATINUM
 */
public class DownloadPastelCustomersTEMPLATE {

    static Properties properties;
    static Properties version;
    static int currentPeriod;
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
        +"cat.CCDesc "
            + "from CustomerMaster as cust "
            + "inner join CustomerCategories as cat "
            + "on cust.Category = cat.CCCode "
            + "where Category=1 ";
            //+ "where Category<>27 and Category<>30 "
            //+ "and Category<>50 ";

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
            Logger.getLogger(DownloadPastelCustomersTEMPLATE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        /**
         * *********************
         ** Manage Validation **
         */
        List<ValidationItem> results = null;
        try (FilteredRowSet pastelRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumDebtorRowSet = new FilteredRowSetImpl();
                FilteredRowSet platinumPriceRowSet = new FilteredRowSetImpl();
                RowSet controlsoftRowSet = new FilteredRowSetImpl()) {

            //

            // Get pastel data
            Class.forName("com.pervasive.jdbc.v2.Driver");
            pastelRowSet.setUrl(properties.getProperty("pastelURL"));
            pastelRowSet.setCommand(pastelQuery);
            System.out.println("Getting Pastel rows ...");
            pastelRowSet.execute();
            pastelRowSet.last();
            int count = pastelRowSet.getRow();
            pastelRowSet.beforeFirst();
            System.out.printf("Got: %d rows\n", count);
            System.out.println("Writing XML ...");
            //FileOutputStream f=new FileOutputStream("pc.xml");
            //OutputStreamWriter osw = new OutputStreamWriter(f,Charset.forName("UTF-8"));
            //FileWriter fr = new FileWriter("pc.xml");
            //PrintWriter out = new PrintWriter(new File("pc.xml"), "UTF-8");
            
            //Writer out = new BufferedWriter(new OutputStreamWriter(
            //    new FileOutputStream("pc.xml"), "UTF-16"));
            //FileOutputStream out = new FileOutputStream("pc.xml");
            //pastelRowSet.writeXml(out);
            //out.flush();
            //out.close();
            
            // Create jsofile
            json(pastelRowSet);
            
            
            // Get platinum data
            //platinumDebtorRowSet.setUrl(properties.getProperty("platinumURL"));
            //platinumDebtorRowSet.setCommand(platinumDebtorQuery);
            //platinumDebtorRowSet.execute();
            
            // Get controlsoft data
            //controlsoftRowSet.setUrl(properties.getProperty("controlsoftURL"));
            //controlsoftRowSet.setCommand(controlsoftQuery);
            //controlsoftRowSet.execute();
                        
            System.out.println("Done...");

        } catch (Exception ex) {
            Logger.getLogger(DownloadPastelCustomersTEMPLATE.class.getName()).log(Level.SEVERE, null, ex);
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
                //(fields) ***** This is where the .py scrip will insert the fields ****
                list.add(cust);
            }
            customers.put("list", list);
            FileWriter file = new FileWriter("customers.json");
            file.write(customers.toJSONString());
            file.flush();
            file.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }    
    
    public static void jsontest()
    {
    	JSONObject obj = new JSONObject();
	obj.put("name", "mkyong.com");
	obj.put("age", new Integer(100));
 
	JSONArray list = new JSONArray();
	list.add("msg 1");
	list.add("msg 2");
	list.add("msg 3");
 
	obj.put("messages", list);
 
	try {
 
		FileWriter file = new FileWriter("test.json");
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
 
	System.out.print(obj);
 
     }
}
