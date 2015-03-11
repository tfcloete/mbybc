/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.FilteredRowSet;

/**
 *
 * @author Tertius
 */
public class PlatinumValidator {

    private FilteredRowSet pastelCustomers;
    private FilteredRowSet platinumDebtors;
    private FilteredRowSet controlsoftEmployees;
    private FilteredRowSet platinumPrices;
    private double minimumGP;
    private List<String> PastelMemberTags;
    
    public String[][] validationTypes = {
        {"not used", "Zero - not used"}, //0
        {"Debtor:, %s, %s, Member:, , has no member number in Platinum.%n", "No member number in Platinum"}, //1
        {"Debtor:, %s, %s, Member:, %s, has no swipe card in Platinum.%n", "No swipe card in Platinum"}, //2
        {"Debtor:, %s, %s, Member:, %s, is not a member in good standing and should be blocked.%n", "Not a member in good standing"}, //3
        {"Debtor:, %s, %s, Member:, %s, has a name that is inconsistent with Pastel (%s).%n", "Name inconsistent with Pastel"}, //4
        {"Debtor:, %s, %s, Member:, %s, has a swipe card (%s) that does not exist in the Controlsoft database.%n", "Swipecard inconsistent with Controlsoft"},//5
        {"Debtor:, %s, %s, Member:, %s, has a Member Number that is inconsistent with Controlsoft (%s) (Swipe card:%s).%n", "Number inconsistent with Controlsoft"},//6
        {"Debtor:, %s, %s, Member:, %s, Should not be on Price Level 1.%n", "Should not be on price leve 1"},//7
        {"Debtor:, %s, %s, Member:, %s, Should be on Price Level 1 (R%.2f).%n", "Should be on price level 1"},//8
        {"Debtor:, %s, %s, Member:, %s, Should be on Price Level 3 (R%.2f).%n", "Should be on price level 3"},//9
        {"Platinum Price List %d, for item %s (%s) may not be 0.%n", "No price"},//10
        {"Platinum Price List 1, for item %s (%s) is below the minimum GP of %.0f%% (%.1f%%).%n", "Price 1 below margin"},//11
        {"Platinum Price List 3, for item %s (%s) is above the maximum discount of 20%%. (%.1f%%).%n", "Price 3 high discount"},//12
        {"Blocked Debtor:, %s, %s, Member:, %s, is a member in good standing and should unblocked.%n", "Blocked member}"}//13
    };

    public PlatinumValidator(FilteredRowSet pastelCustomers, 
            FilteredRowSet platinumDebtors, 
            FilteredRowSet controlsoftEmployees,
            FilteredRowSet platinumProducts,
            double minimumGP,
            List<String> PastelMemberTags) {

        this.pastelCustomers = pastelCustomers;
        this.platinumDebtors = platinumDebtors;
        this.controlsoftEmployees = controlsoftEmployees;
        this.platinumPrices = platinumProducts;
        this.minimumGP = minimumGP;
        this.PastelMemberTags = PastelMemberTags;
    }

