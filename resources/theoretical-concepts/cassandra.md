More fields can be added in type-ID pairs:
                    
    /<Database>/<Table>/<Id>/<Type1>/<Sub Id1>                  
    /<Database>/<Table>/<Id>/<Type1>/<Sub Id1>/<Type2>/<Sub Id2>                    
Examples of valid keys are:                 
                    
/cpm/customers/15                   
/cpm/customers/15/agreements/65                 
/cpm/customers/15/agreements/65/products/34                 
/cil/customers_location/15                  
Entity Value                    
Entities in CIL usually have an EntityValue associated with them, but this is not mandatory                 
                    
An EntityValue contains some data and a schema id. The data should be Avro encoded and it represents the value of an entity, the schema id represents the schema used to encode the data. The schema id is mandatory - it cannot be null.                   
                    
Cassandra Mapping                   
Cassandra 2.2.x is used as the back end storage in CIL. All Entity Key in CIL are mapped using a specific scheme into Cassandra. There are different ways to access data in Cassandra. CIL use the CQL API. CQL is an abstraction layer in Cassandra which presents data arranged tables, with support for SQL-like queries.                    
                    
However, internally in Cassandra data is arranged into a set of Column Families (CF). Column Families have many similarities with tables in an SQL database. For example, a CF have a Row Key (similar to primary index) as well as a set of named columns. The number of columns and their names are dynamic, and different rows can have different columns. Column families are then stored in a Key Space (similar to a DB schema). Key Spaces are important to Cassandra when distributing data internally in the Cassandra cluster. Data Segment distribution in CIL is not necessarily aligned with how Key Spaces are distributed in Cassandra. These concepts are similar, but not related to one another. Visit the official Cassandra site for more details.                  
                    
Before CIL store an entity into Cassandra, the Entity Key is broken down into a few specific parts. Consider the following Entity Key:                  
                    
/cpm/customers/56                   
/cpm/customers/56/agreements/23                 
/cpm/customers/56/agreements/23/products/12                 
/cpm/customers/56/agreements/23/products/17                 
/cpm/customers/56/agreements/24                 
/cpm/customers/56/agreements/24/products/19                 
These would be mapped into Cassandra as follows (the data is not shown here):                   
                    
Key Space: cpm                  
  Column Family: customers                  
    56 -> /, agreements/23, agreements/23/products/12, agreements/23/products/17, agreements/24, agreements/24/products/19                  
Several things can be observed here. The Key Space in Cassandra has the same name as the first field in the Entity Key. Also the Column Family used has its name derived from the second field in the Entity Key. The third field in the Entity Key is used as the Row Key in Cassandra.                    
                    
As all these Entity Keys have the same Entity Base Key, that means that all Entity Keys will be mapped to the same Row Key. Once the three first fields in the Entity Key have been mapped to Key Space, Column Family and Row Key, the remainder of the Entity Key is simply used as the name of the column in Cassandra. There is one special case though. The first Entity Key which have only three sections, will be mapped into a column named "/".                   
                    
Continuing on the same example, lets assume that the following Entity Keys were added as well:                  
                    
/cpm/customers/78                   
/cpm/customers/78/agreements/28                 
/cpm/customers/78/agreements/28/products/15                 
In Cassandra we will now have the following content:                    
                    
Key Space: cpm                  
  Column Family: customers                  
    56 -> /, agreements/23, agreements/23/products/12, agreements/23/products/17, agreements/24, agreements/24/products/19                  
    78 -> /, agreements/28, agreements/28/products/15                   
Note that different rows in Cassandra can have different column names, and a varying number of columns per row.                 
                    
Continuing further on the same example:                 
                    
/cha/customers/78                   
As the Entity Key is associated with another Database, it will end up in another Key Space:                 
                    
Key Space: cpm                  
  Column Family: customers                  
    56 -> /, agreements/23, agreements/23/products/12, agreements/23/products/17, agreements/24, agreements/24/products/19                  
    78 -> /, agreements/28, agreements/28/products/15                   
                    
Key Space: cha                  
  Column Family: customers                  
    78 -> /                 
Consider that we now add a totally different Table:                 
                    
/cil/customers_location/78                  
As another Table is added, a new Column Family will be used in Cassandra:                   
                    
Key Space: cpm                  
  Column Family: customers                  
    56 -> /, agreements/23, agreements/23/products/12, agreements/23/products/17, agreements/24, agreements/24/products/19                  
    78 -> /, agreements/28, agreements/28/products/15                   
                    
Key Space:                  
  Column Family: customers                  
    78 -> /                 
                    
Key Space: cil                  
  Column Family: customers_location                 
    78 -> /                 