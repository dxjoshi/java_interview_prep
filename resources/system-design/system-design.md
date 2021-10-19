
### System Design Template (Credit: [Topcat](https://leetcode.com/topcat/))

####(1) FEATURE EXPECTATIONS: Functional & Non Functional Requirements [5 min]    
        (1) Use cases   
        (2) Scenarios that will not be covered  
        (3) Who will use    
        (4) How many will use   
        (5) Usage patterns  
####(2) ESTIMATIONS [5 min] (Ask if needed to do the estimations) 
        (1) Throughput (QPS for read and write queries) 
        (2) Latency expected from the system (for read and write queries)   
        (3) Read/Write ratio    
        (4) Traffic estimates   
                - Write (QPS, Volume of data)   
                - Read  (QPS, Volume of data)   
        (5) Storage estimates   
        (6) Memory estimates    
                - If we are using a cache, what is the kind of data we want to store in cache   
                - How much RAM and how many machines do we need for us to achieve this ?    
                - Amount of data you want to store in disk/ssd  
####(3) DESIGN GOALS [5 min]    
        (1) Latency and Throughput requirements 
        (2) Consistency vs Availability  [Weak/strong/eventual => consistency | Failover/replication => availability]   
####(4) HIGH LEVEL DESIGN [5-10 min]    
        (1) APIs for Read/Write scenarios for crucial components    
        (2) Database schema 
        (3) Basic algorithm 
        (4) High level design for Read/Write scenario   
####(5) DEEP DIVE [15-20 min]   
        (1) Scaling the algorithm   
        (2) Scaling individual components:  
                -> Availability, Consistency and Scale story for each component 
                -> Consistency and availability patterns    
        (3) Think about the following components, how they would fit in and how it would help   
                a) DNS  
                b) CDN [Push vs Pull]   
                c) Load Balancers [Active-Passive, Active-Active, Layer 4, Layer 7] 
                d) Reverse Proxy    
                e) Application layer scaling [Microservices, Service Discovery] 
                f) DB [RDBMS, NoSQL]    
                        > RDBMS     
                            >> Master-slave, Master-master, Federation, Sharding, Denormalization, SQL Tuning   
                        > NoSQL 
                            >> Key-Value, Wide-Column, Graph, Document  
                                Fast-lookups:   
                                -------------   
                                    >>> RAM  [Bounded size] => Redis, Memcached 
                                    >>> AP [Unbounded size] => Cassandra, RIAK, Voldemort   
                                    >>> CP [Unbounded size] => HBase, MongoDB, Couchbase, DynamoDB  
                g) Caches   
                        > Client caching, CDN caching, Webserver caching, Database caching, Application caching, Cache @Query level, Cache @Object level    
                        > Eviction policies:    
                                >> Cache aside  
                                >> Write through    
                                >> Write behind 
                                >> Refresh ahead    
                h) Asynchronism 
                        > Message queues    
                        > Task queues   
                        > Back pressure 
                i) Communication    
                        > TCP   
                        > UDP   
                        > REST  
                        > RPC   
####(6) JUSTIFY [5 min] 
	(1) Throughput of each layer    
	(2) Latency caused between each layer   
	(3) Overall latency justification   
	
	
### Terminologies:  
    - Domain Name System (DNS): 
    - Relational Databases (RDBMS) and NoSQL Databases:
    - Database Sharding
    - Vertical and Horizontal Scaling:
    - Load Balancer
    - Database Replication
    - Cache
    - CDN (Content Delivery Network)
    - Multi Region Setup
    - Message Queue
    - Logging
    - Metrics - CPU, memory, disk I/o
    - Automation
    - CAP Theorem
    

### Latency Comparison Numbers(Dr. Dean):    
    L1 cache reference                           0.5 ns
    Branch mispredict                            5   ns
    L2 cache reference                           7   ns                      14x L1 cache
    Mutex lock/unlock                           25   ns
    Main memory reference                      100   ns                      20x L2 cache, 200x L1 cache
    Compress 1K bytes with Zippy            10,000   ns       10 us
    Send 1 KB bytes over 1 Gbps network     10,000   ns       10 us
    Read 4 KB randomly from SSD*           150,000   ns      150 us          ~1GB/sec SSD
    Read 1 MB sequentially from memory     250,000   ns      250 us
    Round trip within same datacenter      500,000   ns      500 us
    Read 1 MB sequentially from SSD*     1,000,000   ns    1,000 us    1 ms  ~1GB/sec SSD, 4X memory
    HDD seek                            10,000,000   ns   10,000 us   10 ms  20x datacenter roundtrip
    Read 1 MB sequentially from 1 Gbps  10,000,000   ns   10,000 us   10 ms  40x memory, 10X SSD
    Read 1 MB sequentially from HDD     30,000,000   ns   30,000 us   30 ms 120x memory, 30X SSD
    Send packet CA->Netherlands->CA    150,000,000   ns  150,000 us  150 ms
    
    Notes
    -----
    1 ns = 10^-9 seconds
    1 us = 10^-6 seconds = 1,000 ns
    1 ms = 10^-3 seconds = 1,000 us = 1,000,000 ns


### Back of the envelope estimations:
    Example: Estimate Twitter QPS and storage requirements
    Please note the following numbers are for this exercise only as they are not real numbers
    from Twitter.
    Assumptions:
    • 300 million monthly active users.
    • 50% of users use Twitter daily.
    • Users post 2 tweets per day on average.
    • 10% of tweets contain media.
    • Data is stored for 5 years.
    Estimations:
    Query per second (QPS) estimate:
    • Daily active users (DAU) = 300 million * 50% = 150 million
    • Tweets QPS = 150 million * 2 tweets / 24 hour / 3600 seconds = ~3500
    • Peek QPS = 2 * QPS = ~7000
    We will only estimate media storage here.
    • Average tweet size:
    • tweet_id 64 bytes
    • text 140 bytes
    • media 1 MB
    • Media storage: 150 million * 2 * 10% * 1 MB = 30 TB per day
    • 5-year media storage: 30 TB * 365 * 5 = ~55 PB
    
    
    
### Dos:
    • Always ask for clarification. Do not assume your assumption is correct.
    • Understand the requirements of the problem.
    • There is neither the right answer nor the best answer. A solution designed to solve the problems of a young startup is different from that of an established company with millions of users. Make sure you understand the requirements.
    • Let the interviewer know what you are thinking. Communicate with your interview.
    • Suggest multiple approaches if possible.
    • Once you agree with your interviewer on the blueprint, go into details on each component. Design the most critical components first.
    • Bounce ideas off the interviewer. A good interviewer works with you as a teammate.
    • Never give up.

### Dont's:
    • Don't be unprepared for typical interview questions.
    • Don’t jump into a solution without clarifying the requirements and assumptions.
    • Don’t go into too much detail on a single component in the beginning. Give the highlevel design first then drills down.
    • If you get stuck, don't hesitate to ask for hints.
    • Again, communicate. Don't think in silence.
    • Don’t think your interview is done once you give the design. You are not done until your interviewer says you are done. Ask for feedback early and often.

