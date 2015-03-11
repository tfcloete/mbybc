select Category, CustomerCode, CustomerDesc, 
            (BalanceLast01 
             + BalanceLast02 
             + BalanceLast03 
             + BalanceLast04 
             + BalanceLast05 
             + BalanceLast06 
             + BalanceLast07 
             + BalanceLast08 
             + BalanceLast09 
             + BalanceLast10 
             + BalanceLast11 
             + BalanceLast12 
             + BalanceLast13 
             + BalanceThis01 
             + BalanceThis02 
             + BalanceThis03 
             + BalanceThis04 
             + BalanceThis05 
             + BalanceThis06 
             + BalanceThis07 
             + BalanceThis08 
             + BalanceThis09 
             + BalanceThis10 
             + BalanceThis11 
             + BalanceThis12 
             + BalanceThis13) as balance, 
            (CurrBalanceLast01 
             + CurrBalanceLast02 
             + CurrBalanceLast03 
             + CurrBalanceLast04 
             + CurrBalanceLast05 
             + CurrBalanceLast06 
             + CurrBalanceLast07 
             + CurrBalanceLast08 
             + CurrBalanceLast09 
             + CurrBalanceLast10 
             + CurrBalanceLast11 
             + CurrBalanceLast12 
             + CurrBalanceLast13 
             + CurrBalanceThis01 
             + CurrBalanceThis02 
             + CurrBalanceThis03 
             + CurrBalanceThis04 
             + CurrBalanceThis05 
             + CurrBalanceThis06 
             + CurrBalanceThis07 
             + CurrBalanceThis08 
             + CurrBalanceThis09 
             + CurrBalanceThis10 
             + CurrBalanceThis11 
             + CurrBalanceThis12 
             + CurrBalanceThis13) as currentbalance, 
             Ageing01, 
             Ageing02, 
             Ageing03, 
             Ageing04, 
             Ageing05, 
             UserDefined01 
             from CustomerMaster as cust 
             inner join CustomerCategories as cat 
             on cust.Category = cat.CCCode 
             where Category<>27 and Category<>30 and Category<>50 
             order by CustomerCode