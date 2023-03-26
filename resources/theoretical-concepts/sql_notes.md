### Useful Links:
 - [Indexes - Oracle Docs](https://docs.oracle.com/cd/E11882_01/server.112/e40540/indexiot.htm#CNCPT1182)               
 - [**Indexes in SQL**](https://www.complexsql.com/indexing-in-sql/)        
 - [Oracle Indexes](https://tipsfororacle.blogspot.com/2016/09/oracle-indexes.html)             
    
### Index
 - **Unique indexes** guarantee that no two rows of a table have duplicate values in the key column (or columns).       
 - **Primary index** create index on the columns using "create index command" to retrieve the rows faster.          
     
- To create an empty table with same structure as original table:       
		select * into Student_Copy from Student where 1=2;      
- Diff between UNION(AUB), MINUS(A-B), INTERSECT(A^B):              
		select name from Students       
		UNION       
		select name from Students_Two;	        
- Diff between UNOIN and UNION ALL:		            
	UNION - only gives unique records.      
	UNION ALL - gives all records, even duplicates      
- What is a view?               
	A virtual table. Can be used to hide internal complexity(multiple table joins) and restricting data access.         
	Create view ABC as select cust_name, city from Customers where country = 'India'        
- Character manipulation functions:             
	upper, lower, length, concat, initcap       
- Where vs Having               
	select * from Student where name = 'Dee';       
	select MAX(fees) from Student having id = '10'; (We can't use where with Aggregate Functions: Count, Max, Min, Avg, Sum)        
- IN vs Exists              
	select * from Student where id IN ('1', '11', '122', '75');     
	select * from Student where EXISTS (select city from Branch where branch.location=Student.address); - exists return either true or false        
- Order by(sorting) vs group by(used with aggregate functions)              
	select * from Student order by name desc;       
	select isPresent, count(*) from Student where name like '%Doe' group by isPresent;      
- Join(can select from both tables) vs Subquery(can select from outer table only)               
- **Joins:** Combine rows from 2 or more tables based on a related column.  
    Customer(id, name, designation)
    Order(id, amount, customer_id)
    - JOIN/ INNER JOIN: (rows common in both tables)
        select order.id, customer.name from Order join Customer on Order.customer_id = Customer.id 
    - LEFT JOIN:(Left table rows + common rows)
        select order.id, customer.name from Order left join Customer on Order.customer_id = Customer.id 
    - RIGHT JOIN:(Right table rows + common rows)
        select order.id, customer.name from Order right join Customer on Order.customer_id = Customer.id 
    - FULL OUTER JOIN:(All rows of both tables)
        select order.id, customer.name from Order full outer join Customer on Order.customer_id = Customer.id 
    - SELF JOIN:(All rows of both tables)
        Employee(id, name, manager_id, salary)
        select distinct e.name from Employee e join Employee e2 on e.id = e2.manager_id

- **Trigger:** is a stored PL/SQL block associated with a table/schema/database or an anonymous PL/SQL block. Database automatically executes a trigger when specified conditions occur.    
   CREATE TRIGGER salary_check
   BEFORE INSERT OR UPDATE OF salary, job_id ON employees
   FOR EACH ROW
   WHEN (new.job_id <> 'AD_VP')
   CALL check_sal(:new.job_id, :new.salary, :new.last_name)

- **Constraints:** We can specify the limit on the type of data that can be stored in a particular column in a table using constraints. The available constraints in SQL are: 
    - **NOT NULL:** This constraint tells that we cannot store a null value in a column. That is, if a column is specified as NOT NULL then we will not be able to store null in this particular column any more.
    - **UNIQUE:** This constraint when specified with a column, tells that all the values in the column must be unique. That is, the values in any row of a column must not be repeated.
    - **PRIMARY KEY:** A primary key is a field which can uniquely identify each row in a table. And this constraint is used to specify a field in a table as primary key.
    - **FOREIGN KEY:** A Foreign key is a field which can uniquely identify each row in a another table. And this constraint is used to specify a field as Foreign key.
    - **CHECK:** This constraint helps to validate the values of a column to meet a particular condition. That is, it helps to ensure that the value stored in a column meets a specific condition.
    - **DEFAULT:** This constraint specifies a default value for the column when no value is specified by the user.
   
- **CTE(Common Table Expression):** 
    - The Common Table Expressions (CTE) were introduced into standard SQL in order to simplify various classes of SQL Queries for which a derived table was just unsuitable.   
    - CTEs can be a useful tool when you need to generate temporary result sets that can be accessed in a SELECT, INSERT, UPDATE, DELETE, or MERGE statement.
    - CTE is a temporary named result set that you can reference within a SELECT, INSERT, UPDATE, or DELETE statement.
    - WITH cteReports (EmpID, FirstName, LastName, MgrID, EmpLevel) AS (
          SELECT EmployeeID, FirstName, LastName, ManagerID, 1 FROM Employees WHERE ManagerID IS NULL
          UNION ALL
          SELECT e.EmployeeID, e.FirstName, e.LastName, e.ManagerID, r.EmpLevel + 1 FROM Employees e INNER JOIN cteReports r ON e.ManagerID = r.EmpID )
      SELECT FirstName + ' ' + LastName AS FullName, EmpLevel, (SELECT FirstName + ' ' + LastName FROM Employees WHERE EmployeeID = cteReports.MgrID) AS Manager
      FROM cteReports 
      ORDER BY EmpLevel, MgrID 
    
## Syntax:  
- CREATE DATABASE database_name;
- CREATE TABLE table_name ( column1 data_type(size), column2 data_type(size), column3 data_type(size), );        
- INSERT INTO table_name VALUES (value1, value2, value3,...);
- INSERT INTO table_name (column1, column2, column3,..) VALUES ( value1, value2, value3,..);
- UPDATE table_name SET column1 = value1, column2 = value2,... WHERE condition;                    
- DELETE FROM table_name WHERE some_condition;

- ALTER TABLE table_name ADD (Columnname_1  datatype, Columnname_2  datatype, … Columnname_n  datatype);
- ALTER TABLE table_name DROP COLUMN column_name;
- ALTER TABLE table_name MODIFY column_name column_type;        

- SELECT * FROM table_name ORDER BY column1 ASC|DESC, column2 ASC|DESC;
- SELECT DISTINCT column1,column2 FROM table_name;
- SELECT * FROM Students WHERE name LIKE 'A%';
- SELECT * FROM Students WHERE address LIKE '_E%';
- SELECT column_name(s) FROM table_name WHERE column_name BETWEEN value1 AND value2; 
- SELECT column_name(s) FROM table_name WHERE column_name IN (list_of_values);  
- SELECT table1.column1, table1.column2, table2.column1 FROM table1 **(INNER JOIN | LEFT JOIN | RIGHT JOIN | FULL JOIN)** table2 ON table1.matching_column = table2.matching_column; 
- SELECT table1.column1, table1.column2, table2.column1 FROM table1 CROSS JOIN table2; (**Cross join** is similar to an inner join where the join-condition will always evaluate to True)   
- SELECT a.coulmn1 , b.column2 FROM table_name a, table_name b WHERE some_condition; (**Self Join**)

- Aggregate functions: Count(), Sum(), Avg(), Min(), Max()  
- SELECT column1, function_name(column2) FROM table_name WHERE condition GROUP BY column1, column2 ORDER BY column1, column2;   
- SELECT * FROM Students **LIMIT** 3; (LIMIT clause is used to set an upper limit on the number of tuples returned) 
- SELECT * FROM Students **LIMIT 3 OFFSET 2** ORDER BY roll_no;(LIMIT x OFFSET y simply means skip the first y entries and then return the next x entries. OFFSET can only be used with the ORDER BY clause. It cannot be used on its own.)
- SELECT column_name(s) FROM table1 **UNION** SELECT column_name(s) FROM table2;
- SELECT column-1, column-2 …… FROM table 1 WHERE….. **INTERSECT** SELECT column-1, column-2 …… FROM table 2 WHERE…..   
- SELECT column_name(s) FROM table_name WHERE **EXISTS** (SELECT column_name(s) FROM table_name WHERE condition);   
- **Sub-query:** A subquery is a query within another query. The outer query is called as main query and inner query is called as subquery. ORDER BY command cannot be used in a Subquery.   
  SELECT column_name FROM table_name WHERE column_name expression operator ( SELECT COLUMN_NAME  from TABLE_NAME   WHERE ... );    
- **Sub-query in from clause:** SELECT column1, column2 FROM (SELECT column_x  as C1, column_y FROM table WHERE PREDICATE_X) as table2 WHERE PREDICATE;     
- **Independent Nested Queries:** SELECT * FROM Students WHERE Roll_No IN (SELECT DISTINCT Roll_No FROM Marks WHERE Score >= 90);   
- **Co-related Nested Queries:** SELECT * FROM Students WHERE EXISTS (SELECT * FROM Students WHERE Student.Roll_No = Marks.Roll_No AND Marks.Course = 'CN');   
- **Correlated subqueries** are used for row-by-row processing. Each subquery is executed once for every row of the outer query.
  SELECT column1, column2, .... FROM table1 outer WHERE column1 operator (SELECT column1, column2 FROM table2 WHERE expr1 = outer.expr2); 
- GRANT privilege_name ON object_name TO username; GRANT all ON Student to john_doe;
- REVOKE privilege_name ON object_name FROM username;   

## Popular Queries: 
- Find all the employees who earn more than the average salary in their department.                 


      SELECT last_name, salary, department_id FROM employees outer 
      WHERE salary > (SELECT AVG(salary) FROM employees WHERE department_id = outer.department_id);
- Find 2nd highest salary:                  


      select Max(salary) from Employee where salary < (select Max(salary) from Employee)
      select Min(salary) from Employee where salary in (select salary from Employee order by salary desc Limit 2)
      select Top 1 salary from (select Top 2 salary from Employee order by salary desc) as Emp1 order by salary asc
- Find name of employee with 2nd highest salary:            


      select name from Employee 
      where salary = (select Min(salary) from Employee 
        where salary < (select Max(salary) from Employee))    
- Find Nth highest salary:                  


        select name from Employee where salary = 
            (select Min(salary) from Employee where salary in 
                (select distinct TOP N salary from Employee order by salary desc))  
- Find details of all employees whose salary is greater than average:                       


        select * from Employee where salary > 
                (select AVG(salary) from Employee))  
- Find details of all employees whose salary is greater than average salary of **all dept:**                


        select * from Employee where salary > All (select AVG(salary) from Employee group by dept)) 
- Find details of all employees whose salary is greater than average salary **in the dept:**                


        select * from Employee e1 where salary > (select AVG(salary) from Employee e2 where e1.dept_id=e2.dept_id))     
- Find names of departments that don't have any employee                    


        select name from Department d where dept_id not exists 
        (select distinct e.dept_id from Employee e where e.dept_id=d.dept_id)
- Find employee names, their salaries and average salaries of their departments                     


        select name, salary, (select avg(salary) from Empoloyee e2 where e2.dept_id=e1.dept_id) as average from Employee e1;
- Print city names which don't start with M:                    


        select city from Cities where substr(city,1,1) not in ('a',eiou AEIOU);     
- Find all duplicate emails in a table:                   


        select distinct id from Emails group by id having count(id) > 1    
- Find name in uppercase and use alias:                  


        select upper(name) as FIRST_NAME from Person
- Fetch top N records:                  


        select top N * from Employee order by name desc;
- Fetch 2 cloumns in a single column:                   


        select concat(fname, ' ', lname) as FULL_NAME from Person
- Fetch sum of salaries paid for each dept:                     


        select dept, sum(salary) from Employee group by dept
- Fetch salaries between 10000 to 20000:                    


        select salary from Employee where salary between 10000 and 20000;
        
## MySQL Query Optimization:
- Main considerations are:    
    - Check if index can be added. If already added, check explain plan to see if the index is being used. 
    - Isolate or tune out part of query, function call that takes excessive time.
    - Read explain plan and use it to adjust index, where, join clause etc.
    