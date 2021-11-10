## REST API concepts:  

## Topics:      
* [HTTP Response Codes](#http-response-codes)               
* [Concurrency Questions](#concurrency-questions)               
        
### HTTP Status Codes           
1xx — It is used to communicate the transfer protocol-level information.        
2xx — It is used to indicate the request was accepted successfully. Some codes are,     
200 (OK) — It indicates the request is successfully carried out.        
201 (Created) — It is returned when a resource is created inside the collection.        
202 (Accepted) — It indicates the request has been accepted for processing.     
204 (No Content) — It indicates when a request is declined.     
3xx — It indicates the client must take additional action to complete the request.      
4xx — It is the client error status code.       
5xx — It is the server error status code.           
200 - OK, shows success.        
201 - CREATED, when a resource is successful created using POST or PUT request. Return link to newly created resource using location header.        
304 - NOT MODIFIED, used to reduce network bandwidth usage in case of conditional GET requests. Response body should be empty. Headers should have date, location etc.      
400 - BAD REQUEST, states that invalid input is provided e.g. validation error, missing data.       
401 - FORBIDDEN, states that user is not having access to method being used for example, delete access without admin rights.        
404 - NOT FOUND, states that method is not available.       
409 - CONFLICT, states conflict situation while executing the method for example, adding duplicate entry.       
500 - INTERNAL SERVER ERROR, states that server has thrown some exception while executing the method.       
        
### HTTP methods        
GET: It requests a resource at the request URL. It should not contain a request body as it will be discarded. Maybe it can be cached locally or on the server.      
POST: It submits information to the service for processing; it should typically return the modified or new resource     
PUT: At the request URL it update the resource      
DELETE: At the request URL it removes the resource      
OPTIONS: It indicates which techniques are supported        
HEAD: About the request URL it returns meta information             
        
### Headers             
Accept headers tells web service what kind of response client is accepting, so if a web service is capable of sending response in XML and JSON format and client sends Accept header as application/xml then XML response will be sent.         
For Accept header application/json, server will send the JSON response.     
Content-Type header is used to tell server what is the format of data being sent in the request.        
If Content-Type header is application/xml then server will try to parse it as XML data. This header is useful in HTTP Post and Put requests.        
        
### Imp Points          
1. REST (Representational State Transfer) is web standards based architectural style or approach for communications purpose that is often used in various web services development.     
It uses HTTP Protocol for data communication and a relatively new aspect of writing web API.                
2. The options allows the client of the REST API to determine what HTTP methods (GET, HEAD, POST, PUT, DELETE) can be used for the resource identified by the requested URI.        
The client determines without initiating a resource request.        
   The REST OPTIONS method is also used for the CORS (Cross-Origin Resource Sharing) request.       
3. URI (Uniform Resource Identifiers) is used to identify each resource in the REST. An HTTP operation is called by the client application to access the resource.      
4. XML and JSON are the most popular representations of resources in REST.          
        
        
### Explain the caching mechanism       
Caching is a process of storing server response at the client end. It makes the server save significant time from serving the same resource again and again.        
The server response holds information which leads a client to perform the caching. It helps the client to decide how long to archive the response or not to store it at all.        
        
        
### difference between PUT and POST     
PUT puts a file or resource at a particular URI and exactly at that URI. If there is already a file or resource at that URI, PUT changes that file or resource. If there is no resource or file there, PUT makes one.       
POST sends data to a particular URI and expects the resource at that URI to deal with the request. The web server at this point can decide what to do with the data in the context of specified resource.       
PUT is idempotent meaning, invoking it any number of times will not have an impact on resources.        
However, POST is not idempotent, meaning if you invoke POST multiple times it keeps creating more resources.        
            
### HTTP request components             
1. The Verb which indicates HTTP methods such as GET, PUT, POST, DELETE.        
2. URI stands for Uniform Resource Identifier.It is the identifier for the resource on the server.      
3. HTTP Version which indicates HTTP version, for example-HTTP v1.1.        
4. Request Header carries metadata (as key-value pairs) for the HTTP Request message. Metadata could be a client (or browser) type, the format that the client supports, message body format, and cache settings.       
5. Request Body indicates the message content or resource representation.       
        
### HTTP response components            
1. Status/Response Code — Indicates Server status for the resource present in the HTTP request. For example, 404 means resource not found, and 200 means response is ok.        
2. HTTP Version — Indicates HTTP version, for example-HTTP v1.1.        
3. Response Header — Contains metadata for the HTTP response message stored in the form of key-value pairs. For example, content length, content type, response date, and server type.      
4. Response Body — Indicates response message content or resource representation.           
        
### What are the best practices to be followed while designing a secure RESTful web service             
Validation − Validate all inputs on the server. Protect your server against SQL or NoSQL injection attacks.     
Session based authentication − Use session based authentication to authenticate a user whenever a request is made to a Web Service method.      
No sensitive data in URL − Never use username, password or session token in URL , these values should be passed to Web Service via POST method.     
Restriction on Method execution − Allow restricted use of methods like GET, POST, DELETE. GET method should not be able to delete data.     
Validate Malformed XML/JSON − Check for well formed input passed to a web service method.       
Throw generic Error Messages − A web service method should use HTTP error messages like 403 to show access forbidden etc.       
        
### best practices to create a standard URI for a web service           
Use Plural Noun − Use plural noun to define resources. For example, we’ve used users to identify users as a resource.       
Avoid using spaces − Use underscore(_) or hyphen(-) when using a long resource name, for example, use authorized_users instead of authorized%20users.       
Use lowercase letters − Although URI is case-insensitive, it is good practice to keep url in lower case letters only.       
Maintain Backward Compatibility − As Web Service is a public service, a URI once made public should always be available. In case, URI gets updated, redirect the older URI to new URI using HTTP Status code, 300.      
Use HTTP Verb − Always use HTTP Verb like GET, PUT, and DELETE to do the operations on the resource. It is not good to use operations names in URI.             
        
### Best Practices      
[Ref](https://betterprogramming.pub/22-best-practices-to-take-your-api-design-skills-to-the-next-level-65569b200b9)         
1. API design follows **Resource Oriented Design**. It consists of three key concepts:              
Resource: A resource is a piece of data, For example, a User.       
Collection: A group of resources is called a collection. Example: A list of users       
URL: Identifies the location of resource or collection. Example: /user      
2. Use kebab-case for URLs:         
                
        /systemOrders or /system_orders  (BAD)      
        vs.         
        /system-orders (GOOD)       
3. Use camelCase for Parameters:            
            
        /system-orders/{order_id} or /system-orders/{OrderId}           
        vs.         
        /system-orders/{orderId}                    
4. Plural Name to Point to a Collection:            
                
        GET /user or GET /User      
        vs.     
        GET /users              
5. URL Starts With a Collection and Ends With an Identifier:            
        
        GET /shops/:shopId/category/:categoryId/price       
        vs.     
        GET /shops/:shopId/ or GET /category/:categoryId                
6. Keep Verbs Out of Your Resource URL:         
                
        POST /updateuser/{userId} or GET /getusers      
        vs.     
        PUT /user/{userId}              
7. Use Verbs for Non-Resource URL(that returns nothing but an operation):       
        
        POST /alerts/245743/resend  (GOOD)              
8. Use camelCase for JSON property:     
        
        {       
           user_name: "Mohammad Faisal"     
           user_id: "1"     
        }       
        vs.     
        {       
           userName: "Mohammad Faisal"      
           userId: "1"      
        }       
9. RESTful HTTP services MUST implement the /health and /version and /metrics API endpoints:                
                
        /health - Respond to requests to /health with a 200 OK status code.     
        /version - Respond to request to /version with the version number.      
        /metrics - This endpoint will provide various metrics like average response time.       
        /debug and /status endpoints are also highly recommended.       
10. Don’t Use table_name for the Resource Name:             
        
        Bad: product_order  // exposing the underlying architecture         
        Good: product-orders        
11. Use API Design Tools like API Blueprint and Swagger             
12. Always use versioning for the API and move it all the way to the left so that it has the highest scope. The version number should be v1, v2 etc.                
        
        http://api.domain.com/v1/shops/3/products (GOOD)        
13. If an API returns a list of objects always include the total number of resources in the response:               
                
        {       
          users: [      
             ...        
          ]     
        }       
                
        {       
          users: [      
             ...        
          ],        
          total: 34         //Good      
        }       
14. Accept limit and offset Parameters because it’s necessary for pagination on the front end.          
                
        GET /shops?offset=5&limit=5         
15. Add a fields parameter to expose only the required fields from your API. It also helps to reduce the response size in some cases.               
            
        GET /shops?fields=id,name,address,contact       
16. Don’t Pass Authentication Tokens in URL(bad practice) because often URLs are logged and the authentication token will also be logged unnecessarily. Instead, pass them with the header.         
        
        GET /shops/123?token=some_kind_of_authenticaiton_token      
        vs.     
        Authorization: Bearer xxxxxx, Extra yyyyy       //passed with header                
17. Always validate the content-type and if you want to go with a default one use content-type: application/json            
18. Use HTTP Methods for CRUD Functions:                
GET: To retrieve a representation of a resource.        
POST: To create new resources and sub-resources.        
PUT: To update existing resources.      
PATCH: To update existing resources. It only updates the fields that were supplied, leaving the others alone.               
DELETE: To delete existing resources.       
19. Use the Relation in the URL For Nested Resources. Some practical examples are:          
GET /shops/2/products : Get the list of all products from shop 2.       
GET /shops/2/products/31: Get the details of product 31, which belongs to shop 2.       
DELETE /shops/2/products/31 , should delete product 31, which belongs to shop 2.        
PUT /shops/2/products/31 , should update the info of product 31, Use PUT on resource-URL only, not the collection.      
POST /shops , should create a new shop and return the details of the new shop created. Use POST on collection-URLs.         
20. Do support CORS (Cross-Origin Resource Sharing) headers for all public-facing APIs.         
Consider supporting a CORS allowed origin of “*”, and enforcing authorization through valid OAuth tokens.       
Avoid combining user credentials with origin validation.        
21. Enforce HTTPS (TLS-encrypted) across all endpoints, resources, services, callback URLs, push notification endpoints, and webhooks.              
22. Do return 4xx HTTP error codes when rejecting a client request due to one or more Service Errors.       
Consider processing all attributes and then returning multiple validation problems in a single response.            
        