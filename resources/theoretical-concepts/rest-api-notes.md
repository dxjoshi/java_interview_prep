## REST API concepts:  

## Topics:      
* [Introduction](#introduction)       
* [REST API Examples](#examples)      
* [Architectural Constraints](#architectural-constraints)               
* [HTTP Status Codes](#http-status-codes)            
* [HTTP methods](#http-methods)           
* [Headers](#headers)   
* [Difference between PUT and POST](#difference-between-put-and-post)   
* [HTTP request components](#http-request-components)
* [HTTP response components](#http-response-components)
* [Idempotency](#idempotency)
* [Best practices for designing a secure RESTful web service](#best-practices-for-designing-a-secure-restful-web-service)
* [Best practices to create a standard URI for a web service](#best-practices-to-create-a-standard-uri-for-a-web-service)
* [RESTful API Best Practices](#best-practices)
* [SOAP vs REST](#soap-vs-rest)    
* [Pagination](#Pagination)         

## Articles:
* [Vinay Sahni's blog on Restful API](https://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api#restful)       
* [restcookbook.com](https://restcookbook.com/Basics/loggingin/) 
* [RESTful Web APIs Notes](https://github.com/dxjoshi/book-summaries/blob/fe9c49ca37140ba0c0a0ca99765c373a7f586f2b/Engineering/RestfulWebApis_LeonardRichardson.md)
* [RestAPI useful tutorials](https://www.restapitutorial.com/)              
* [Rest API examples](https://restfulapi.net/http-methods/)
* [Put vs Patch](https://stackoverflow.com/questions/28459418/use-of-put-vs-patch-methods-in-rest-api-real-life-scenarios/39338329#39338329)            
* [HTTP Headers](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers)             

### Introduction          
1. REST (Representational State Transfer) is web standards based architectural style or approach for communications purpose that is often used in various web services development.     
It uses HTTP Protocol for data communication and a relatively new aspect of writing web API.                
2. The options allows the client of the REST API to determine what HTTP methods (GET, HEAD, POST, PUT, DELETE) can be used for the resource identified by the requested URI.        
The client determines without initiating a resource request.        
   The REST OPTIONS method is also used for the CORS (Cross-Origin Resource Sharing) request.       
3. URI (Uniform Resource Identifiers) is used to identify each resource in the REST. An HTTP operation is called by the client application to access the resource.      
4. XML and JSON are the most popular representations of resources in REST.          
     
### Examples
- **Versioning:**   http://api.yourservice.com/v1/companies/34/employees
- **Sorting:**      GET /companies?sort=rank_asc           
- **Filtering:**    GET /companies?category=banking&location=india           
- **Searching:**    GET /companies?search=Digital Mckinsey           
- **Pagination:**   GET /companies?page=23           
- **GET:**   GET /shops/2/products : Get the list of all products from shop 2.       
- **GET:**   GET /shops/2/products/31: Get the details of product 31, which belongs to shop 2.       
- **DELETE:**   DELETE /shops/2/products/31 , should delete product 31, which belongs to shop 2.        
- **POST:**   POST /shops , should create a new shop and return the details of the new shop created. Use POST on collection-URLs.         
- **PUT:**   PUT /shops/2/products/31 , should update the info of product 31, Use PUT on resource-URL only, not the collection.      
- **PUT vs PATCH:**     
    
    
        -- Initial resource     
        POST /customers
        {  
           "id":1,
           "firstName":"first",
           "lastName":"last",
           "email":"noreply@www.javadevjournal.com"
        }
        
        PUT /customers/1
        {
          "id":1,
          "firstName":"first",
          "lastName":"last",
          "email":"contactus@www.javadevjournal.com"   //new email ID
        }
        
        -- Idempotent Patch
        PATCH /customers/1  
        {  
           "email":"newmail@www.javadevjournal.com"
        }
        
        GET /customers/1
        {  
           "id":1,
           "firstName":"first",
           "last Name":"last",
           "email":"newmail@www.javadevjournal.com"
        }
        
        -- Non-Idempotent Patch: Every time a new resource will get created because we are creating a Patch on /customers
        PATCH /customers
        {
          "firstName":"first",
          "lastName":"last",
          "email":"contactus@www.javadevjournal.com" 
        }
        
        GET /customers
        [
            {  
               "id":1,
               "firstName":"first",
               "last Name":"last",
               "email":"newmail@www.javadevjournal.com"
            },
            {  
               "id":2,
               "firstName":"first",
               "last Name":"last",
               "email":"contactus@www.javadevjournal.com"
            }
        ]

### Pagination           
- [Pagination Concepts](https://nordicapis.com/everything-you-need-to-know-about-api-pagination/)      
- [FB Pagination](https://developers.facebook.com/docs/graph-api/results)              
- [Atlassian Pagination](https://developer.atlassian.com/server/confluence/pagination-in-the-rest-api/)         
- An API that uses the Link header can return a set of ready-made links so the API consumer doesn't have to construct links themselves. This is especially important when pagination is **cursor based**.           
    Link: <https://api.github.com/user/repos?page=3&per_page=100>; rel="next", 
          <https://api.github.com/user/repos?page=50&per_page=100>; rel="last"

- **Offset Pagination:**           
    - **Advantages:**
        - Offset pagination requires almost no programming. It’s also stateless on the server side and works regardless of custom sort_by parameters.
    - **Disadvantages:**        
        - The downside of offset pagination is that it stumbles when dealing with large offset values. 
        - The other downside of offset pagination is that adding new entries to the table can cause confusion, which is known as page drift.   
    
    
        GET /items?limit=20&offset=100

- **Keyset Pagination:** Keyset pagination uses the filter values of the previous page to determine the next set of items.           
    - **Advantages:**
        - it doesn’t require additional backend logic. It only requires one limit URL parameter. 
        - It also features consistent ordering, even when new items are added to the database. It also works smoothly with large offset values.        

    
        Client requests most recent items: GET /items?limit=20          
        Upon clicking the next page, the query finds the minimum created date of 2019–01–20T00:00:00. This is then used to create a query filter for the next page.          
        GET /items?limit=20&created:lte:2019-01-20T00:00:00     
        And so on…      
        
- **Seek Pagination:**           
    - Seek pagination is the next step beyond keyset pagination. Adding the queries after_id and before_id, you can remove the constraints of filters and sorting. 
    - Unique identifiers are more stable and static than lower cardinality fields such as state enums or category names.
    - **Disadvantages:**        
        - It can be challenging to create custom sort orders. 
        - The other downside of offset pagination is that adding new entries to the table can cause confusion, which is known as page drift.   
          
          
        Client requests a list of the most recent items             
        GET items?limit=20          
        Client requests a list of the next 20 items, using the results of the first query           
        GET /items?limit=20&after_id=20         
        Client requests the next/scroll page, using the final entry from the second page as the starting point          
        GET /items?limit=20&after_id=40             
        
### Architectural Constraints   
1. Uniform interface:   
    - A resource should contain links (HATEOAS) pointing to relative URIs. **HATEOAS** stands for Hypertext As The Engine Of Application State. It means that hypertext should be used to find your way through the API.       
    - The resource representations across the system should follow specific guidelines such as naming conventions, link formats, or data format (XML or/and JSON).
2. Client–server:       
    - Servers and clients may also be replaced and developed independently, as long as the interface between them is not altered.
3. Stateless:   
    - The server will not store anything about the latest HTTP request the client made. It will treat every request as new. No session, no history.
    - No client context shall be stored on the server between requests. **The client is responsible for managing the state of the application**.
4. Cacheable:  
    - Caching shall be applied to resources when applicable, and then these resources MUST declare themselves cacheable. Caching can be implemented on the server or client-side.
    - A well-managed caching partially or completely eliminates some client–server interactions, further improving availability and performance.
5. Layered system:      
    - REST allows you to use a layered system architecture where you deploy the APIs on server A, and store data on server B and authenticate requests in Server C, for example.
    - A client cannot ordinarily tell whether it is connected directly to the end server or an intermediary along the way.
6. Code on demand (optional):   
    - Most of the time, you will be sending the static representations of resources in the form of XML or JSON. 
    - But when you need to, you are free to return executable code to support a part of your application, e.g., clients may call your API to get a UI widget rendering code. It is permitted.
 
### HTTP Status Codes           
**1xx** — It is used to communicate the transfer protocol-level information.        
**2xx** — It is used to indicate the request was accepted successfully. Some codes are,     
**200** (OK) — It indicates the request is successfully carried out.        
**201** - CREATED, when a resource is successful created using POST or PUT request. Return link to newly created resource using location header.        
**202** (Accepted) — It indicates the request has been accepted for processing.     
**204** (No Content) — It indicates when a request is declined.     
**3xx** - It indicates the client must take additional action to complete the request.      
**301** - It indicates that a page has permanently moved to a new location          
**302** - It indicates page has moved to a new location, but that it is only temporary           
**304** - NOT MODIFIED, used to reduce network bandwidth usage in case of conditional GET requests. Response body should be empty. Headers should have date, location etc.      
**4xx** - It is the client error status code.       
**400** - BAD REQUEST, states that invalid input is provided e.g. validation error, missing data.       
**401** - FORBIDDEN, states that user is not having access to method being used for example, delete access without admin rights.        
**404** - NOT FOUND, states that method is not available.       
**405** - Method_not_allowed      
**409** - CONFLICT, states conflict situation while executing the method for example, adding duplicate entry.       
**429** - TOO_MANY_REQUESTS      
**5xx** - It is the server error status code.           
**500** - INTERNAL SERVER ERROR, states that server has thrown some exception while executing the method.       
**502** - Bad Gateway              
**503** - Service Unavailable              
**504** - Gateway Timeout              
        
### HTTP methods    

A basic HTTP request consists of a verb (method) and a resource (endpoint).  Below are common HTTP verbs:

| Verb | Description | Idempotent* | Safe | Cacheable |
|---|---|---|---|---|
| GET | Reads a resource | Yes | Yes | Yes |
| POST | Creates a resource or trigger a process that handles data | No | No | Yes if response contains freshness info |
| PUT | Creates or replace a resource | Yes | No | No |
| PATCH | Partially updates a resource | No | No | Yes if response contains freshness info |
| DELETE | Deletes a resource | Yes | No | No |

*Can be called many times without different outcomes.
    
GET: It requests a resource at the request URL. It should not contain a request body as it will be discarded. Maybe it can be cached locally or on the server.      
POST: It submits information to the service for processing; it should typically return the modified or new resource     
PUT: At the request URL it update the resource      
DELETE: At the request URL it removes the resource      
OPTIONS: It indicates which HTTP methods are supported by a resource.          
HEAD: About the request URL it returns meta information             
        
### Headers             
Accept headers tells web service what kind of response client is accepting, so if a web service is capable of sending response in XML and JSON format and client sends Accept header as application/xml then XML response will be sent.         
For Accept header application/json, server will send the JSON response.     
Content-Type header is used to tell server what is the format of data being sent in the request.        
If Content-Type header is application/xml then server will try to parse it as XML data. This header is useful in HTTP Post and Put requests.

Here are some of the most common API Headers you will encounter when testing any API.
- **Authorization:** Contains the authentication credentials for HTTP authentication.           
- **WWW-Authenticate:** The server may send this as an initial response if it needs some form of authentication before responding with the actual resource being requested. Often following this header is the response code 401, which means “unauthorized”.           
- **Accept-Charset:** This header is set with the request and tells the server which character sets (e.g., UTF-8, ISO-8859-1, Windows-1251, etc.) are acceptable by the client.         
- **Content-Type:**  Tells the client what media type (e.g., application/json, application/javascript, etc.) a response is sent in. This is an important header field that helps the client know how to process the response body correctly.            
- **Cache-Control:** The cache policy defined by the server for this response, a cached response can be stored by the client and re-used till the time defined by the Cache-Control header.                 
        
### Difference between PUT and POST     
PUT puts a file or resource at a particular URI and exactly at that URI. If there is already a file or resource at that URI, PUT changes that file or resource. If there is no resource or file there, PUT makes one.       
POST sends data to a particular URI and expects the resource at that URI to deal with the request. The web server at this point can decide what to do with the data in the context of specified resource.       
PUT is idempotent meaning, invoking it any number of times will not have an impact on resources.        
However, POST is not idempotent, meaning if you invoke POST multiple times it keeps creating more resources.        
            PUT /article/1234   {"name":"book1"}       
            POST /articles      {"name":"book1"}    
            
### HTTP request components             
- The Verb which indicates HTTP methods such as GET, PUT, POST, DELETE.        
- URI stands for Uniform Resource Identifier.It is the identifier for the resource on the server.      
- HTTP Version which indicates HTTP version, for example-HTTP v1.1.        
- Request Header carries metadata (as key-value pairs) for the HTTP Request message. Metadata could be a client (or browser) type, the format that the client supports, message body format, and cache settings.       
- Request Body indicates the message content or resource representation.       
        
### HTTP response components            
- **Status/Response Code** — Indicates Server status for the resource present in the HTTP request. For example, 404 means resource not found, and 200 means response is ok.        
- **HTTP Version** — Indicates HTTP version, for example-HTTP v1.1.        
- **Response Header** — Contains metadata for the HTTP response message stored in the form of key-value pairs. For example, content length, content type, response date, and server type.      
- **Response Body** — Indicates response message content or resource representation.           
        
### Best practices for designing a secure RESTful web service             
- **Validation** − Validate all inputs on the server. Protect your server against SQL or NoSQL injection attacks.     
- **Session based authentication** − Use session based authentication to authenticate a user whenever a request is made to a Web Service method.      
- **No sensitive data in URL** − Never use username, password or session token in URL , these values should be passed to Web Service via POST method.     
- **Restriction on Method execution** − Allow restricted use of methods like GET, POST, DELETE. GET method should not be able to delete data.     
- **Validate Malformed XML/JSON** − Check for well formed input passed to a web service method.       
- **Throw generic Error Messages** − A web service method should use HTTP error messages like 403 to show access forbidden etc.       
        
### Best practices to create a standard URI for a web service           
- **Use Plural Noun** − Use plural noun to define resources. For example, we’ve used users to identify users as a resource.       
- **Avoid using spaces** − Use underscore(_) or hyphen(-) when using a long resource name, for example, use authorized_users instead of authorized%20users.       
- **Use lowercase letters** − Although URI is case-insensitive, it is good practice to keep url in lower case letters only.       
- **Maintain Backward Compatibility** − As Web Service is a public service, a URI once made public should always be available. In case, URI gets updated, redirect the older URI to new URI using HTTP Status code, 300.      
- **Use HTTP Verb** − Always use HTTP Verb like GET, PUT, and DELETE to do the operations on the resource. It is not good to use operations names in URI.             
        
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

### Safe Methods    
Safe methods are HTTP methods that do not modify resourcesand can be cached, prefetched without any repercussions to the resource. For instance, using GET or HEAD on a resource URL, should NEVER change the resource.         
However, this is not completely true. It means: it won't change the resource representation. It is still possible, that safe methods do change things on a server or resource, but this should not reflect in a different representation.       
    This means the following is incorrect, if this would actually delete the blogpost:  GET /blog/1234/delete HTTP/1.1      
    

### Idempotency  
[Stripe Idempotency keys](https://stripe.com/blog/idempotency)
[Idempotent POST requests](https://medium.com/@saurav200892/how-to-achieve-idempotency-in-post-method-d88d7b08fcdd)
An idempotent HTTP method is one that can be called many times without different outcomes, whether its called only once, or ten times over result should be the same. Again, this only applies to the result, not the resource itself.      
         
         a = 4;     // idempotent
         a++;       // not idempotent   
         
### SOAP vs REST
[SOAP Intro](https://www.geeksforgeeks.org/basics-of-soap-simple-object-access-protocol/?ref=lbp)         
[Difference](https://www.geeksforgeeks.org/difference-between-rest-api-and-soap-api/?ref=lbp)

         
         
                