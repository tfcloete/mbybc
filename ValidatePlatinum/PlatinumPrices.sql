SELECT prod.Product_Code as Code
      ,[Short_Description] as Name
      ,dep.Short_Name as Dept
      ,[Unit_Size] as Size
      ,[Unit_of_Measure] as UOM
      ,[Touch_Item]
      ,[Landed_Cost] as Cost
      ,prod.Ave_Cost
      ,[Selling_Price] as Price1
      ,price.Price2
      ,price.Price3
      ,price.Price4
      ,price.Price5
      ,price.Price6
      ,sum(sales.Qty) as QtySum
  FROM [Platinum].[dbo].[Products] as prod
  Inner Join [Platinum].[dbo].[Product_Prices] as price
  on prod.Product_Code = price.Product_Code
  Inner Join [Platinum].[dbo].[Departments] as dep
  on prod.Department_No = dep.Department_No
  left Join [Platinum].[dbo].[Sales_Journal] as sales
  on prod.Product_Code = sales.Product_Code
  where prod.Sales_Item = 1
  group by prod.Product_Code
        ,[Short_Description]
      ,dep.Short_Name
      ,[Unit_Size]
      ,[Unit_of_Measure]
      ,[Touch_Item]
      ,[Landed_Cost]
      ,prod.Ave_Cost
      ,[Selling_Price]
      ,price.Price2
      ,price.Price3
      ,price.Price4
      ,price.Price5
      ,price.Price6

GO