    public List<ValidationItem> validateDebtorsByItself() {

        String validationMessage;
        List<ValidationItem> validationResult = new ArrayList<ValidationItem>();

        try {
            platinumDebtors.beforeFirst();
            while (platinumDebtors.next()) {
                if (!platinumDebtors.getBoolean("Blocked")) {

                    // *** 1, No member number ***
                    if (platinumDebtors.getString("Contact_Person").trim().isEmpty()) {

                        validationMessage = String.format(validationTypes[1][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"));

                        validationResult.add(new ValidationItem(1, validationMessage));
                    }
                    
                    // *** 2, No Swipe card ***
                    if (platinumDebtors.getString("SwipeCard").trim().isEmpty()) {

                        validationMessage = String.format(validationTypes[2][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"),
                                platinumDebtors.getString("Contact_Person").trim());

                        validationResult.add(new ValidationItem(2, validationMessage));
                    }
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
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
            platinumDebtors.beforeFirst();
            while (platinumDebtors.next()) {
                if (!platinumDebtors.getBoolean("Blocked")) {

                    //Don't evaluate reords without member number or swipe card. 
                    //leave to evaluateByItself.
                    boolean hasNoMemberNo = platinumDebtors.getString("Contact_Person").trim().isEmpty();
                    if (hasNoMemberNo) {
                        continue;
                    }

                    // *** 3, If the member is not in Pastel he/she is not in good standing
                    pastelFilter.memberNumber = platinumDebtors.getString("Contact_Person");
                    if (!pastelCustomers.first()) {

                        validationMessage = String.format(validationTypes[3][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"),
                                platinumDebtors.getString("Contact_Person"));

                        validationResult.add(new ValidationItem(3, validationMessage));

                    } else {

                        String x = platinumDebtors.getString("Debtor_Name").split(",")[0].trim();
                        pastelCustomers.beforeFirst();
                        while (pastelCustomers.next()) {

                            //*** 4, If the first part (up to the first space) of the names do not correspond,
                            //There could be a missmatch in the member name. ***
                            String y = pastelCustomers.getString("CustomerDesc").split(",")[0].trim();
                            if ((x.isEmpty()) || (y.isEmpty())
                                    || !x.split(" ")[0].trim().contentEquals(y.split(" ")[0].trim())) {

                                validationMessage = String.format(validationTypes[4][0],
                                        platinumDebtors.getString("Debtor_No"),
                                        platinumDebtors.getString("Debtor_Name"),
                                        platinumDebtors.getString("Contact_Person"),
                                        pastelCustomers.getString("CustomerDesc").trim());

                                validationResult.add(new ValidationItem(4, validationMessage));
                            }
                        }
                    }
                }
                //Check for members that should NOT be blocked
                else{
                    // 13, *** If the member is in Pastel he/she is in good standing
                    pastelFilter.memberNumber = platinumDebtors.getString("Contact_Person");
                    if (pastelCustomers.first()) {

                        validationMessage = String.format(validationTypes[13][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"),
                                platinumDebtors.getString("Contact_Person"));

                        validationResult.add(new ValidationItem(13, validationMessage));
                    }
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }

    public List<ValidationItem> validateByControlsoft() {

        SwipeCardFilter controlsoftFilter = new SwipeCardFilter("TagID", null);
        String validationMessage;
        List<ValidationItem> validationResult = new ArrayList<ValidationItem>();

        try {
            //FilteredRowSet pastel = new FilteredRowSetImpl();
            //pastel.populate(pastelRS);
            //FilterByMemberNumber pastelFilter = new FilterByMemberNumber("CustomerCode", null);

            controlsoftEmployees.setFilter(controlsoftFilter);
            platinumDebtors.beforeFirst();
            while (platinumDebtors.next()) {
                if (!platinumDebtors.getBoolean("Blocked")) {

                    //Don't evaluate reords without member number or swipe card. 
                    //leave to evaluateByItself.
                    boolean hasNoMemberNo = platinumDebtors.getString("Contact_Person").trim().isEmpty();
                    boolean hasNoSwipeCard = platinumDebtors.getString("SwipeCard").trim().isEmpty();
                    if (hasNoMemberNo || hasNoSwipeCard) {
                        continue;
                    }

                    //*** 5, If the swipe card is not found in controlsoft. ***
                    controlsoftEmployees.beforeFirst();
                    controlsoftFilter.cardNumber = platinumDebtors.getString("SwipeCard").trim();
                    if (!controlsoftEmployees.first()) {

                        validationMessage = String.format(validationTypes[5][0],
                                platinumDebtors.getString("Debtor_No"),
                                platinumDebtors.getString("Debtor_Name"),
                                platinumDebtors.getString("Contact_Person"),
                                platinumDebtors.getString("SwipeCard"));

                        validationResult.add(new ValidationItem(5, validationMessage));
                    } else {

                        controlsoftEmployees.beforeFirst();
                        while (controlsoftEmployees.next()) {

                            //*** 6, See if the member numbers match ***
                            String controlsoftMemberNumber = controlsoftEmployees.getString("Number").trim();
                            String platinumMemberNumber = platinumDebtors.getString("Contact_Person").trim();
                            if (!platinumMemberNumber.contentEquals(controlsoftMemberNumber)) {

                                validationMessage = String.format(validationTypes[6][0],
                                        platinumDebtors.getString("Debtor_No"),
                                        platinumDebtors.getString("Debtor_Name"),
                                        platinumDebtors.getString("Contact_Person"),
                                        controlsoftMemberNumber,
                                        controlsoftFilter.cardNumber);

                                validationResult.add(new ValidationItem(6, validationMessage));
                            }
                        }
                    }
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }

    List<ValidationItem> validatePriceLevel() {

        MemberFilter pastelFilter = new MemberFilter("CustomerCode", null);
        String validationMessage;
        List<ValidationItem> validationResult = new ArrayList<ValidationItem>();

        try {
            pastelCustomers.setFilter(pastelFilter);
            platinumDebtors.beforeFirst();
            while (platinumDebtors.next()) {
                if (!platinumDebtors.getBoolean("Blocked")) {

                    //Don't evaluate reords without member number or swipe card. 
                    //leave to evaluateByItself.
                    boolean hasNoMemberNo = platinumDebtors.getString("Contact_Person").trim().isEmpty();
                    if (hasNoMemberNo) {
                        continue;
                    }

                    pastelFilter.memberNumber = platinumDebtors.getString("Contact_Person");

                    pastelCustomers.beforeFirst();
                    while (pastelCustomers.next()) {

                        int priceLevel = platinumDebtors.getInt("Price_Level");
                        double balance = pastelCustomers.getDouble("balance");
                        String tag = pastelCustomers.getString("UserDefined01").trim();

                        //*** 7, A member should be on a minimum price level of 2 ***
                        if ((priceLevel < 2)) {

                            validationMessage = String.format(validationTypes[7][0],
                                    platinumDebtors.getString("Debtor_No"),
                                    platinumDebtors.getString("Debtor_Name"),
                                    platinumDebtors.getString("Contact_Person"));

                            validationResult.add(new ValidationItem(7, validationMessage));
                        }

                        //*** 8, If the balance amount is more than R2500.00 the member should not be on Price Level 3 ***
                        //*** unless he has a special member tag in the Pastel UserDefine01 field ***
                        else if ((balance >= 2500) && (priceLevel != 2)) {

                            if (!PastelMemberTags.contains(tag)) {

                                validationMessage = String.format(validationTypes[8][0],
                                        platinumDebtors.getString("Debtor_No"),
                                        platinumDebtors.getString("Debtor_Name"),
                                        platinumDebtors.getString("Contact_Person"),
                                        balance);

                                // This rule is not applied any more...
                                //validationResult.add(new ValidationItem(8, validationMessage));
                            }
                        }                            
                        //*** 9, If the balance amount is less than R1000.00 the member should not be on Price Level 3 ***
                        else if ((balance < 1000) && (priceLevel != 3)) {

                            validationMessage = String.format(validationTypes[9][0],
                                    platinumDebtors.getString("Debtor_No"),
                                    platinumDebtors.getString("Debtor_Name"),
                                    platinumDebtors.getString("Contact_Person"),
                                    balance);

                            validationResult.add(new ValidationItem(9, validationMessage));
                        }
                    }
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }

    List<ValidationItem> validatePriceLists() {
        
        List<ValidationItem> validationResult = new ArrayList<>();
        try {
            platinumPrices.beforeFirst();
            while (platinumPrices.next()) {
                double price1 = platinumPrices.getDouble("Price1")/1.14;
                double price2 = platinumPrices.getDouble("Price2")/1.14;
                double price3 = platinumPrices.getDouble("Price3")/1.14;
                String productCode = platinumPrices.getString("Code");
                String productDescription = platinumPrices.getString("Name");
                double costPrice = platinumPrices.getDouble("Ave_Cost");
                double gp1 = (price1-costPrice)/price1;
                double discount2 = (price1-price2)/price1;
                double discount3 = (price1-price3)/price1;
                
                //*** 10, Check for zero prices ***
                if(price1==0) addValidationItem10(validationResult, 1, productCode, productDescription);
                //if(price2==0) addValidationItem10(validationResult, 2, productCode, productDescription);
                if(price3==0) addValidationItem10(validationResult, 3, productCode, productDescription);
                
                //*** 11, Check the margin on price 1. Exclude Happy Hour & Specials products ***
                double specialsGP = 0.05;
                if (!platinumPrices.getString("Dept").trim().toUpperCase().equals("HAPPY HOUR")&&
                    !platinumPrices.getString("Dept").trim().toUpperCase().equals("SPECIALS")) {
                    if (gp1 < minimumGP - 0.001) {
                        String validationMessage = String.format(validationTypes[11][0],
                                productCode,
                                productDescription,
                                minimumGP * 100,
                                gp1 * 100);

                        validationResult.add(new ValidationItem(11, validationMessage));
                    }
                }
                else //Happy Hour or special
                {
                    if (gp1 < specialsGP - 0.001) {
                        String validationMessage = String.format(validationTypes[11][0],
                                productCode,
                                productDescription,
                                specialsGP * 100,
                                gp1 * 100);

                        validationResult.add(new ValidationItem(11, validationMessage));
                    }
                }

                //*** 12, Check the discount on price 3 **
                if (discount3 > 0.201) {
                    String validationMessage = String.format(validationTypes[12][0],
                            productCode,
                            productDescription,
                            discount3 * 100);

                    validationResult.add(new ValidationItem(12, validationMessage));
                }
            }
            return validationResult;
        } catch (Exception ex) {
            Logger.getLogger(ValidatePlatinum.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }
    
    //***10, Price zero ***
    void addValidationItem10(List<ValidationItem> validationResult,
            int priceListNumber,
            String productCode,
            String productDescription){
        
                            String validationMessage = String.format(validationTypes[10][0],
                                    priceListNumber,
                                    productCode,
                                    productDescription);

                            validationResult.add(new ValidationItem(10, validationMessage));
    }
}
