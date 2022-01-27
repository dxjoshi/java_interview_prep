## Operating System:
- What is the main purpose of an operating system? Discuss different types? 
- The purpose of an operating system is to provide an environment in which a user can execute programs in a convenient and efficient manner. Besides that, an OS is responsible for the proper management of the system's resources (Memory, CPU, I/O devices, Files, etc.) to the various user tasks.    
- Based on various use-cases, different types of OS have been developed:
    - **Batch OS:** There is an operatoring program that groups the jobs into batches based on similar requirements. Usage: Payroll System
    - **Time-sharing OS:** Multiple users can concurrently interact with the system. Each user is allocated a time quantum (chunk) for execution, after which the OS context-switches to another user. eg. Unix  
    - **Distributed OS:** A distributed OS consists of multiple hardware units (independent CPU, Memory, I/O Units), with a single OS managing execution of processes in those various independent hardware systems. They are thus also known as loosely coupled systems.  
    - **Network OS:** These systems run on a server and provide the capability to manage data, users, groups, security, applications, and other networking functions. eg. Ubuntu Server   
    - **Real-time OS:** They are OS meant to handle mission-critical tasks. They have high responsiveness and fast processing time. The functionalities are limited and specific to the domain. e.g., Defense Systems, Air-Traffic Control      
    
- What is a socket, kernel and monolithic kernel ? 

- Difference between process and program and thread? Different types of process. 
- **Introduction to process:** A process is a program in execution or a process is an ‘active’ entity, as opposed to a program, which is considered to be a ‘passive’ entity.
  A single program can create many processes when running multiple times
    
  
- Define virtual memory, thrashing, threads.  
- What is RAID ? Different types. 
- What is a deadlock? Different conditions to achieve a deadlock. 
- What is fragmentation? Types of fragmentation. 
- What is spooling ? 
- What is semaphore and mutex (Differences might be asked)? Define Binary semaphore. 
- Belady’s Anomaly
- Starving and Aging in OS
- Why does trashing occur? 
- What is paging and why do we need it? 
- Demand Paging, Segmentation 
- Real Time Operating System, types of RTOS. 
- Difference between main memory and secondary memory. 
- Dynamic Binding 
- FCFS Scheduling 
- SJF Scheduling 
- SRTF Scheduling 
- LRTF Scheduling 
- Priority Scheduling 
- Round Robin Scheduling 
- Producer Consumer Problem 
- Banker’s Algorithm 
- Explain Cache
- Diff between direct mapping and associative mapping 

- Diff between multitasking and multiprocessing 
- **Multiprocessing:**
    - True parallelism is achieved via **Multiprocessing** as there physically exists multiple hardware units (CPU, cache, and even Memory) amongst which programs are shared.  
    - Multiprocessing aided systems have an added advantage of failure-tolerance. i.e. Even if one CPU fails, the system can keep running relying upon the other processors. 
    - Also one can gain significant gains in computation by horizontally scaling the system.    
- **Multiprogramming:** 
    - Multiple programs share single hardware resources. However, fake parallelism is achieved by context-switching between the processes.  
    - All the programs are placed in a queue for execution (also called job pool). The scheduler picks jobs one-by-one and executes till the time quantum expires, or the process is pre-empted by some external factors (I/O requirement, interrupt, etc.).    
    - The process is context-switched with the next one, thus preventing the CPU from being idle.   
- **Multithreading:**   
    - Multiple Threads running as part of the same process. Threads have common shared resources (memory, file descriptors, code segment, etc.). However, they have independent stack-space, program-counters, etc.
    - Threads are said to be lightweight processes, which run together in the same context. They also get context-switched.      


## Database Management System: 
- What is DBMS ? Mention advantages.. 
- What is Database? 
- What is a database system? 
- What is RDBMS ? Properties.. 
- Types of database languages 
- ACID properties (VVVVV IMP) 
- Difference between vertical and horizontal scaling 
- What is sharding 
- Keys in DBMS 
- Types of relationship 
- Data abstraction in DBMS, three levels of it 
- Indexing in DBMS 
- What is DDL (Data Definition Language) 
- What is DML (Data Manipulation Language)
- What is normalization ? Types of normalization.
- What is denormalization ? 
- What is functional dependency ? 
- E-R Model ? 
-  Conflict Serializability in DBMS .. 
- Explain Normal forms in DBMS 
- What is CCP ? (Concurrency Control Protocols) 
- Entity, Entity Type, Entity Set, Weak Entity Set.. 
- What are SQL commands ? Types of them.. 
- Nested Queries in SQL ? 
- What is JOIN .. Explain types of JOINs 
- Inner and Outer Join 
- Practice sql queries from leetcode
- Diff between 2 tier and 3 tier architecture 
- Diff between TRUNCATE and DELETE command .. 
- Difference between Intension and Extension in a DataBase
- Difference between share lock and exclusive lock, definition of lock 
- Answers to the above questions coming soon!.

## Computer Networks:
- Define network 
- What do you mean by network topology, and explain types of them 
- Define bandwidth, node and link ? 
- Explain TCP model .. 
- Layers of OSI model 
- Significance of Data Link Layer
- Define gateway, difference between gateway and router .. 
- What does ping command do ? 
- What is DNS, DNS forwarder, NIC, ? 
- What is MAC address ? 
- What is IP address, private IP address, public IP address, APIPA ? 
- Difference between IPv4 and IPv6
- What is subnet ? 
- Firewalls 
- Different type of delays 
- 3 way handshaking 
- Server-side load balancer
- RSA Algorithm 
- What is HTTP and HTTPS protocol ? 
- What is SMTP protocol ? 
- TCP and UDP protocol, prepare differences
- What happens when you enter “google.com” (very very famous question) 
- Hub vs Switch 
- VPN, advantages and disadvantages of it 
- LAN