select 
Debtors.Debtor_No as Debtor,  
Debtors.Contact_Person as Member,  
Debtors.Debtor_Name as Name,  
Debtors.Blocked,    
Debtors.Price_Level,    
round(Members.sales,2) as Sales,  
round(members.balance,2) as Balance     
from 

(
	select 
	members1.debtor,
	members1.Contact_Person,
	members1.sales,
	members2.balance 
	from
	 
	(
		SELECT 
		Min(Debtors.Debtor_No) as debtor,  
		Debtors.Contact_Person,  
		sum(Debtor_Accounts.Debit) as sales   
		FROM Platinum.dbo.Debtors Debtors  
		left join Debtor_Accounts
		on Debtors.Debtor_No = Debtor_Accounts.Account_No  
		GROUP BY
		Debtors.Contact_Person
	 ) as members1 
	  
	left join 

	(
		select
		Min(Debtors.Debtor_No) as debtor,  
		Debtors.Contact_Person,  
		sum(Debtors.Balance) as balance   
		FROM Platinum.dbo.Debtors  
		GROUP BY
		Debtors.Contact_Person
	) as members2 
	
	on members1.debtor = members2.debtor
 
) as members

left join Platinum.dbo.Debtors 
on members.debtor = Debtors.Debtor_No
