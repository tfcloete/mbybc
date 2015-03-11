select Category, CustomerCode, CustomerDesc, 
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
             ) as sixtydays, 
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
             CurrBalanceLast13, 
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
             and currentbalance>=sixtydays
             and sixtydays>=1000
             order by CustomerCode