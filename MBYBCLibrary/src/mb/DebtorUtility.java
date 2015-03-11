/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import com.sun.rowset.FilteredRowSetImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;

/**
 *
 * @author tertius
 */
public class DebtorUtility {
    
    private int currentPeriod;
    private double currentBalance;
    String sql =
            "UPDATE dbo.Employee "
            + "set Enabled = ? "
            + "where EmployeeID = ?";
    Connection conn;
    PreparedStatement pstmt;
    
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
            + "where Category<>27 and Category<>30 and Category<>50 "
            + "order by CustomerCode";
    
    static String controlsoftQuery = "SELECT * FROM Employee";
    
    private FilteredRowSet pastelRowSet;
    private RowSet controlsoftRowSet;


    public DebtorUtility(int currentPeriod, FilteredRowSet pastelRowSet,
                            RowSet controlsoftRowSet){

        this.currentPeriod = currentPeriod;
        this.pastelRowSet = pastelRowSet;
        this.controlsoftRowSet = controlsoftRowSet;
        
        //Open connection used for updating Controlsoft
        try {

//            conn = DriverManager.getConnection("jdbc:sqlserver://10.0.0.55:1082;databaseName=Tertius;user=mbybc;password=mbybc");
            conn = DriverManager.getConnection("jdbc:sqlserver://10.0.0.55:1082;databaseName=Controlsoftpro;user=mbybc;password=mbybc");
            pstmt = conn.prepareStatement(sql);
        } catch (Exception ex) {
            Logger.getLogger(DebtorUtility.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }


        
    }
   
    boolean IsBadDebtor(){
        
        try {
            
            //If a member has a debit order he/she is not a bad debtor
            String userdefined01 = pastelRowSet.getString("UserDefined01").trim();
            if(userdefined01.contains("DebitOrder")) return false;
            
            //Calculate period totals
            double sixtyDays = 0;
            for (int p = 1; p < currentPeriod + 10; p++) 
                sixtyDays += pastelRowSet.getDouble(String.format("p%d", p));
            GetCurrentBalance();
            
            //If current and sixty days exceed 1000, the member is a bad debtor
            if(currentBalance >= 1000 && sixtyDays >= 1000)
                return true;
            
            return false;
            
        } catch (Exception ex) {
            Logger.getLogger(DebtorUtility.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
       
        return true;
    }
    
    double GetCurrentBalance(){
        try {

            currentBalance = 0;
            for (int p = 1; p <= currentPeriod + 12; p++) 
                currentBalance += pastelRowSet.getDouble(String.format("p%d", p));
            
        } catch (Exception ex) {
            Logger.getLogger(DebtorUtility.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        return currentBalance;
    }
    
    void DisableEmplooyee(){
        try {
            
            int employeeID = controlsoftRowSet.getInt("EmployeeID");
            
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, employeeID);
            //pstmt.executeUpdate();

        } catch (Exception ex) {
            Logger.getLogger(DebtorUtility.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
   
}
