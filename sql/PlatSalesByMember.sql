select Date_time,dt.Debtor_No, dt.Debtor_Name, dt.Contact_person, Debit 
from dbo.Debtor_Accounts as acc, Debtors as dt 
where dt.Debtor_No=acc.Account_No and 
dt.Blocked=1 and
Date_Time>'2014-04-01';
