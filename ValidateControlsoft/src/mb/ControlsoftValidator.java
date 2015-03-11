/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;

/**
 *
 * @author Tertius
 */
public class ControlsoftValidator {

    private FilteredRowSet pastelCustomers;
    private RowSet controlsoftEmployees;
    private List<String> PastelMemberTags;
    //private int currentPeriod;
    //private int currentBalance;
    private DebtorUtility debtorUtility;
    
    public String[][] validationTypes = {
        {"not used", "Zero - not used"}, //0
        {"Employee:, %s, %s, Member:, , has no member number in ControlSoft.%n", "No member number in Controlsoft"}, //1
        {"Employee:, %s, %s, Member:, %s, has no swipe card in Controlsoft.%n", "No swipe card in Controlsoft"}, //2
        {"Employee:, %s, %s, Member:, %s, is not a member in good standing and should be blocked in Controlsoft.%n", "Not a member in good standing"}, //3
        {"Employee:, %s, %s, Member:, %s, has a name that is inconsistent with Pastel (%s).%n", "Name inconsistent with Pastel"}, //4
        {"Not used", "Not used"},//5
        {"Not used", "Not used"},//6
        {"Not used", "Not used"},//7
        {"Not used", "Not used"},//8
        {"Not used", "Not used"},//9
        {"Not used", "Not used"},//10
        {"Not used", "Not used"},//11
        {"Not used", "Not used"},//12
        {"Blocked Employee:, %s, %s, Member:, %s, is a member in good standing and should be unblocked.%n", "Blocked member}"},//13
        {"Employee:, %s, %s, Member:, %s, is overdue, R%.2f, and should be blocked in Controlsoft.%n", "Overdue debtor"} //14
    };

    
    public ControlsoftValidator(FilteredRowSet pastelCustomers, 
            RowSet controlsoftEmployees,
            List<String> PastelMemberTags,
            int currentPeriod) {

        this.pastelCustomers = pastelCustomers;
        this.controlsoftEmployees = controlsoftEmployees;
        this.PastelMemberTags = PastelMemberTags;
        //this.currentPeriod = currentPeriod;
        debtorUtility = new DebtorUtility(currentPeriod, pastelCustomers, controlsoftEmployees);
    }

    public List<ValidationItem> validateEmployeeByItself() {

        String validationMessage;
        List<ValidationItem> validationResult = new ArrayList<ValidationItem>();

        try {
            controlsoftEmployees.beforeFirst();
            while (controlsoftEmployees.next()) {
                if (controlsoftEmployees.getBoolean("Enabled")) {

                    // *** 1, No member number ***
                    if (controlsoftEmployees.getString("Number").trim().isEmpty()) {

                        validationMessage = String.format(validationTypes[1][0],
                                controlsoftEmployees.getString("EmployeeID"),
                                controlsoftEmployees.getString("Surname"));

                        validationResult.add(new ValidationItem(1, validationMessage));
                    }
                    
                    // *** 2, No Swipe card ***
/*                    if (platinumDebtors.getString("SwipeCard").trim().isEmpty()) {

                        validationMessage = String.format(validationTypes[2][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"),
                                platinumDebtors.getString("Contact_Person").trim());

                        validationResult.add(new ValidationItem(2, validationMessage));
                    }*/
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidateControlsoft.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }

    
        
    public List<ValidationItem> validateByPastel() {

        MemberFilter pastelFilter = new MemberFilter("CustomerCode", null);
        String validationMessage;
        List<ValidationItem> validationResult = new ArrayList<ValidationItem>();

        try {
            pastelCustomers.setFilter(pastelFilter);
            controlsoftEmployees.beforeFirst();
            while (controlsoftEmployees.next()) {
                if (controlsoftEmployees.getBoolean("Enabled")) {

                    //Don't evaluate records without member number or swipe card. 
                    //leave to evaluateByItself.
                    boolean hasNoMemberNo = controlsoftEmployees.getString("Number").trim().isEmpty();
                    if (hasNoMemberNo) {
                        continue;
                    }

                    // *** 3, If the member is not in Pastel he/she is not in good standing
                    pastelFilter.memberNumber = controlsoftEmployees.getString("Number");
                    if (!pastelCustomers.first()) {

                        validationMessage = String.format(validationTypes[3][0],
                                controlsoftEmployees.getString("EmployeeID"),
                                controlsoftEmployees.getString("Number"),
                                controlsoftEmployees.getString("Surname"));

                        validationResult.add(new ValidationItem(3, validationMessage));

                    } else {

                        String x = controlsoftEmployees.getString("Surname").split(",")[0].trim();
                        pastelCustomers.beforeFirst();
                        while (pastelCustomers.next()) {

                            //*** 14, If a member's 60-day debd exceeds R1000-00 he/she is not in good standing
                            if (debtorUtility.IsBadDebtor()) {

                                validationMessage = String.format(validationTypes[14][0],
                                        controlsoftEmployees.getString("EmployeeID"),
                                        controlsoftEmployees.getString("Number"),
                                        controlsoftEmployees.getString("Surname"),
                                        debtorUtility.GetCurrentBalance());

                                validationResult.add(new ValidationItem(14, validationMessage));
                                
                                debtorUtility.DisableEmplooyee();

                            }

                            //*** 4, If the first part (up to the first space) of the names do not correspond,
                            //There could be a missmatch in the member name. ***
                            String y = pastelCustomers.getString("CustomerDesc").split(",")[0].trim();
                            if ((x.isEmpty()) || (y.isEmpty())
                                    || !x.split(" ")[0].trim().contentEquals(y.split(" ")[0].trim())) {

                                validationMessage = String.format(validationTypes[4][0],
                                        controlsoftEmployees.getString("EmployeeID"),
                                        controlsoftEmployees.getString("Number"),
                                        controlsoftEmployees.getString("Surname"),
                                        pastelCustomers.getString("CustomerDesc").trim());

                                validationResult.add(new ValidationItem(4, validationMessage));
                            }
                        }
                    }
                }
                //Check for members that should NOT be blocked
                else{
                    // 13, *** If the member is in Pastel and is not a bad debtor he/she is in good standing
                    pastelFilter.memberNumber = controlsoftEmployees.getString("Number");
                    if (pastelCustomers.first()) {
                        if (!debtorUtility.IsBadDebtor()) {
                            validationMessage = String.format(validationTypes[13][0],
                                    controlsoftEmployees.getString("EmployeeID"),
                                    controlsoftEmployees.getString("Number"),
                                    controlsoftEmployees.getString("Surname"));

                            validationResult.add(new ValidationItem(13, validationMessage));
                        }
                    }
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidateControlsoft.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }
    
    /*
    boolean isBadDebtor(){
        
        try {
            //Calculate period totals
            double sixtyDays = 0;
            for (int p = 1; p < currentPeriod + 10; p++) 
                sixtyDays += pastelCustomers.getDouble(String.format("p%d", p));
            currentBalance = 0;
            for (int p = 1; p < currentPeriod + 12; p++) 
                currentBalance += pastelCustomers.getDouble(String.format("p%d", p));
            
            //If current and sixty days exceed 1000, the member is a bad debtor
            if(currentBalance >= 1000 && sixtyDays >= 1000)
                return true;
            
            return false;
            
        } catch (Exception ex) {
            Logger.getLogger(ValidateControlsoft.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
       
        return true;
    }
   */
}
