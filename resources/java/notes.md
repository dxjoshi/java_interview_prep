## Java Interview Refresher:  

## Topics:
* [Thread Basics](#thread-basics)
* [ThreadPoolExecutor](#threadpoolexecutor)
* [ForkJoinPool](#forkjoinpool)
* [CompletionService](#completionservice)
* [Executor Factory Methods](#executor-factory-methods)
* [Concurrency Questions](#concurrency-questions)
* [Java Basics Questions](#java-basics-questions)
* [Iterators](#iterators)
* [Collections](#collections)
* [Object class](#object-class)
* [Objects Utility Class](#objects-utility-class)
* [Arrays Class](#arrays-class)
* [Collections Class](#collections-class)       
* [ConcurrentModificationException](#concurrentmodificationexception)       
* [Comparator and Comparable Interface](#comparator-and-comparable-interface)       
* [Java Basics](#java-basics)   
* [Interfaces](#interfaces)
* [Inheritance](#inheritance)
* [Exception Handling](#exception-handling)  
* [Polymorphism](#polymorphism)     
* [JVM Architecture](#jvm-architecture)   
* [Garbage Collection](#garbage-collection)
* [Reference Types](#reference-types)       
* [String Pool](#string-pool)       
* [Generics](#generics)     
* [Serialization](#serialization)       
* [Regular Expression](#regular-expression)           
* [Java 8](#java-8)     


### Thread Basics        
* Threads are managed by a thread-scheduler of JVM(Java Virtual Machine). [These threads are not directly mapped to a native OS thread](https://baptiste-wicht.com/posts/2010/05/java-concurrency-part-1-threads.html). 
* Threads can be created by either implementing java.lang.Runnable interface or extending java.lang.Thread class.     
    
        class CustomThread extends Thread {
            @Override
            public void run() {
                System.out.println(String.format("Extended thread class to create thread: %s", Thread.currentThread().getName()));
            }
        }
    
        public class ThreadCreation {
        
            public static void main(String[] args) {
                // by implementing Runnable interface
                Thread threadInstance = new Thread(()-> System.out.println(String.format("Implemented runnable interface to create thread: %s", Thread.currentThread().getName())));
                threadInstance.start();
        
                // by extending Thread class
                CustomThread threadInstanceTwo = new CustomThread();
                threadInstanceTwo.start();
            }
        }
        
        
* A thread can be in one of the following states:   
  **NEW:**  A thread that has not yet started is in this state.(thread object before start() gets called)  
  **RUNNABLE:** A thread executing in the Java virtual machine is in this state.(after call to start())    
  **BLOCKED:** A thread that is blocked waiting for a monitor lock is in this state.    
  **WAITING:** A thread that is waiting indefinitely for another thread to perform a particular action is in this state.    
  (calls to object.wait(), thread.join() or LockSupport.park())    
  **TIMED_WAITING:** A thread that is waiting for another thread to perform an action for up to a specified waiting time is in this state.  
  (calls to thread.sleep(long millis), wait(int timeout) or wait(int timeout, int nanos), thread.join(long millis), LockSupport.parkNanos, LockSupport.parkUntil)  
  **TERMINATED:** A thread that has exited is in this state.(either finished execution or was terminated abnormally)    

        public class ThreadState {
            static Thread parentThread;
            public static void main(String[] args) {
                Runnable childTask = () -> {
                    System.out.println("Current State: "+ parentThread.getState());   // parentThread is WAITING for childThread to complete
                };
        
                Runnable task = ()  -> {
                    sleepThread(3);
                    Thread childThread = new Thread(childTask);
                    childThread.start();
                    try {
                        // this joins childThread to current parentThread, 
                        // so it can't execute ahead until childThread finishes
                        childThread.join(); 
                    } catch (InterruptedException e) {
                        // https://stackoverflow.com/questions/3976344/handling-interruptedexception-in-java
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                };
        
                parentThread = new Thread(task);
                System.out.println("Current State: "+ parentThread.getState());   // NEW
                parentThread.start(); // another call to parentThread.start() will cause IllegalThreadStateException
                System.out.println("Current State: "+ parentThread.getState());   // RUNNABLE
                sleepThread(1);
                System.out.println("Current State: "+ parentThread.getState());   // TIMED_WAITING
                sleepThread(5);
                System.out.println("Current State: "+ parentThread.getState());   // TERMINATED
        
                // ------------------case for creating blocked state-------------
                Runnable infiniteTask = () -> {
                    synchronized (Runnable.class) {
                        while (true) {
        
                        }
                    }
                };
        
                Thread threadOne = new Thread(infiniteTask);
                Thread threadTwo = new Thread(infiniteTask);
                threadOne.start();
                sleepThread(1);
                threadTwo.start();
                sleepThread(1);
                System.out.println("Current State: "+ threadTwo.getState());   // BLOCKED as threadOne goes on infinitely
                System.exit(0);
        
            }
        
            public static void sleepThread(long timeoutInSec) {
                try {
                    TimeUnit.SECONDS.sleep(timeoutInSec);  // equivalent to Thread.sleep(timeout*1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

* thread.start() causes this thread to begin execution, the JVM calls the run() of this thread.     
  The result is that two threads are running concurrently: the current thread (which returns from the call to start() and the other thread which executes its run().        
  If we don’t override run() new thread object gets created and its run() is called.    
  If we override start(), run() method will not be called until called explicitly inside start().   
  If we call thread.run() explicitly, it will not execute in a new thread, but in the same thread.    
  If we call thread.start() a 2nd time, it throws **IllegalStateException()**   
  
        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * Subclasses of <code>Thread</code> should override this method.
         */
        @Override
        public void run() {
            if (target != null) {
                target.run();
            }
        }          
        
        
* **Spurious Wakeups:** A thread can also wake up without being notified, interrupted, or timing out.   
  
                  
### ThreadPoolExecutor
   
1. Thread pools address two different problems:  
    1. They usually provide improved performance when executing large numbers of asynchronous tasks, due to reduced per-task invocation overhead,
    2. They provide a means of bounding and managing the resources, including threads, consumed when executing a collection of tasks.

2. **Core and maximum pool sizes:**     
When a new task is submitted in method execute(Runnable), and fewer than corePoolSize threads are running, a new thread is created to handle the request, even if other worker threads are idle.  
If there are more than corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is full.  
By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool.  
By setting maximumPoolSize to an essentially unbounded value such as  Integer.MAX_VALUE, you allow the pool to accommodate an arbitrary number of concurrent tasks. 
By default, even core threads are initially created and started only when new tasks arrive, but this can be overridden dynamically using prestartCoreThread() or prestartAllCoreThreads().  You probably want to prestart threads if you construct the pool with a non-empty queue.  

3. **BlockingQueue:**    
    1. Any BlockingQueue may be used to transfer and hold submitted tasks.  The use of this queue interacts with pool sizing:
    2. If fewer than corePoolSize threads are running, the Executor always prefers adding a new thread rather than queuing.
    3. If corePoolSize or more threads are running, the Executor always prefers queuing a request rather than adding a new thread.
    4. If a request cannot be queued, a new thread is created unless this would exceed maximumPoolSize, in which case, the task will be rejected.
    5. There are three general strategies for queuing:  
        1. **Direct handoffs:**  
        A SynchronousQueue that hands off tasks to threads without otherwise holding them. Here, an attempt to queue a task will fail if no threads are immediately available to run it, so a new thread will be constructed.   
        There is a possibility of unbounded thread growth when commands continue to arrive on average faster than they can be processed.    
        
        2. **Unbounded queues:**    
        Using an unbounded queue, LinkedBlockingQueue(without a predefined capacity), will cause new tasks to wait in the queue when all corePoolSize threads are busy.      
        Thus, no more than corePoolSize threads will ever be created. (And the value of the maximumPoolSize therefore doesn't have any effect).     
        There is possibility of unbounded work queue growth when commands continue to arrive on average faster than they can be processed.      
        
        3. **Bounded queues:**      
        A bounded queue (ArrayBlockingQueue) helps prevent resource exhaustion when used with finite maximumPoolSizes, but can be more difficult to tune and control.Queue sizes and maximum pool sizes may be traded off for each other:       
        Using large queues and small pools minimizes CPU usage, OS resources, and context-switching overhead, but can lead to artificially low throughput.      
        If tasks frequently block (for example if they are I/O bound), a system may be able to schedule time for more threads than you otherwise allow.     
        Use of small queues generally requires larger pool sizes, which keeps CPUs busier but may encounter unacceptable scheduling overhead, which also decreases throughput.      

                List<Runnable> initialList = Arrays.asList(Runnables.simpleRunnable, Runnables.simpleRunnable, Runnables.simpleRunnable);
                //Creates an ArrayBlockingQueue with the given (fixed) capacity, the specified access policy and initially containing the elements of the given collection, added in traversal order of the collection's iterator.
                BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(10, true, initialList);
                //Creates a LinkedBlockingQueue with a capacity of Integer#MAX_VALUE.
                BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();
                //Creates a SynchronousQueue with the specified fairness policy.
                BlockingQueue<Runnable> synchronousQueue = new SynchronousQueue<>(true);

4. **Rejected tasks:**      
    1. New tasks submitted in execute(Runnable) will be rejected when the Executor has been shut down, and also when the Executor uses finite bounds for both maximum threads and work queue capacity, and is saturated. In either case, rejectedExecution(Runnable, ThreadPoolExecutor) of its RejectedExecutionHandler is invoked.
    2. Four predefined rejected handler policies are provided:
        1. In default **ThreadPoolExecutor.AbortPolicy**, the handler throws a runtime RejectedExecutionException upon rejection.
        2. In **ThreadPoolExecutor.CallerRunsPolicy**, the thread that invokes execute() itself runs the task. This provides a simple feedback control mechanism that will slow down the rate that new tasks are submitted. 
        3. In **ThreadPoolExecutor.DiscardPolicy**, a task that cannot be executed is simply dropped.
        4. In **ThreadPoolExecutor.DiscardOldestPolicy**, if the executor is not shut down, the task at the head of the work queue is dropped, and then execution is retried (which can fail again, causing this to be repeated.) 

                //A handler that may be invoked by a ThreadPoolExecutor when execute() cannot accept a task.
                //This may occur when no more threads or queue slots are available because their bounds would be exceeded, or upon shutdown of the Executor.
                //In the absence of other alternatives, the method may throw an unchecked RejectedExecutionException, which will be propagated to the caller of execute().
                RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println("Unable to process the runnable: "+ r);
                    }
                };
        
                RejectedExecutionHandler discardOldestPolicy =  new ThreadPoolExecutor.DiscardOldestPolicy();

5. **Hook methods:**
    1. This class provides protected overridable beforeExecute(Thread, Runnable) and afterExecute(Runnable, Throwable) that are called before and after execution of each task.   
    These can be used to manipulate the execution environment; for example, reinitializing ThreadLocals, gathering statistics, or adding log entries.   
    2. Additionally, terminated() can be overridden to perform any special processing that needs to be done once the Executor has fully terminated.      
    3. If hook or callback methods throw exceptions, internal worker threads may in turn fail and abruptly terminate.       
        
            //Constructs a new Thread.  Implementations may also initialize priority, name, daemon status, ThreadGroup, etc.
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r);
                }
            };
    
            //This factory creates all new threads used by an Executor in the same ThreadGroup. If there is a  java.lang.SecurityManager, it uses the group of System#getSecurityManager, else the group of the thread invoking this defaultThreadFactory method.
            //Each new thread is created as a non-daemon thread with priority set to the smaller of Thread.NORM_PRIORITY and the maximum priority permitted in the thread group.
            //New threads have names accessible via name  pool-N-thread-M, where N is the sequence number of this factory, and M is the sequence number of the thread created by this factory.
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
            ThreadFactory priviledgedThradFactory = Executors.privilegedThreadFactory();
    
            //corePoolSize - #threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set.
            //maximumPoolSize - maximum #threads to allow in the pool
            //keepAliveTime - when #threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
            //unit - the time unit for the keepAliveTime argument
            //workQueue - the queue to use for holding tasks before they are executed.  This queue will hold only  Runnable tasks submitted by the execute() method.
            //threadFactory - the factory to use when the executor creates a new thread
            //rejectionHandler - the handler to use when execution is blocked because the thread bounds and queue capacities are reached
            ThreadPoolExecutor threadPoolExecutorOne = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory(), discardOldestPolicy);
            ThreadPoolExecutor threadPoolExecutorTwo = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), rejectedExecutionHandler);
            ThreadPoolExecutor threadPoolExecutorThree = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory());
            ThreadPoolExecutor threadPoolExecutorFour = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
    
### Fork Join F/W   

1. **Fork** - Recursively breaking the task into smaller independent subtasks until they are simple enough to be executed asynchronously.   
2. **Join** - Results of all subtasks are recursively joined into a single result, or in the case of a task which returns void, the program simply waits until every subtask is executed.   
3. ForkJoinTask is the base type for tasks executed inside ForkJoinPool. In practice, one of its two subclasses should be extended: the RecursiveAction for void tasks and the RecursiveTask<V> for tasks that return a value.
   
        Result solve(Problem problem) {
            if (problem is small)
                directly solve problem
            else {
                split problem into independent parts
                fork new subtasks to solve each part
                join all subtasks
                compose result from subresults
            }
        }


### ForkJoinPool    

1. ForkJoinPool differs from other ExecutorService due to work-stealing: all threads in the pool attempt to find and execute tasks submitted to the pool and/or created by other active tasks (eventually blocking waiting for work if none exist). Simply put - free threads try to “steal” work from deques of busy threads.
2. This enables efficient processing when most tasks spawn other subtasks (as do most ForkJoinTasks, as well as when many small tasks are submitted to the pool from external clients.     
3. Especially when asyncMode is true in constructors, ForkJoinPools may also be appropriate for use with event-style tasks that are never joined.      
4. A static commonPool() is available and appropriate for most applications. The common pool is used by any ForkJoinTask that is not explicitly submitted to a specified pool.
   Using the common pool normally reduces resource usage (its threads are slowly reclaimed during periods of non-use, and reinstated upon subsequent use). 
5. For applications that require separate or custom pools, a ForkJoinPool may be constructed with a given target parallelism level; by default, equal to the number of available processors.    
   The pool attempts to maintain enough active (or available) threads by dynamically adding, suspending, or resuming internal worker threads, even if some tasks are stalled waiting to join others.However, no such adjustments are guaranteed in the face of blocked I/O or other unmanaged synchronization.   

        RecursiveAction recursiveAction = new RecursiveAction() {
            @Override
            protected void compute() {
                System.out.println("Dummy action");
            }
        };

        RecursiveTask<Integer> recursiveTask = new RecursiveTask<Integer>() {
            int val = 1;
            @Override
            protected Integer compute() {
                return val*2;
            }
        };

        CountedCompleter<Integer> countedCompleter = new CountedCompleter<Integer>() {
            @Override
            public void compute() {
                System.out.println("NoOp");
            }
        };

        ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;

        //parallelism - the parallelism level. For default value, use java.lang.Runtime#availableProcessors()
        //factory - the factory for creating new threads. For default value, use defaultForkJoinWorkerThreadFactory
        //handler - the handler for internal worker threads that terminate due to unrecoverable errors encountered while executing tasks. For default value, use null.
        //asyncMode - if true, establishes local FIFO scheduling mode for forked tasks that are never joined. else LIFO (by default)
        //public ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, UncaughtExceptionHandler handler,boolean asyncMode) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4, defaultForkJoinWorkerThreadFactory, null, true);

        //Returns the common pool instance. This pool is statically constructed; its run state is unaffected by attempts to shutdown() or shutdownNow().
        //However this pool and any ongoing processing are automatically terminated upon program System.exit().
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        //Arranges for (asynchronous) execution of the given task.
        forkJoinPool.execute(recursiveTask);
        
        //Submits a ForkJoinTask for execution.
        forkJoinPool.submit(recursiveTask);
        
        //Performs the given task, returning its result upon completion.
        //If the computation encounters an unchecked Exception or Error, it is rethrown as the outcome of this invocation.
        //Rethrown exceptions behave in the same way as regular exceptions, but, when possible, contain stack traces of both the current thread as well as the thread actually encountering the exception;minimally only the latter.
        forkJoinPool.invoke(recursiveTask);


### CompletionService   
1. A service that decouples the production of new asynchronous tasks from the consumption of the results of completed tasks. 
2. Producers submit tasks for execution. Consumers take completed tasks and process their results in the order they complete.  
   
           ExecutorService executorService = Executors.newFixedThreadPool(2);
           BlockingQueue unboundedBlockingQueue = new LinkedBlockingQueue();
   
           //It can for example be used to manage asynchronous I/O, in which tasks that perform reads are submitted in one part of a program or system, and then acted upon in a different part of the program when the reads complete, possibly in a different order than they were requested.
           //A CompletionService that uses a supplied Executor to execute tasks.  This class arranges that submitted tasks are, upon completion, placed on a queue accessible using take.
           ExecutorCompletionService completionService = new ExecutorCompletionService(executorService, unboundedBlockingQueue);
   
           for (int i = 1; i < 11; i++) {
               completionService.submit(Callables.simpleCallable);
           }
   
           for (int i = 1; i < 11; i++) {
               try {
                   completionService.take().get();
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                   e.printStackTrace();
               } catch (ExecutionException e) {
                   e.printStackTrace();
               }
           }
        

### Executor Factory Methods

        /*--------------------------------ThreadPoolExecutor-----------------------------------*/

        //return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory));
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());

        //return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        //At any point, at most nThreads threads will be active processing tasks. If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is available.
        //If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.  The threads in the pool will exist until it is explicitly shutdown.
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5, Executors.defaultThreadFactory());

        //return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
        //Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available, and uses the provided ThreadFactory to create new threads when needed.
        // USUALLY FOR SHORT-LIVED TASKS
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

        /*--------------------------------ScheduledThreadPoolExecutor-----------------------------------*/

        //super(1, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue(), threadFactory);
        //Creates a single-threaded executor that can schedule commands to run after a given delay, or to execute periodically.
        //Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time.
        //Unlike the otherwise equivalent newScheduledThreadPool(1) the returned executor is guaranteed not to be reconfigurable to use additional threads.
        ExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());

        //super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue(), threadFactory);
        //Creates a thread pool that can schedule commands to run after a given delay, or to execute periodically.
        ExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(5, Executors.defaultThreadFactory());

        /*--------------------------------ForkJoinPool-----------------------------------*/

        //return new ForkJoinPool(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
        //Creates a thread pool that maintains enough threads to support the given parallelism level, and may use multiple queues to reduce contention.
        //The parallelism level corresponds to the maximum number of threads actively engaged in, or available to engage in, task processing. The actual number of threads may grow and shrink dynamically.
        //A work-stealing pool makes no guarantees about the order in which submitted tasks are executed.
        ExecutorService newWorkStealingPool = Executors.newWorkStealingPool(4);

        
* Executors typically manage a pool of threads and are capable of running asynchronous tasks using that pool.    
* Executors keep listening for new tasks and **need to be stopped explicitly**. An ExecutorService provides two methods for that purpose:      
    1. **shutdown()** waits for currently running tasks to finish.   
    2. **shutdownNow()** interrupts all running tasks and shut the executor down immediately.    

            try {
                System.out.println("Shutting down executor");
            // shutdown() initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted. 
            // Invocation has no additional effect if already shut down. This method does not wait for previously submitted tasks to complete execution.
                executorService.shutdown();
    
            // awaitTermination() blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, 
            // or the current thread is interrupted, whichever happens first.
                executorService.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Tasks interrupted");
            } finally {
            // isTerminated() returns true if all tasks have completed following shut down.
                if (!executorService.isTerminated()) {
                    System.out.println("Cancelling all pending tasks");
                }
    
            // shutdownNow() attempts(not guarantees) to stop all actively executing tasks, halts the processing of waiting tasks, 
            // and returns a list of the tasks that were awaiting execution. It does not wait for actively executing tasks to terminate.   
            // For example, typical implementations will cancel via Thread.interrupt(), so any task that fails to respond to interrupts may never terminate.
                executorService.shutdownNow();
                System.out.println("shutdown complete");
            }

* The class Executors provides convenient factory methods for creating different kinds of executor services.    

* **Callables** are functional interfaces just like runnables but instead of being void they return a value.    
* ExecutorService.submit() doesn't wait until the callable task completes, rather returns a **Future** representing the pending results of the task. 

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> callableResult = executorService.submit(simpleCallable);

* ExecutorService also has invokeAll() and invokeAny() methods which takes in a collection of tasks.

        ExecutorService executorService = Executors.newWorkStealingPool();
        ExecutorService executorServiceTwo = Executors.newWorkStealingPool();
        List<Callable<Integer>> tasks = Arrays.asList(simpleCallable, simpleCallable, simpleCallable);
        try {
            // invokeAll(tasks) executes the given tasks, returning a list of Futures holding their status and results when all complete(either normally or by throwing an exception).
            // Future.isDone() is true for each element of the returned list.
            // The results of this method are undefined if the given collection is modified while this operation is in progress.
            List<Future<Integer>> results = executorService.invokeAll(tasks);

            // invokeAll(tasks, 10, TimeUnit.SECONDS) executes the given tasks, returning a list of Futures holding their status and results when all complete(either normally or by throwing an exception) or the timeout expires, whichever happens first.
            // Upon return, tasks that have not completed are cancelled.
            List<Future<Integer>> resultsWithTimeOut = executorServiceTwo.invokeAll(tasks, 10, TimeUnit.SECONDS);

            // Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do.
            // Upon normal or exceptional return,tasks that have not completed are cancelled.
            Integer result = executorService.invokeAny(tasks);

            // Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do before the given timeout elapses.
            // Upon normal or exceptional return, tasks that have not completed are cancelled.
            Integer resultTwo = executorService.invokeAny(tasks, 10, TimeUnit.SECONDS);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Task interrupted!!");
        } catch (ExecutionException ex) {   // if no task successfully completes
            ex.printStackTrace();
        } catch (TimeoutException ex) {     // if the given timeout elapses before any task successfully completes
            ex.printStackTrace();
        } catch (RejectedExecutionException ex) {
            ex.printStackTrace();           // if tasks cannot be scheduled for execution
        }

* Future has following important methods:
    1. **get()**, a blocking method, will return the task's result upon successful completion.   
    2. **isDone()** tells if the future has already finished execution.

        public static <T> T getResult(Future<T> callableResult)  {
            T result = null;
            try {
                // isDone() returns true if this task completed due to normal termination, an exception, or cancellation
                System.out.println(callableResult.isDone());
                // get() waits if necessary for the computation to complete, and then retrieves its result.
                result = callableResult.get();
    
                // get(long timeout, TimeUnit unit) waits if necessary for at most the given time for the computation to complete, 
                // and then retrieves its result, if available.
                result = callableResult.get(1, TimeUnit.SECONDS);
    
            } catch (InterruptedException e) {  // if the current thread was interrupted while waiting
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {    // if the current thread was interrupted while waiting
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                System.out.println("Due to callableResult.get(1, TimeUnit.SECONDS)");
            }
            return result;
        }
  
* A ScheduledExecutorService is capable of scheduling tasks to run either periodically(scheduleAtFixedRate(), scheduleWithFixedDelay()) or once(schedule()) after a certain amount of time has elapsed. 
* scheduleAtFixedRate() doesn't take into account the actual duration of the task. So if you specify a period of one second but the task needs 2 seconds to be executed then the thread pool will working to capacity very soon.

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // Creates and executes a ScheduledFuture that becomes enabled after the given delay.
        ScheduledFuture<Integer> scheduledCallableResult = executorService.schedule(Callables.simpleCallable, 5, TimeUnit.SECONDS);

        long initialDelay = 1;
        long delay = 2;

        // Creates and executes a periodic action that executes after the given initial delay, and subsequently with the given delay between the termination of one execution and the commencement of the next.
        // If any execution of the task encounters an exception, subsequent executions are suppressed.Otherwise, the task will only terminate via cancellation or termination of the executor.
        executorService.scheduleWithFixedDelay(Runnables.simpleRunnable, initialDelay, delay, TimeUnit.SECONDS);

        // Creates and executes a periodic action whose executions commence after initialDelay then initialDelay+period and so on.
        // If any execution of the task encounters an exception, subsequent executions are suppressed. Otherwise, the task will only terminate via cancellation or termination of the executor.
        // If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute.
        executorService.scheduleAtFixedRate(Runnables.simpleRunnable, initialDelay, delay, TimeUnit.SECONDS);


        TimeUnit.SECONDS.sleep(2);
        // getDelay() returns the remaining delay associated with the ScheduledFuture object, in the given time unit. 
        // zero or negative values indicate that the delay has already elapsed. 
        System.out.printf("Remaining delay: %d sec%n", scheduledCallableResult.getDelay(TimeUnit.SECONDS));

* Internally Java uses a so called monitor also known as monitor lock or intrinsic lock in order to manage synchronization.  
  This monitor is bound to an object, e.g. when using synchronized methods each method share the same monitor of the corresponding object.  
  All implicit monitors implement the reentrant characteristics. Reentrant means that locks are bound to the current thread.     
  A thread can safely acquire the same lock multiple times without running into deadlocks (e.g. a synchronized method calls another synchronized method on the same object).      

        int count = 0;
        int countThreadSafe = 0;
    
        private final Object lock = new Object();
        Runnable increment = () -> count += 1;
        Runnable syncIncrement = () -> {
            synchronized (lock) {
                countThreadSafe += 1;
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        SynchronizationAndLocks obj = new SynchronizationAndLocks();
        IntStream.range(0, 10000)
                .forEach( i -> executorService.submit(obj.increment));

        ExecutorServiceTutorial.shutdown(executorService);
        // received 9975 coz 2 threads share a mutable variable 'count' without synchronizing the access to this variable which results in a **race condition**.
        System.out.println("Should be 10000, but got " + obj.count);

        obj.count = 0;
        ExecutorService executorServiceTwo = Executors.newFixedThreadPool(2);
        IntStream.range(0, 10000)
                .forEach( i -> executorServiceTwo.submit(obj.syncIncrement));

        ExecutorServiceTutorial.shutdown(executorServiceTwo);
        System.out.println("Should be 10000, but got " + obj.countThreadSafe); // 10000 as the increment method is thread safe now

* Concurrency API supports explicit locks specified by the Lock interface, for finer grained lock control thus are more expressive than implicit monitors(synchronized).   
  1. **ReentrantLock**  
            ReentrantLock reentrantLock = new ReentrantLock();
    
            //tryLock() acquires the lock only if it is not held by another thread at the time of invocation, but doesn't wait for the lock.
            //If the current thread already holds this lock then hold count is incremented by one and the method returns true, else will immediately return false.
            if (reentrantLock.tryLock()) {
                //Acquires the lock if it is not held by another thread and returns immediately, setting the lock hold count to one.
                //If the current thread already holds the lock then the hold count is incremented by one and the method returns immediately.
                //If the lock is held by another thread then the current thread lies dormant until the lock has been acquired, at which time the lock hold count is set to one.
                reentrantLock.lock();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    //If the current thread is the holder of this lock then the hold count is decremented.
                    //If the hold count is now zero then the lock is released.
                    //If the current thread is not the holder of this lock then IllegalMonitorStateException is thrown.
                    reentrantLock.unlock();
                }
            }
    
            System.out.println("Locked: " + reentrantLock.isLocked()); // Only to check if this lock is held by any thread.
            System.out.println("Held by me: " + reentrantLock.isHeldByCurrentThread()); // Only to check if this lock is held by the current thread.
 
  2. **ReentrantReadWriteLock**     
     The idea behind read-write locks is that it's usually safe to read mutable variables concurrently as long as nobody is writing to this variable.   
     So the read-lock can be held simultaneously by multiple threads as long as no threads hold the write-lock. This can improve performance and throughput in case that reads are more frequent than writes.      

            ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            Map<String, String> data = new HashMap<>();
            Runnable writeTask = () -> {
                //Acquires the write lock if neither the read nor write lock are held by another thread and returns immediately, setting the write lock hold count to one.
                //If the current thread already holds the write lock then the hold count is incremented by one and the method returns immediately.
                //If the lock is held by another thread then the current thread becomes disabled for thread scheduling purposes and lies dormant until the write lock has been acquired, at which time the write lock hold count is set to one.
                reentrantReadWriteLock.writeLock().lock();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    data.put("key", "value");
                } catch (Exception ex) {
                    System.out.println("Error while updating data");
                } finally {
                    //If the current thread is the holder of this lock then the hold count is decremented.
                    //If the hold count is now zero then the lock is released.
                    //If the current thread is not the holder of this lock then IllegalMonitorStateException is thrown.
                    reentrantReadWriteLock.writeLock().unlock();
                }
            };
            executorService.submit(writeTask);
    
            Runnable readTask = () -> {
                //Acquires the read lock if the write lock is not held by another thread and returns immediately.
                //If the write lock is held by another thread then the current thread waits until the read lock has been acquired.
                reentrantReadWriteLock.readLock().lock();
                try {
                    data.get("key");
                } catch (Exception ex) {
                    System.out.println("Error while fetching data");
                } finally {
                    // If the number of readers is now zero then the lock is made available for write lock attempts.
                    reentrantReadWriteLock.readLock().unlock();
                }
            };
            IntStream.range(1, 5).forEach(i -> executorService.submit(readTask));

  3. **StampedLock**      
     In contrast to ReadWriteLock the locking methods of a StampedLock return a stamp represented by a long value.      
     You can use these stamps to either release a lock or to check if the lock is still valid. Additionally stamped locks support another lock mode called optimistic locking.      
     Keep in mind that stamped locks don't implement reentrant characteristics. Each call to lock returns a new stamp and blocks if no lock is available even if the same thread already holds a lock. 
     So you have to pay particular attention not to run into deadlocks.

            StampedLock stampedLock = new StampedLock();
            ExecutorService executorServiceTwo = Executors.newFixedThreadPool(2);
            Map<String, String> map = new HashMap<>();
            Runnable writeTask = () -> {
                //Possibly blocks waiting for exclusive access, returning a stamp that can be used in unlockWrite() to release the lock. Untimed and timed versions of tryWriteLock are also provided.
                //When the lock is held in write mode, no read locks may be obtained, and all optimistic read validations will fail.
                long stamp = stampedLock.writeLock();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    map.put("key", "value");
                } catch (Exception ex) {
                    System.out.println("Error while updating data");
                } finally {
                    //If the lock state matches the given stamp, releases the corresponding mode of the lock.
                    stampedLock.unlock(stamp);
                }
            };
            executorServiceTwo.submit(writeTask);
    
            Runnable readTask = () -> {
                //Possibly blocks waiting for non-exclusive access, returning a stamp that can be used in unlockRead() to release the lock.
                //Untimed and timed versions of tryReadLock() are also provided.
                long stamp = stampedLock.readLock();
                try {
                    map.get("key");
                } catch (Exception ex) {
                    System.out.println("Error while fetching data");
                } finally {
                    //If the lock state matches the given stamp, releases the corresponding mode of the lock.
                    stampedLock.unlock(stamp);
                }
            };
            IntStream.range(1, 5).forEach(i -> executorServiceTwo.submit(readTask));
            ExecutorServiceTutorial.shutdown(executorServiceTwo);

     An optimistic read lock is acquired by calling **tryOptimisticRead()** which always returns a stamp without blocking the current thread, no matter if the lock is actually available.       
     If there's already a write lock active the returned stamp equals zero. You can always check if a stamp is valid by calling **lock.validate(stamp)**.    
     An optimistic lock doesn't prevent other threads to obtain a write lock instantaneously, waiting for the optimistic read lock to be released.  
     From this point the optimistic read lock is no longer valid. Even when the write lock is released the optimistic read locks stays invalid.     
     So when working with optimistic locks you have to validate the lock every time after accessing any shared mutable variable to make sure the read was still valid.

            Runnable readTask = () -> {
              //Returns a non-zero stamp only if the lock is not currently held in write mode. validate() returns true if the lock has not been acquired in write mode since obtaining a given stamp.  
              //This mode can be thought of as an extremely weak version of a read-lock, that can be broken by a writer at any time.  
              //The use of optimistic mode for short read-only code segments often reduces contention and improves throughput.  However, its use is inherently fragile.
              //Optimistic read sections should only read fields and hold them in local variables for later use after validation. 
              //Fields read while in optimistic mode may be wildly inconsistent, so usage applies only when you are familiar enough with data representations to check consistency and/or repeatedly invoke method validate().
              long stamp = stampedLock.tryOptimisticRead();
              try {
                  System.out.println("Optimistic lock valid: " + stampedLock.validate(stamp));
                  TimeUnit.SECONDS.sleep(1);
                  System.out.println("Optimistic lock valid: " + stampedLock.validate(stamp));
                  TimeUnit.SECONDS.sleep(2);
                  System.out.println("Optimistic lock valid: " + stampedLock.validate(stamp));
              } catch (Exception ex) {
                  System.out.println("Error while fetching data");
              } finally {
                  stampedLock.unlock(stamp);
              }
            };
     
     StampedLock provides non-blocking **tryConvertToWriteLock()** for converting a read lock into a write lock without unlocking and locking again:   

            long stamp = stampedLock.readLock();
            try {
                //If the lock state matches the given stamp, performs one of the following actions: 
                //If the stamp represents holding a write lock, returns it.  
                //Or, if a read lock, if the write lock is available, releases the read lock and returns a write stamp.
                //Or, if an optimistic read, returns a write stamp only if immediately available. This method returns zero in all other cases.
                stamp = stampedLock.tryConvertToWriteLock(stamp);
                if ( stamp == 0L) {
                    // couldn't get write lock
                    stamp = stampedLock.writeLock();
                }
            } catch (Exception ex) {
                System.out.println("Exception while getting lock");
            } finally {
                stampedLock.unlock(stamp);
            }
     
* **Semaphores** are often used to restrict the number of threads than can access some (physical or logical) resource, by maintaining set of permits.  
  Each acquire() blocks if necessary until a permit is available, and then takes it. Each release() adds a permit, potentially releasing a blocking acquirer.   
  However, no actual permit objects are used; the Semaphore just keeps a count of the number available and acts accordingly.    

        Semaphore semaphore = new Semaphore(5);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
  
        Runnable task = () -> {
            boolean hasPermit = false;
            //Acquires the given number of permits, if they are available, and returns true immediately, reducing the number of available permits by the given amount.
            //If insufficient permits are available then this method will return false immediately and the number of available permits is unchanged.
            hasPermit = semaphore.tryAcquire(1);
            //acquire() acquires a permit, if available and returns immediately, reducing the number of available permits by one.
            //If no permit is available then the current thread waits/blocks until other thread invokes the release() method for this semaphore and the current thread is next to be assigned a permit OR some other thread interrupts the current thread.
            //semaphore.acquire();

            //acquireUninterruptibly(permits) the given number of permits from this semaphore, blocking until all are available.
            //If the current thread is interrupted while waiting for permits then it will continue to wait and its position in the queue is not affected.  
            //When the thread does return from this method its interrupt status will be set.
            //semaphore.acquireUninterruptibly(2);
            try {
                if (hasPermit) {
                    System.out.println("Acquired a permit");
                    TimeUnit.SECONDS.sleep(5);
                } else {
                    System.out.println("Couldn't acquire permit");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                ex.printStackTrace();
            } finally {
                //Releases a permit, increasing the number of available permits by one.  If any threads are trying to acquire a permit, then one is selected and given the permit that was just released.
                if (hasPermit) {
                    semaphore.release();
                }
            }
        };


* Atomic classes that support lock-free thread-safe programming on single variables, make heavy use of compare-and-swap (CAS), an atomic instruction directly supported by most modern CPUs(much faster than synchronizing via locks).     
  **boolean compareAndSet(expectedValue, updateValue)** (atomically sets a variable to updateValue if it currently holds the expectedValue, reporting true on success). 
  
* **Volatile Keyword:**  
  1. If one thread updates the shared variable/object, we cannot know for sure when exactly this change will be visible to the other thread, due to CPU caching. Each thread that uses the variable makes a local copy (i.e. cache) of its value on the CPU itself. 
  2. **Visibility** When a variable is marked volatile, the JVM guarantees that each write operation's result isn't written in the local memory but rather in the main memory. 
  3. **Happens-before relationship:** This relationship is simply a guarantee that memory writes by one specific statement are visible to another specific statement. 
  4. Volatile keyword guarantees visibility and happens before order, but does not guarantee mutual exclusion.(**synchronized keyword ensures both visibility and mutual exclusion but is slow**)   

* **Synchronized Keyword:** 
  1. For a synchronized block, the lock is acquired on the object specified in the parentheses after the synchronized keyword
     For a synchronized static method, the lock is acquired on the .class object
     For a synchronized instance method, the lock is acquired on the current instance of that class i.e. this instance
    

### Concurrency Questions   
* [Concurrency vs. Parallelism](https://stackoverflow.com/questions/1050222/what-is-the-difference-between-concurrency-and-parallelism)
* [Atomic vs. Volatile vs. Synchronized](https://stackoverflow.com/questions/9749746/what-is-the-difference-between-atomic-volatile-synchronized)   

### Java Basics Questions   
* [Is java pass-by-value?](https://stackoverflow.com/questions/7893492/is-java-really-passing-objects-by-value)       
* [jdk vs. jre vs. jvm](https://www.geeksforgeeks.org/differences-jdk-jre-jvm/)
    
### Iterators

        //public interface Iterator<E>
        //public interface Spliterator<T>
        //public interface ListIterator<E> extends Iterator<E>

        List<Integer> list = Arrays.asList(1,2,3);

        // An iterator over a collection which allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
        //hasNext() - Returns true if this list iterator has more elements
        //next() - Returns the next element in the list and advances the cursor position.
        //remove() - Removes from the underlying collection the last element returned by this iterator and can be called only once per call to next().
        //The behavior is unspecified if the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
        Iterator<Integer> iterator = list.iterator();

        //An object for traversing and partitioning elements of a source. A Spliterator may traverse elements individually using tryAdvance() or sequentially in bulk using forEachRemaining().
        //It may also partition off some of its elements (using trySplit() as another Spliterator, to be used in possibly-parallel operations.
        //Operations using a Spliterator that cannot split, or does so in a highly imbalanced or inefficient manner, are unlikely to benefit from parallelism.
        //Traversal and splitting exhaust elements, each Spliterator is useful for only a single bulk computation.
        //Spliterator API was designed to support efficient parallel traversal in addition to sequential traversal, by supporting decomposition as well as single-element iteration.
        //In addition, the protocol for accessing elements via a Spliterator is designed to impose smaller per-element overhead than Iterator, and to avoid the inherent race involved in having separate methods for hasNext() and next().
        Spliterator<Integer> spliterator = list.spliterator();

        //An iterator for lists that allows the programmer to traverse the list in either direction, modify the list during iteration, and obtain the iterator's current position in the list.
        //It has no current element; its cursor position always lies between the element that would be returned by a call to previous() and the element that would be returned by a call to next().
        //hasNext() - Returns true if this list iterator has more elements
        //next() - Returns the next element in the list and advances the cursor position.
        //hasPrevious() - Returns {@code true} if this list iterator has more elements when traversing in reverse direction.
        //previous() - Returns the previous element in the list and moves the cursor position backwards.
        //nextIndex() - Returns the index of the element that would be returned by next(). Returns list size if the list iterator is at the end of the list.
        //previousIndex() - Returns the index of the element that would be returned by previous(). Returns -1 if the list iterator is at the beginning of the list.
        //
        //remove() - Removes from the list the last element that was returned by next() or previous().  This call can only be made once per call to next() or previous().
        //It can be made only if add() has not been called after the last call to next() or previous().
        //
        //set() - Replaces the last element returned by next() or previous() with the specified element.
        //This call can be made only if neither remove() or add() have been called after the last call to next() or previous().
        //
        //add() - Inserts the specified element into the list immediately before the element that would be returned by next(), if any, and after the element that would be returned by previous(), if any.
        //If the list contains no elements, the new element becomes the sole element on the list.
        //The new element is inserted before the implicit cursor: a subsequent call to next() would be unaffected, but previous() would return the new element.
        //It increases by one the value that would be returned by nextIndex() previousIndex()
        ListIterator<Integer> listIterator = list.listIterator();

### Object class    

        //ArrayList<Integer> obj = new ArrayList<>(10);
        Object obj = new Object();

        //getClass() - Returns the runtime class of this Object. The returned Class object is the object that is locked by static synchronized methods of the represented class.
        Class<? extends Object> objectClass = obj.getClass();
        System.out.println("objectClass: " + objectClass);

        //hashCode() - Returns a hash code value for the object. The general contract of {@code hashCode} is:
        //Multiple invocations of hashCode() must consistently return the same integer, if no information used in equals() comparisons on the object is modified, for the same run of java application.
        //If two objects are equal according to the equals(), then calling the hashCode() on each of the two objects must produce the same integer result.
        //If two objects are unequal according to the equals(), then calling the hashCode() on both objects CAN/CAN NOT produce the same integer result.
        obj.hashCode();

        //equals() - Indicates whether some other object is "equal to" this one.
        //The equals() method implements an equivalence relation on non-null object references:
        //x.equals(x) should return true(reflexive)
        //y.equals(x)==true iff x.equals(y)==true (symmetric)
        //if x.equals(y)==true and y.equals(z)==true then x.equals(z)==true(transitive) 
        //multiple invocations of x.equals(y) should consistently return true/false (consistent)
        //x.equals(null)==false
        obj.equals();

        //clone() - Creates and returns a shallow copy of this object.
        //First, if the class of this object does not implement the interface Cloneable, then a CloneNotSupportedException is thrown.
        //Also, the class whose object’s copy is to be made must have a public clone method in it or in one of its parent class.
        //Also need to copy any mutable objects that comprise the internal structure of the object being cloned and replacing the references to these objects with references to the copies.
        obj.clone();    //object's clone() can't be called directly as it will throw Runtime Exception

        //toString() - Returns a string representation of the object. Default form:- getClass().getName() + '@' + Integer.toHexString(hashCode())
        obj.toString();

        //notify() - If any threads are waiting on this object(by calling wait()), one of them is chosen to be awakened(arbitrarily chosen)
        //The awakened thread will not be able to proceed until the current thread relinquishes the lock on this object.
        //This method should only be called by a thread that is the owner of this object's monitor, in one of three possible ways:
        //1. By executing a synchronized instance method of that object.
        //2. By executing the body of a synchronized statement that synchronizes on the object.
        //3. For objects of type Class by executing a synchronized static method of that class.
        obj.notify(); //IllegalMonitorStateException if the current thread is not the owner of this object's monitor.

        //notifyAll() - Same as notify() except it wakes up all threads that are waiting on this object's monitor.
        obj.notifyAll();    //IllegalMonitorStateException if the current thread is not the owner of this object's monitor.

        //wait() - This method causes the current thread **T** to place itself in the wait set for this object and then to relinquish any and all synchronization claims on this object and becomes disabled for thread scheduling purposes and lies dormant until one of four things happens:
        //Other thread invokes notify() method for this object and T happens to be arbitrarily chosen as the thread to be awakened.
        //Some other thread invokes the notifyAll() method for this object.
        //Some other thread interrupts T.
        //The specified amount of real time has elapsed, more or less. If it is zero, however, then real time is not taken into consideration and the thread simply waits until notified.

        //The thread T is then removed from the wait set for this object and re-enabled for thread scheduling. It then competes in the usual manner with other threads for the right to synchronize on the object.
        //Once it has gained control of the object, all its synchronization claims on the object are restored to the situation as of the time that the  wait() was invoked and T then returns from wait().

        obj.wait(100);     //IllegalMonitorStateException if the current thread is not the owner of this object's monitor.

        //wait() - same as wait(0) Causes the current thread to release ownership of this monitor(lock) and wait until another thread invokes notify() or notifyAll()
        //As in the one argument version, interrupts and spurious wakeups are possible, and this method should always be used in a loop:
        //    synchronized (obj) {
        //        while (&lt;condition does not hold&gt;)
        //            obj.wait();
        //        ... // Perform action appropriate to condition
        //    }
        obj.wait();     //IllegalMonitorStateException if the current thread is not the owner of this object's monitor.

        //wait() - same as wait(timeout), additionally provides finer control(in nanoseconds) over timeout
        obj.wait(90, 100000);     //IllegalMonitorStateException if the current thread is not the owner of this object's monitor.

        //finalize() - Called by the garbage collector on an object when garbage collection determines that there are no more references to the object.
        //It is not public because it shouldn't be invoked by anyone other than JVM. However, it must be protected so that it can be overridden by subclasses who need to define behavior for it.
        //It is not guaranteed which thread will invoke the finalize() method for any given object.
        //It is guaranteed, however, that the thread that invokes finalize will not be holding any user-visible synchronization locks when finalize is invoked.
        //If an uncaught exception is thrown by the finalize method, the exception is ignored and finalization of that object terminates.
        obj.finalize(); // protected access, only called by JVM


### Objects Utility Class
* This class consists of static utility methods for operating on objects.                 

        //equals() - Returns true if the arguments are equal to each other and false otherwise.
        boolean equals = Objects.equals(new Integer(1), new Integer(1));
        System.out.println("equals: " + equals);
        
        //deepEquals() - Returns true if the arguments are deeply equal to each other and false otherwise.
        boolean deepEquals = Objects.deepEquals(new Integer(1), new Integer(1));
        System.out.println("deepEquals: " +deepEquals);
        
        //hashCode() - Returns the hash code of a non-null argument and 0 for a null argument.
        System.out.println("hash: " + Objects.hashCode(new Integer(1)));
        
        //hash() - Generates a hash code for a sequence of input values.
        System.out.println("Hash: " + Objects.hash(new Integer(1), new Integer(4), new Integer(7)));
        
        //toString() - Returns the result of calling {@code toString} for a non-null argument and null for a null argument.
        System.out.println("ToString: " + Objects.toString(new Integer(1)));
        
        //toString() - Returns the result of calling toString() on the first argument if the first argument is not null and returns the second argument otherwise.
        System.out.println("ToString: " + Objects.toString(new Integer(1), "1"));
        
        //compare() - Returns 0 if the arguments are identical and c.compare(a, b) otherwise. If both arguments are null 0 is returned.
        Objects.compare(new Integer(1), new Integer(2), (o1, o2) -> o1.compareTo(o2));
        
        //requireNonNull() - Checks that the specified object reference is not null.
        Integer val = new Integer(1);
        Objects.requireNonNull(val);
        
        //requireNonNull() - Checks that the specified object reference is not null and throws a customized NullPointerException if it is.
        Integer valTwo = new Integer(1);
        Objects.requireNonNull(valTwo, "Val must not be null");
        
        //isNull() - Returns true if the provided reference is null otherwise returns false.
        boolean isNull = Objects.isNull(val);
        
        //nonNull() - Returns true if the provided reference is non-null otherwise returns false.
        boolean isNotNull = Objects.nonNull(val);

### Arrays Class

        Integer[] arr = new Integer[10];
        //Assigns the specified Object reference to each element of the specified array
        Arrays.fill(arr, 1);
        sout(arr, "arr");

        //Copies the specified array, truncating or padding with nulls (if necessary)so the copy has the specified length and returns the new copy array
        arr = Arrays.copyOf(arr, 20);
        sout(arr, "arr");

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) arr[i] = i;
        }
        sout(arr, "arr");

        //Copies the specified range of the specified array into a new array. The initial index of the range from must lie between zero and original.length, inclusive. 
        Integer[] arr2 = Arrays.copyOfRange(arr, 12,17);
        sout(arr2, "arr2");

        int key = 12;
        //Searches the specified array for the specified object using the binary search algorithm. The array must be sorted into ascending order.
        int result = Arrays.binarySearch(arr, key);
        System.out.println( "Key: " + key + " found at index: " + result);

        //Returns a fixed-size list backed by the specified array.  (Changes to the returned list "write through" to the array.)  
        List<Integer> list = Arrays.asList(arr);
        sout(list.toArray(new Integer[list.size()]), "list");

        List<Integer> listTwo = Arrays.stream(arr).map(x-> x*2).collect(Collectors.toList());
        sout(listTwo.toArray(new Integer[listTwo.size()]), "listTwo");

        //Sorts the specified array of objects according to the order induced by the specified comparator.
        //All elements in the array must be mutually comparable by the specified comparator (that is, c.compare(e1, e2) must not throw a ClassCastException fro elements e1, e2 in array.
        //This sort is guaranteed to be stable equal elements will not be reordered as a result of the sort.
        Arrays.sort(arr, Comparator.reverseOrder());
        sout(arr, "arr");

        //Cumulates, in parallel, each element of the given array in place, using the supplied function.
        Arrays.parallelPrefix(arr, (a,b) -> a+b);
        sout(arr, "arr");

        //Set all elements of the specified array, using the provided generator function to compute each element.
        Arrays.setAll(arr, i-> i*5);
        sout(arr, "arr");


### Collections Class

        System.out.println("------------------Collections Methods---------------------");
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8);
        System.out.println(list);

        //Reverses the order of the elements in the specified list.
        reverse(list);
        System.out.println(list);

        //Sorts the specified list into ascending order, according to the Comparable of its elements.
        sort(list);
        System.out.println(list);

        //Searches the specified list for the specified object using the binary search algorithm.  The list must be sorted into ascending order.
        int key = 2;
        int result = binarySearch(list, 2);
        System.out.println( "Key: " + key + " found at index: " + result);

        //Sorts the specified list according to the order induced by the specified comparator. All elements in the list must be mutually comparable using the specified comparator.
        sort(list, (a,b) -> b.compareTo(a));    //sort(list, Comparator.reverseOrder())) works same
        System.out.println(list);

        shuffle(list);
        System.out.println(list);

        //Swaps the elements at the specified positions in the specified list.
        swap(list, 3 ,5);
        System.out.println(list);

        //Returns the minimum element of the given collection, according to the natural ordering of its elements.
        Integer min = min(list);
        System.out.println(list + " Min: " + min);

        //Returns the maximum element of the given collection, according to the natural ordering of its elements.
        Integer max = max(list);
        System.out.println(list + " Max: " + max);

        //Rotates the elements in the specified list by the specified distance.
        rotate(list, 3);
        System.out.println(list);

        // Replaces all of the elements of the specified list with the specified element.
        fill(list, 5);
        System.out.println(list);

        //Returns an empty list (immutable).  This list is serializable.
        List<Integer> emptyList = Collections.emptyList();

        //Returns an unmodifiable view of the specified list.  This method allows modules to provide users with "read-only" access to internal lists.
        //Query operations on the returned list "read through" to the specified list, and attempts to modify the returned list, whether direct or via its iterator, result in an UnsupportedOperationException.
        List<Integer> unmodifiableList = Collections.unmodifiableList(emptyList);

        //Returns a synchronized (thread-safe) list backed by the specified list.
        List<Integer> synchronizedList = Collections.synchronizedList(emptyList);

        //Returns an empty set (immutable).  This set is serializable.
        Set<Integer> emptySet = Collections.emptySet();

        //Returns an unmodifiable view of the specified set.  This method allows modules to provide users with "read-only" access to internal sets.
        //Query operations on the returned set "read through" to the specified set, and attempts to modify the returned set, whether direct or via its iterator, result in an UnsupportedOperationException.
        Set<Integer> unmodifiableSet = Collections.unmodifiableSet(emptySet);

        //Returns a synchronized (thread-safe) set backed by the specified set. In order to guarantee serial access, it is critical that all access to the backing set is accomplished through the returned set.
        //It is imperative that the user manually synchronize on the returned set when iterating over it:
        Set<Integer> synchronizedSet = Collections.synchronizedSet(emptySet);

        //Returns an empty map (immutable).  This map is serializable.
        Map<Integer, Integer> emptyMap = Collections.emptyMap();

        //Returns an unmodifiable view of the specified map.  This method allows modules to provide users with "read-only" access to internal maps.
        //Query operations on the returned map "read through" to the specified map, and attempts to modify the returned map, whether direct or via its collection views, result in an UnsupportedOperationException
        Map<Integer, Integer> unmodifiableMap = Collections.unmodifiableMap(emptyMap);

        //Returns a synchronized (thread-safe) map backed by the specified map. In order to guarantee serial access, it is critical that all access to the backing map is accomplished through the returned map.
        //It is imperative that the user manually synchronize on the returned map when iterating over any of its collection views:
        Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(emptyMap);



        System.out.println("---------------------------------------");

### ConcurrentModificationException
* modCount holds the number of times this list has been structurally modified. This field is incremented in operations such as add(), remove() and all other methods call to which structurally modifies the list.    

        protected transient int modCount = 0;

* expectedModCount hold the modCount value, at the time iterator is instantiated and calls checkForComodification() to check it in all iterator operations.     

        int expectedModCount = modCount;
        
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
     
### Comparator and Comparable Interface
* **Comparator:** [Multi-Level Comparator ex.](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)       
A comparison function, which imposes a total ordering on some collection of objects. Compares its two arguments for order. It returns:           
negative integer - First argument is less than second          
zero - Both are equal       
positive integer - First argument greater than the second       
     
        //int compare(T o1, T o2);
        
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int age = ageGenerator.nextInt(100);
            employees.add(new Employee( i+"", age));
        }
        ageGenerator.nextInt();

        System.out.println("Before Sorting: " + employees);

        Collections.sort(employees);
        System.out.println("Sorted by name order using comparable: " + employees);

        // ALTERNATIVELY (employeeOne, employeeTwo) -> Integer.compare(employeeOne.getAge(), employeeTwo.getAge()) OR (employeeOne, employeeTwo) -> employeeOne.getAge() - employeeTwo.getAge();
        Comparator<Employee> customEmployeeComparator = Comparator.comparingInt(Employee::getAge);

        Collections.sort(employees, customEmployeeComparator);
        System.out.println("Sorted using custom comparator: " + employees);

        
* **Comparable:**   
It imposes a total ordering on the objects of each class that implements it, it is referred to as the class's natural ordering. It compares this object with the specified object for order and returns:        
negative integer - **this** is less than specified object             
zero - Both are equal           
positive integer - **this** is greater than specified object        

        //public int compareTo(T o);
        
        public class Employee implements Comparable<Employee> {
            String name;
            String department;
            String post;
            Integer age;
        
            //comparing in natural ordering of employee name
            @Override
            public int compareTo(Employee o) {
                return this.name.compareTo(o.name);
            }

     
### Collections 
* **List:**    
        
        //public interface List<E> extends Collection<E>

        //If multiple threads access an list(linked or array) instance concurrently, and at least one of the threads modifies the list structurally(adds or deletes one or more elements,
        // or explicitly resizes the backing array), it must be synchronized externally.


        //public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
        //Doubly-linked list implementation, Non-synchronized
        //The iterators returned by this class's iterator and listIterator methods are fail-fast: They use the modCount field to check for concurrent modifications
        //If the list is structurally modified at any time after the iterator is created, in any way except through the Iterator's own remove() or add(), the iterator will throw a ConcurrentModificationException.
        //
        //Fail-fast iterators throw ConcurrentModificationException on a best-effort basis.Therefore, it would be wrong to write a program that depended on this exception for its correctness.
        List<Integer> linkedList = new LinkedList<>();


        //public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
        //Constructs an empty list with an initial capacity of ten, and uses **Object[]** to sktore the elements
        // add() ensureCapacity and grows size by 50% for new array and copies over original array's contents, if necessary
        List<Integer> arrayList = new ArrayList<>();


        //public class Stack<E> extends Vector<E>
        //A last-in-first-out(LIFO) stack of objects.
        //push() and pop() are provided to add/remove items in stack
        //peek() - Looks at top item of this stack without removing it
        //empty() - checks if stack is empty
        //search() - Search the stack for an item and discover how far it is from the top(returns a 1-based answer).
        Stack<Integer> stack = new Stack<>();   // Deque should be preferred over Stack


* **Set:**    
* **Queue:**    
* **Map:**    

     
### Java-8
* **Functional Interface:** Must contain exactly one abstract method declaration(abstract method which override Object class's public method does not count).   

        //public interface Function<T, R> { R apply(T t); }
        //Represents a function that accepts one argument and produces a result.
        Function<Object, String> convertToString = (val) -> String.valueOf(val);

        //public interface Consumer<T> { void accept(T t); }
        //Represents an operation that accepts a single input argument and returns no result.
        Consumer<Object> printToConsole = (val) -> System.out.println(val);

        //public interface Supplier<T> { T get(); }
        //Represents a supplier of results. Doesn't need distinct result be returned each time.
        Supplier<Integer> supplyAnInteger = () -> 1;

        //public interface Predicate<T> { boolean test(T t); }
        //Represents a predicate (boolean-valued function) of one argument.
        Predicate<Integer> isEvenInteger = (integer) -> (integer % 2)==0;

        // public interface UnaryOperator<T> extends Function<T, T> { static <T> UnaryOperator<T> identity() { return t -> t; }}
        //Represents an operation on a single operand that produces a result of the same type as its operand.
        UnaryOperator<Integer> multiplyByTwo = (integer) -> integer*2;
        
        //public interface BiFunction<T, U, R> { R apply(T t, U u); }
        //Represents a function that accepts two arguments and produces a result.
        BiFunction<Integer, Integer, Integer> sumOfIntegers = (integerOne, integerTwo) -> integerOne + integerTwo;

        //public interface BiConsumer<T, U> { void accept(T t, U u);}
        //Represents an operation that accepts two input arguments and returns no result.   
        BiConsumer<Integer, Integer> printSumToConsole = (integerOne, integerTwo) -> System.out.println(sumOfIntegers.apply(integerOne, integerTwo));

        //public interface BiPredicate<T, U> { boolean test(T t, U u); }
        //Represents a predicate (boolean-valued function) of two arguments.
        BiPredicate<Integer, Integer> areBothEven = (integerOne, integerTwo) -> isEvenInteger.test(integerOne) && isEvenInteger.test(integerTwo);

        //public interface IntBinaryOperator { int applyAsInt(int left, int right);}
        //Represents an operation upon two int valued operands and producing an int result.
        IntBinaryOperator mutliply = (integerOne, integerTwo) -> integerOne * integerTwo;

        //public interface IntConsumer { void accept(int value); }
        //Represents an operation that accepts a single int and returns no result
        IntConsumer printIntToConsole = (integerOne) -> printToConsole.accept(integerOne);

        //public interface IntFunction<R> { R apply(int value);}
        //Represents a function that accepts an int-valued argument and produces a result;
        IntFunction<Double> toDouble = (val) -> Double.valueOf(String.valueOf(val));


        //public interface IntPredicate { boolean test(int value); }
        //Represents a predicate (boolean-valued function) of one int valued  argument.
        IntPredicate isOddNumber = (val) -> val%2 != 1;

        //public interface IntSupplier { int getAsInt(); }
        //Represents a supplier of int valued results.
        IntSupplier returnZero = () -> 0;

        //public interface IntToDoubleFunction { double applyAsDouble(int value);}
        //Represents a function that accepts an int-valued argument and produces a double-valued result.
        IntToDoubleFunction intToDoubleFunction = value -> toDouble.apply(value);

        //public interface IntToLongFunction { long applyAsLong(int value);}
        //Represents a function that accepts an int-valued argument and produces a long-valued result.
        IntToLongFunction intToLongFunction = value -> Long.valueOf(convertToString.apply(value));

        //public interface IntUnaryOperator { int applyAsInt(int operand); }
        //Represents an operation on a single int valued operand that produces an int valued result.
        IntUnaryOperator addTen = value -> value + 10;
  
         
### Java Basics 

* **Variables:**    
    **Instance Variables (Non-Static Fields)** - Their values are unique to each instance(object) of a class.    
    **Class Variables (Static Fields)**  - Any field declared with the static modifier; this tells the compiler that there is exactly one copy of this variable in existence, regardless of how many times the class has been instantiated.  
    **Local Variables** - A method will often store its temporary state in local variables. and they are only visible to the methods in which they are declared.    
    **Parameters** - The args variable is the parameter to main(). The important thing to remember is that parameters are always classified as "variables" not "fields". refers to the list of variables in a method declaration. Arguments are the actual values that are passed in when the method is invoked.       

* **Variables Naming:**
1. Names can only begin with letter, dollar-sign and underscore. Subsequently characters can also include digits. Name cannot be a keyword or reserved-word.
2. Use camel case for variable names. Constants can be named in all letters capital.    

* **Members:**      
    A type's fields, methods, and nested types are collectively called its members.      

* **Primitive Data Types:**  
byte(8 bit) - 0    
char(16 bit) - '\u0000'      
short(16 bit) - 0   
int(32 bit) - 0     
long(64 bit) - 0L    
float(32 bit) - 0.0f      
double(64 bit) - 0.0d  
boolean - false 

* **Order of expression evaluation:**   
1. If you don't explicitly indicate the order for the operations to be performed, the order is determined by the precedence assigned to the operators in use within the expression. Operators that have a higher precedence get evaluated first.    
2. The subexpressions are evaluated left to right.  

* **Branching statements:**   
1. An unlabeled break statement terminates the innermost switch, for, while, or do-while statement, but a labeled break terminates an outer statement.      
2. The continue statement skips the current iteration of a for, while , or do-while loop. [A labeled continue statement skips the current iteration of an outer loop marked with the given label.](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/branch.html)   

* **Class Declaration:**    
1. Modifiers such as public, private. (The private modifier can only be applied to Nested Classes.)          
2. The class name begins with capital letter. And the class's parent (superclass), if any, preceded by the keyword extends. A class can only extend one parent.           
3. A comma-separated list of interfaces implemented by the class, if any, preceded by the keyword implements. A class can implement more than one interface.      
 
        class MyClass extends MySuperClass implements YourInterface {
            // field, constructor, and
            // method declarations
        }
       
* **Controlling Access to Members of a Class:**
**public** — the class(top level) and all its members are accessible from all classes.  
**protected** — the class(top level) and all its members are accessible within its own package and, in addition, by a subclass of its class in another package.  
**package-private(no explicait keyword)** — the class(top level) and all its members are visible only within its own package.  
**private** — the class(top level) and all its members are accessible only within its own class.    

* **Nested Class:**     
A nested class is a member of its enclosing class. It can be declared private, public, protected, or package private. Nested classes are divided into two categories: non-static and static.               
1. Non-static nested classes are called **inner classes**. They have access to other members(methods and fields) of the enclosing class, even if they are declared private. 
As an inner class is associated with an instance, it cannot define any static members itself. There are two special kinds of inner classes: **local classes and anonymous classes**.         
An instance of InnerClass can exist only within an instance of OuterClass and has direct access to the methods and fields of its enclosing instance. [Inner Class Example](https://docs.oracle.com/javase/tutorial/java/javaOO/innerclasses.html)       
       
        class OuterClass {
            ...
            class InnerClass {
                ...
            }
        }
        OuterClass outerObject = new OuterClass();
        OuterClass.InnerClass innerObject = outerObject.new InnerClass();    
        
* **Local Class**  
**Effectively final:** A variable or parameter whose value is never changed after it is initialized.     
Local classes are classes that are defined in a block or method. 
1. A local class has access to the members of its enclosing class. the PhoneNumber constructor accesses the member LocalClassExample.regularExpression.    
2. A local class can only access local variables that are declared final(or effectively final from java 8). When a local class accesses a local variable or parameter of the enclosing block, it **captures** that variable or parameter.        
For example, the PhoneNumber constructor can access the local variable numberLength because it is declared final; numberLength is a **captured variable**.    
3. Local classes are non-static because they have access to instance members of the enclosing block, but cannot define/declare any static members(except static final constants).            
4. Local classes in static methods can only refer to static members of the enclosing class.             
5. You cannot declare an interface inside a block, interfaces are inherently static. Also cannot declare static initializers or member interfaces in a local class.     
        

    public class LocalClassExample {
        static String regularExpression = "[^0-9]";
    
        public static void validatePhoneNumber(String phoneNumber1, String phoneNumber2) {
            final int numberLength = 10;    // Valid in JDK 8 and later: int numberLength = 10;
    
            class PhoneNumber {
                String formattedPhoneNumber = null;
                static final int CONST = 1;
                
                PhoneNumber(String phoneNumber){
                    String currentNumber = phoneNumber.replaceAll(regularExpression, "");
                    if (currentNumber.length() == numberLength) formattedPhoneNumber = currentNumber;
                    else formattedPhoneNumber = null;
                }
    
                public String getNumber() { return formattedPhoneNumber; }
    
                //As local class PhoneNumber is declared in a method, it can access the method's parameters phoneNumber1 and phoneNumber2
                public void printOriginalNumbers() { System.out.println("Original numbers are " + phoneNumber1 + " and " + phoneNumber2); }
            }
    
            PhoneNumber myNumber1 = new PhoneNumber(phoneNumber1);
            PhoneNumber myNumber2 = new PhoneNumber(phoneNumber2);
            myNumber1.printOriginalNumbers();
    
            if (myNumber1.getNumber() == null) System.out.println("First number is invalid");
            else System.out.println("First number is " + myNumber1.getNumber());
    
            if (myNumber2.getNumber() == null) System.out.println("Second number is invalid");
            else System.out.println("Second number is " + myNumber2.getNumber());
        }
          
* **Anonymous Classes**  
1. An anonymous class is an expression, like the invocation of a constructor, alongwith a class definition contained in a block of code.    
2. They are like local classes except that they do not have a name and enable you to declare and instantiate a class at the same time.      
3. Anonymous classes can capture variables; they have the same access to local variables of the enclosing scope:        
4. An anonymous class has access to the members of its enclosing class.        
5. An anonymous class cannot access local variables in its enclosing scope that are not declared as final or effectively final.        
6. A declaration of a type/variable in an anonymous class shadows any other declarations in the enclosing scope with same name.        
7. Anonymous classes also have the same restrictions as local classes with respect to their members:
8. You cannot declare static initializers/member interfaces/constructors in an anonymous class. But anonymous class can declare static final constant variables, Fields, Extra methods (even if they do not implement any methods of the supertype), Instance initializers, Local classes.      


    public class HelloWorldAnonymousClasses {
        interface HelloWorld {
            void greet();
            void greetSomeone(String someone);
        }
    
        public void sayHello() {
            // local class
            class EnglishGreeting implements HelloWorld {
                String name = "world";
                public void greet() { greetSomeone("world"); }
                public void greetSomeone(String someone) { name = someone;System.out.println("Hello " + name); }
            }
            HelloWorld englishGreeting = new EnglishGreeting();
            //anonymous class
            HelloWorld frenchGreeting = new HelloWorld() {
                String name = "tout le monde";
                public void greet() { greetSomeone("tout le monde"); }
                public void greetSomeone(String someone) { name = someone;System.out.println("Salut " + name); }
            };
    
            //anonymous class
            HelloWorld spanishGreeting = new HelloWorld() {
                String name = "mundo";
                public void greet() { greetSomeone("mundo"); }
                public void greetSomeone(String someone) { name = someone;System.out.println("Hola, " + name); }
            };
    
            englishGreeting.greet();
            frenchGreeting.greetSomeone("Fred");
            spanishGreeting.greet();
   

2. Nested classes that are declared static are called **static nested classes**. They do not have access to other members of the enclosing class.      . 
A static nested class is associated with its outer class, and cannot refer directly to instance variables or methods defined in its enclosing class: it can use them only through an object reference.      
A static nested class interacts with the instance members of its outer class (and other classes) just like any other top-level class. In effect, a static nested class is behaviorally a top-level class that has been nested in another top-level class for packaging convenience.         

        class OuterClass {
            ...
            static class StaticNestedClass {
                ...
            }
        }
        StaticNestedClass staticNestedObject = new StaticNestedClass();


* **Lambda expressions:**   
1. Lambda expressions enable you to encapsulate a single unit of behavior that you want to pass as a method argument to other code(code as data).                 
2. You can consider lambda expressions as anonymous methods—methods without a name.       
3. **Target Typing** To determine the type of a lambda expression, the Java compiler uses the target type of the context or situation in which the lambda expression was found. The data type that these methods expect is called the **target type**.         


        void invoke(Runnable r) {       // compiler expects Runnable target type
            r.run();
        }
        
        <T> T invoke(Callable<T> c) {       // compiler expects Callable target type
            return c.call();
        }
 
 
* **Method reference:**     
There are four kinds of method references:      
1. Reference to a static method(ContainingClass::staticMethodName). Ex.	Person::compareByAge, MethodReferencesExamples::appendStrings       
2. Reference to an instance method of a particular object(containingObject::instanceMethodName). Ex. myComparisonProvider::compareByName, myApp::appendStrings2     
3. Reference to an instance method of an arbitrary object of a particular type(ContainingType::methodName). Ex. String::compareToIgnoreCase, String::concat     
4. Reference to a constructor(ClassName::new). Ex. HashSet::new     


* **Enum Types**        
A special data type that enables for a variable to be a set of predefined constants. All enums implicitly extend **java.lang.Enum** and thus can't extend any other class.       
Java requires that the constants be defined first, prior to fields or methods, if any. Also, when there are fields and methods, the list of enum constants must end with a semicolon.   
The compiler automatically adds some special methods(values()) when it creates an enum.         
The constructor for an enum type must be package-private or private access. It automatically creates the constants that are defined at the beginning of the enum body. You cannot invoke an enum constructor yourself.      


        public enum Day {
            SUNDAY("Weekend", 0),
            MONDAY("Weekday", 1),
            TUESDAY("Weekday", 2),
            WEDNESDAY("Weekday", 3),
            THURSDAY("Weekday", 4),
            FRIDAY("Weekend", 5),
            SATURDAY("Weekend", 6);     
        
            Integer dayOfWeek;
            String alias;
        
            Day(String alias, Integer dayOfWeek) {
                this.alias = alias;
                this.dayOfWeek = dayOfWeek;
            }
        
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("Day{");
                sb.append("dayOfWeek=").append(dayOfWeek);
                sb.append(", alias='").append(alias).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }

* **Annotations**       
1. They are a form of metadata, provide data about a program that is not part of the program itself.   
2. The **annotation type** definition looks similar to an interface definition where the keyword interface is preceded by the at sign (@). They are a form of interface.    
3. The **annotation type element** declarations look a lot like methods. Note that they can define optional default values.    
        
        
        @interface ClassPreamble {      //annotation type
           String author();             //element
           String date();
           int currentRevision() default 1;         //element with default value
           String lastModified() default "N/A";
           String lastModifiedBy() default "N/A";
           // Note use of array
           String[] reviewers();
        }

4. The predefined annotation types defined in java.lang are @Deprecated, @Override, and @SuppressWarnings. [Detailed Explaination](https://docs.oracle.com/javase/tutorial/java/annotations/predefined.html)    
5. Type Annotations: Annotations that can be used anywhere you use a type(like  class instance creation expressions (new), casts, implements clauses, and throws clauses).    
    - Instance creation expression:       new @Interned MyObject();
    - Type cast:                          myString = (@NonNull String) str;
    - implements clause:                  class UnmodifiableList<T> implements @Readonly List<@Readonly T> { ... }
    - Thrown exception declaration:       void monitorTemperature() throws @Critical TemperatureException { ... }    
    
* **Package**   
1. A package is a grouping of related types providing access protection and name space management. Note that types refers to classes, interfaces, enumerations, and annotation types.      
2. The names of your types won't conflict with the type names in other packages because the package creates a new namespace.   
3. The package statement(ex. package java.graphics;) must be the first line in the source file. There can be only one package statement in each source file. If you do not use a package statement, your type ends up in an unnamed package.       
4. To use a public package member from outside its package, you must either 


        refer member by its fully qualified name -  graphics.Rectangle myRect = new graphics.Rectangle();   
        import package member       - import graphics.Rectangle;       
        import member's entire package.     - import graphics.*;           
5. Java compiler automatically imports two entire packages for each source file: (1) the java.lang package and (2) the current package (the package for the current file).      
6. If a member in one package shares its name with a member in another package and both packages are imported, you must refer to each member by its qualified name.          
7. **Static import statement** allows to import the constants and static methods that you want to use so that you do not need to prefix the name of their class.        


            import static java.lang.Math.*;
            
            double r = Math.cos(Math.PI * theta); // without static import
            double r = cos(PI * theta); //with static import

8. The full path to the classes directory is called the **class path**, and is set with the CLASSPATH system variable. Both the compiler and the JVM construct the path to your .class files by adding the package name to the class path. For example, if
   IF class path -- **<path_two>\classes**, package name -- **com.example.graphics** THEN the compiler and JVM look for .class files in **<path_two>\classes\com\example\graphics**     
   
   
* **Wrapper Classes**   
1. Wrapper classes for each of the primitive data types "wrap" the primitive in an object. Often, the wrapping is done by the compiler:        
2. If you use a primitive where an object is expected, the compiler boxes the primitive in its wrapper class for you.      
3. Similarly, if you use a number object when a primitive is expected, the compiler unboxes the object for you.       

**Autoboxing:** is the automatic conversion that the Java compiler makes between the primitive types and their corresponding object wrapper classes. Java compiler applies autoboxing when a primitive value is:    
1. Passed as a parameter to a method that expects an object of the corresponding wrapper class.
2. Assigned to a variable of the corresponding wrapper class.


        /*Autoboxing*/
        Boolean autoboxedBoolean = unboxedBoolean;    //Boolean.valueOf(unboxedBoolean);
        Byte autoboxedByte = unboxedByte; //Byte.valueOf(unboxedByte);
        Character autoboxedCharacter = unboxedCharacter;   //Character.valueOf(unboxedCharacter);
        Float autoboxedFloat = unboxedFloat;    //Float.valueOf(unboxedFloat);
        Integer autoboxedInteger = unboxedInteger;   //Integer.valueOf(unboxedInteger);
        Long autoboxedLong = unboxedLong;    //Long.valueOf(unboxedLong);
        Short autoboxedShort = unboxedShort;   //Short.valueOf(unboxedShort);
        Double autoboxedDouble = unboxedDouble;  //Double.valueOf(unboxedDouble);

**Unboxing:** is automatic conversion of an object of a wrapper type (Integer) to its corresponding primitive (int) value is called unboxing. The Java compiler applies unboxing when an object of a wrapper class is:      
1. Passed as a parameter to a method that expects a value of the corresponding primitive type.      
2. Assigned to a variable of the corresponding primitive type.      


        /* Unboxing*/
        boolean unboxedBoolean = booleanWrapper; //compiler calls booleanWrapper.booleanValue();
        byte unboxedByte = byteWrapper;         //compiler calls byteWrapper.byteValue();
        char unboxedCharacter = characterWrapper;      //compiler calls characterWrapper.charValue();
        float unboxedFloat = floatWrapper;        //compiler calls floatWrapper.floatValue();
        int unboxedInteger = integerWrapper; //compiler calls integerWrapper.intValue();
        long unboxedLong = longWrapper; //compiler calls longWrapper.longValue();
        short unboxedShort = shortWrapper;         //compiler calls shortWrapper.shortValue();
        double unboxedDouble = doubleWrapper;     //compiler calls doubleWrapper.doubleValue();


* **Character class(Immutable)**        
1. Important methods:       

        boolean isLetter(char ch),boolean isDigit(char ch)	        Determines whether the specified char value is a letter or a digit, respectively.
        boolean isWhitespace(char ch)	                            Determines whether the specified char value is white space.
        boolean isUpperCase(char ch), boolean isLowerCase(char ch)	Determines whether the specified char value is uppercase or lowercase, respectively.
        char toUpperCase(char ch), char toLowerCase(char ch)	    Returns the uppercase or lowercase form of the specified char value.
        toString(char ch)	                                        Returns a String object representing the specified character value — that is, a one-character string.
        
2. A character preceded by a backslash (\) is an **escape sequence** and has special meaning to the compiler.       

        \t	Insert a tab in the text at this point.
        \b	Insert a backspace in the text at this point.
        \n	Insert a newline in the text at this point.
        \r	Insert a carriage return in the text at this point.
        \f	Insert a form feed in the text at this point.
        \'	Insert a single quote character in the text at this point.
        \"	Insert a double quote character in the text at this point.
        \\	Insert a backslash character in the text at this point.

* **Formatting Numeric Print Output:**                      
1. Format specifiers begin with a percent sign (%) and end with a converter(f). In between we can have optional flags and specifiers.        
2. There are many converters, flags, and specifiers, which are documented in java.util.Formatter. [Commonly used converters](https://docs.oracle.com/javase/tutorial/java/data/numberformat.html)         
 
        long n = 461012;
        System.out.format("%d%n", n);      //  -->  "461012"
        System.out.format("%08d%n", n);    //  -->  "00461012"
        System.out.format("%+8d%n", n);    //  -->  " +461012"
        System.out.format("%,8d%n", n);    // -->  " 461,012"
        System.out.format("%+,8d%n%n", n); //  -->  "+461,012"

        double pi = Math.PI;
        System.out.format("%f%n", pi);       // -->  "3.141593"
        System.out.format("%.3f%n", pi);     // -->  "3.142"
        System.out.format("%10.3f%n", pi);   // -->  "     3.142"
        System.out.format("%-10.3f%n", pi);  // -->  "3.142"
        System.out.format(Locale.FRANCE, "%-10.4f%n%n", pi); // -->  "3,1416"

        Calendar c = Calendar.getInstance();
        System.out.format("%tB %te, %tY%n", c, c, c); // -->  "May 29, 2006"
        System.out.format("%tl:%tM %tp%n", c, c, c);  // -->  "2:34 am"
        System.out.format("%tD%n", c);    // -->  "05/29/06"
        

3. **The DecimalFormat Class**      

        customFormat("###,###.###", 123456.789);    //123456.789  ###,###.###  123,456.789
        customFormat("###.##", 123456.789); //123456.789  ###.##  123456.79
        customFormat("000000.000", 123.78); //123.78  000000.000  000123.780
        customFormat("$###,###.###", 12345.67); //12345.67  $###,###.###  $12,345.67

* **String class(Immutable):**               
[String Manipulation Methods](https://docs.oracle.com/javase/tutorial/java/data/manipstrings.html)            
**StringBuilder** objects are like String objects, except that they can be modified. Internally, these objects are treated like variable-length arrays that contain a sequence of characters. At any point, the length and content of the sequence can be changed through method invocations.              
**StringBuffer** class is exactly the same as the StringBuilder class, except that it is thread-safe by virtue of having its methods synchronized.              
 
* **Shadowing:**    
If a declaration of a type(member variable or a parameter name) in a particular scope(inner class or a method definition) 
has the same name as another declaration in the enclosing scope, then the declaration shadows the declaration of the enclosing scope.      
        
        
        public class ShadowTest {
        
            public int x = 0;
        
            class FirstLevel {
        
                public int x = 1;
        
                void methodInFirstLevel(int x) {
                    System.out.println("x = " + x);
                    System.out.println("this.x = " + this.x);
                    System.out.println("ShadowTest.this.x = " + ShadowTest.this.x);
                }
            }
        
            public static void main(String... args) {
                ShadowTest st = new ShadowTest();
                ShadowTest.FirstLevel fl = st.new FirstLevel();
                fl.methodInFirstLevel(23);
            }
        }
        The following is the output of this example:
        
        x = 23
        this.x = 1
        ShadowTest.this.x = 0
        
* **Methods:**     
1. The method signature consist of the method's name and the parameter types.   

        public double calculateAnswer(double wingSpan, int numberOfEngines, double length, double grossTons) throws Exception {
            //do the calculation here
        }        
        
        calculateAnswer(double, int, double, double)    // method's signature

2. **Overloaded methods** are methods within a class having same name but different parameter lists. They are differentiated by the number and the type of the arguments passed into the method(method's signature).    

3. **Constructors** are called to create objects of a class. They use the name of the class and have no return type.            
    All classes have at least one constructor. If a class does not explicitly declare any, the Java compiler automatically provides a no-argument constructor, called the **default constructor**.       
             

        public void main(String... args) {      // varargs to pass an arbitrary number(zero or more) of String values to a method.
        
4. A parameter can have the same name as one of the class's fields. If this is the case, the parameter is said to shadow the field.     


        public class Circle {
            private int x, y, radius;
            public void setOrigin(int x, int y) {       //x or y within the body of the method refers to the parameter, not to the field. To access the field, you must use this.x, this.y.     
                ...
            }
        }        
5. **Covariant return type**: Means that the return type of a method is allowed to vary in the same direction as the subclass. It could either return a subclass(if Return Type is a class) or an implementation(if return type is an interface).             

6. Within an instance method or a constructor, this is a reference to the current object — the object whose method or constructor is being called.      
   From within a constructor, you can also use the this keyword to call another constructor in the same class. Doing so is called an **explicit constructor invocation**.      


    public class Test {
        int a, b, c;
        public Test() {
            this(0, 0, 0);                  // calls the below constructor using this
        }
        
        public Test(int a, int b, int c)  { // referencing instance variablesto overcome fields shadowing due to same parameters list
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
    }
    
7. Instance methods can access instance an class variables and instance and class methods directly.
   Class methods can access class variables and class methods directly.
   Class methods cannot access instance variables or instance methods directly—they must use an object reference. Also, class methods cannot use the this keyword as there is no instance for this to refer to.     

8. The static modifier, in combination with the final modifier, is also used to define constants. The **final** modifier indicates that the value of this field cannot change.      
   
        private static final int CONST_PI = 3.14;  
   
9. A **static initialization block** is a normal block of code enclosed in braces, { }, and preceded by the static keyword, used for initializing class variables. 
A class can have multiple such blocks and the runtime system guarantees that static initialization blocks are called in the order that they appear in the source code.        
     
     
         static {
            // initialize static variables
         }

The **private static methods** are also an alternative to static blocks as they can be reused later if you need to reinitialize the class variable.     

        private static varType initializeClassVariable() {
            // initialization code goes here
        }

10. There are two alternatives to using a constructor to initialize instance variables: **initializer blocks and final methods**.       
The Java compiler copies initializer blocks into every constructor. Therefore, this approach can be used to share a block of code between multiple constructors.                    


        {
            // whatever code is needed for initialization goes here
        }
        
A final method cannot be overridden in a subclass. This is especially useful if subclasses might want to reuse the initialization method. The method is final because calling non-final methods during instance initialization can cause problems.      
    
        class Whatever {
            private varType myVar = initializeInstanceVariable();
                
            protected final varType initializeInstanceVariable() {
        
                // initialization code goes here
            }
        }
    

### Interfaces  
An interface is a reference type, similar to a class, that can contain only constants, method signatures, default methods, static methods, and nested types(all are implicitly public).             
Method bodies exist only for default methods and static methods. Interfaces cannot be instantiated—they can only be implemented by classes or extended by other interfaces.         

        public interface GroupedInterface extends Interface1, Interface2, Interface3 {
        
            // constant declarations
            public static final int CONST = 1;
            
            // base of natural logarithms
            double E = 2.718282;
         
            // method signatures
            void doSomething (int i, double x);         //abstract method
            int doSomethingElse(String s);
            default boolean checkEven(int i) { return i%2 == 0;}             //default method
        }    

**Default methods** enable you to add new functionality to the interfaces of your libraries and ensure binary compatibility with code written for older versions of those interfaces.   
When you extend an interface that contains a default method, you can do the following:      
    1. Not mention the default method at all, which lets your extended interface inherit the default method.        
    2. Redeclare the default method, which makes it abstract.       
    3. Redefine the default method, which overrides it.         


            default boolean checkEven(int i) { return i%2 == 0;}             //default method
    
**Static method** is a method that is associated with the class in which it is defined rather than with any object. Every instance of the class shares its static methods.      
This makes it easier for you to organize helper methods in your libraries; you can keep static methods specific to an interface in the same interface rather than in a separate class.      


            static boolean multiplyBy2(int i) { return i*2;}             //static method


### Inheritance     
1. A class that is derived from another class is called a subclass/derived/child class. The class from which the subclass is derived is called a superclass/base/parent class.      
2. Excepting Object, which has no superclass, every class has one and only one direct superclass. In no explicit superclass, every class is implicitly a subclass of Object class.        
3. A subclass inherits all the members (fields, methods, and nested classes) from its superclass. 
4. Constructors are not members, so they are not inherited by subclasses, but the constructor of the superclass can be invoked from the subclass.           
5. A subclass inherits all public and protected members of its parent(but not private members), no matter what package the subclass is in. If the subclass is in the same package as its parent, it also inherits the package-private members of the parent.      
    The inherited fields and methods can be used directly.      
    You can declare a field in the subclass with the same name as the one in the superclass, thus hiding it (not recommended).      
    You can declare new fields and methods in the subclass that are not in the superclass.      
    You can write a new instance method in the subclass that has the same signature as the one in the superclass, thus overriding it.       
    You can write a new static method in the subclass that has the same signature as the one in the superclass, thus hiding it.     
    You can write a subclass constructor that invokes the constructor of the superclass, either implicitly or by using the keyword super.       
6. **Casting** shows the use of an object of one type in place of another type, among the objects permitted by inheritance and implementations. For example:        
   **Implicit Casting:**     Object obj = new MountainBike(); //obj is both an Object and a MountainBike. 
   **Explicit Casting:**    Tells the compiler that we promise to assign a MountainBike to obj:    MountainBike myBike = (MountainBike)obj;        
   **instanceof operator** verifies that obj refers to a MountainBike so that we can make the cast with knowledge that there will be no runtime exception thrown.       
            
        
            if (obj instanceof MountainBike) {
                MountainBike myBike = (MountainBike)obj;
            }
7. **Method Overriding:** An method in a subclass with the same signature(name, plus the number and the type of its parameters) and return type(covariant return type) as an superclass method.            
   **Method Hiding** If a subclass defines a static method with the same signature as a static method in the superclass, then the method in the subclass hides the one in the superclass.  
   The distinction between hiding a static method and overriding an instance method has important implications:
        The version of the overridden instance method that gets invoked is the one in the subclass.      
        The version of the hidden static method that gets invoked depends on whether it is invoked from the superclass or the subclass.     
8. **Interface Methods**                      
    When the supertypes of a class or interface provide multiple default methods with the same signature, the Java compiler follows inheritance rules to resolve the name conflict: 
    1. Instance methods are preferred over interface default methods.
    
            public class Horse {
                public String identifyMyself() { return "I am a horse."; }
            }
            public interface Flyer {
                default public String identifyMyself() { return "I am able to fly."; }
            }
            public interface Mythical {
                default public String identifyMyself() { return "I am a mythical creature."; }
            }
            public class Pegasus extends Horse implements Flyer, Mythical {
                public static void main(String... args) {
                    Pegasus myApp = new Pegasus();
                    System.out.println(myApp.identifyMyself());
                }
            }
            The method Pegasus.identifyMyself returns the string I am a horse.
           
    2. Methods that are already overridden by other candidates are ignored. This circumstance can arise when supertypes share a common ancestor.        
   
            public interface Animal {
                default public String identifyMyself() { return "I am an animal."; }
            }
            public interface EggLayer extends Animal {
                default public String identifyMyself() { return "I am able to lay eggs."; }
            }
            public interface FireBreather extends Animal { }
            public class Dragon implements EggLayer, FireBreather {
                public static void main (String... args) {
                    Dragon myApp = new Dragon();
                    System.out.println(myApp.identifyMyself());
                }
            }
            The method Dragon.identifyMyself returns the string I am able to lay eggs.  

9. If two or more independently defined default methods conflict, or a default method conflicts with an abstract method, then the Java compiler produces a compiler error. 
You must explicitly override the supertype methods.     


            public interface OperateCar { default public int startEngine(EncryptedKey key) { } }
            public interface FlyCar { default public int startEngine(EncryptedKey key) { } }
            public class FlyingCar implements OperateCar, FlyCar {
                public int startEngine(EncryptedKey key) {
                    FlyCar.super.startEngine(key);      //**super** for conflict resolution
                    OperateCar.super.startEngine(key);  //**super** for conflict resolution
                }
            }
            
10. Static methods in interfaces are never inherited.   
11. You will get a compile-time error if you attempt to change an instance method in the superclass to a static method in the subclass, and vice versa.          
12. **Rules of Method Overriding**            
- The argument list must exactly match that of the overridden method.               
- The return type must be the same as, or a subtype of, the return type declared in the original overridden method in the superclass.       
- The access level can’t be more restrictive(CAN be less restrictive though) than that of the overridden method.        
- Instance methods can be overridden only if they are inherited by the subtype.     
- The overriding method CAN throw any unchecked (runtime) exception, regardless of whether the overridden method declares the exception.        
- The overriding method must NOT throw checked exceptions that are new or broader than those declared by the overridden method.         
- The overriding method can throw narrower or fewer exceptions(even no exceptions).      
- You cannot override constructors and methods marked final/static/private.     
- If a method can’t be inherited, you cannot override it.       
- Use the super keyword to invoke the overridden method from a subclass.            
- The strictfp/synchronized modifier has no effect on the rules of overriding.              

13. **Hiding Fields** 
Within a class, a field that has the same name as a field in the superclass hides the superclass's field, even if their types are different. Within the subclass, the field in the superclass cannot be referenced by its simple name. Instead, the field must be accessed through **super**.

14. **super keyword**   
To invoke the overridden superclass method/superclass's constructor/hidden fields.


        public class Superclass { public void printMethod() { System.out.println("Printed in Superclass."); } }
        
        public class Subclass extends Superclass {
            public void printMethod() { super.printMethod();        //calls overridden superclass method
            System.out.println("Printed in Subclass"); } }      
        
        public MountainBike(int startHeight, int startCadence, int startSpeed, int startGear) { 
            super(startCadence, startSpeed, startGear);             //calls superclass's constructor
            seatHeight = startHeight;
        }        

15. **final keyword**   
Final methods cannot be overridden by subclasses. 
Final class can not be subclassed.   

16. **abstract keyword**    
1. Abstract class may or may not include abstract methods. It cannot be instantiated, but can be subclassed. If subclassed, the subclass usually provides implementations for all superclass abstract methods, else subclass must be declared abstract.       
2. An abstract method is declared without an implementation. Interface methods that are not default or static are implicitly abstract.        


        public abstract class GraphicObject {
           // declare fields
           // declare nonabstract methods
           abstract void draw();
        }

3. Abstract classes can have non-static, non-final fields, and public, protected and private concrete methods. With interfaces, all fields are automatically public, static, final, and all methods that you declare or define (as default methods) are public. 
   We can extend only one class, whether or not it is abstract, whereas you can implement any number of interfaces.

### Exception Handling  
        //public class Throwable implements Serializable {
        //public class Error extends Throwable {
        //public class Exception extends Throwable {
        //public class RuntimeException extends Exception {

1. An **exception** is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions.   
2. **Catch or Specify Requirement:**  A code that might throw certain exceptions must be enclosed by either: **try statement** that catches the exception OR a method that specifies that it can throw the exception(**throws clause**).        
3. **The Three Kinds of Exceptions:**          
        1. **Checked exception:** - These are exceptional conditions that a well-written application should anticipate and recover from. They are subject to the Catch or Specify Requirement. All exceptions are checked exceptions, except for those indicated by Error, RuntimeException, and their subclasses.   
        java.io.FileNotFoundException     
        2. **Error:** - Errors are those exceptions indicated by Error and its subclasses. These are external to application, and application usually cannot anticipate or recover from.
        java.io.IOError   
        3. **Runtime Exception** - Runtime exceptions are those indicated by RuntimeException and its subclasses. These are internal to application, and application usually cannot anticipate or recover from.         
        Errors and runtime exceptions are collectively known as **unchecked exceptions**. They are not subject to the Catch or Specify Requirement.
        NullPointerException    
4. **The three exception handler components — the try, catch, and finally block**   
        
        try {
            throw new IOException();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
In Java SE 7 and later, a single catch block can handle more than one type of exception, also the catch parameter is implicitly final(the catch parameter ex is final).         

        catch (IOException|SQLException ex) {
            throw ex;
        }
The finally block always executes when the try block exits. Finally also allows the programmer to avoid having cleanup code accidentally bypassed by a return, continue, or break.  
If the JVM exits while the try or catch code is being executed OR if the thread executing it is interrupted or killed, the finally block may not execute.   

        finally {
            if (out != null) { 
                System.out.println("Closing PrintWriter");
                out.close(); 
            } else { 
                System.out.println("PrintWriter not open");
            } 
        }  

5. **The try-with-resources Statement**             
It is a try statement that declares one or more resources and ensures that each resource is closed at the end of the statement. 
A resource is an object that must be closed after the program is finished with it(Should implements java.lang.AutoCloseable, which includes all objects which implement java.io.Closeable). 
In a try-with-resources statement, any catch or finally block is run after the resources declared have been closed. 
If exception is thrown from try block and one or more exceptions are thrown from the try-with-resources statement, then those exceptions thrown from the try-with-resources statement are **suppressed**, and the exception thrown by the block is the one that is thrown ahead. The suppressed exceptions can be fetched by calling the **Throwable.getSuppressed** method from the exception thrown by the try block.     


        try (   java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFileName);
                java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFilePath, charset)) {
        }
The try-with-resources statement contains two declarations that are separated by a semicolon: ZipFile and BufferedWriter. When the block of code that directly follows it terminates, either normally or because of an exception, the close methods of the BufferedWriter and ZipFile objects are automatically called in this order. Note that the close methods of resources are called in the opposite order of their creation.
 
6. The **throws clause** is used to specify that it can throw these exceptions and comprises the throws keyword followed by a comma-separated list of all the exceptions thrown by that method.     
        
        
        public void writeList() throws IOException, IndexOutOfBoundsException {               

7. The throw clause is used to throw an exception: a throwable object. **Throwable objects** are instances of any subclass of the Throwable class.      
        
        
        throw new EmptyStackException();          
        
8. **Chained Exceptions** makes an application respond to an exception by throwing another exception. In effect, the first exception causes the second exception.         


        try {
        
        } catch (IOException e) {
            throw new SampleException("Other IOException", e);
        }


### Polymorphism     
* Subclasses of a class can define their own unique behaviors and yet share some of the same functionality of the parent class.         
* Polymorphic method invocations apply only to instance methods. Not static methods. Not variables. Only overridden instance methods are dynamically invoked based on the real object’s type.   
* You can always refer to an object with a more general reference variable type (a superclass or interface), but at runtime, the ONLY things that are dynamically selected based on the actual object (rather than the reference type) are instance methods.     

        
### Generics                 
1. Generics enable types (classes and interfaces) to be parameters when defining classes, interfaces and methods.  
2. Stronger type checks at compile time, elimination of casts(ex. List<String>), and allows to implement generic algorithms.   
3. The type parameter section, delimited by angle brackets (<>), follows the class name. It specifies the type parameters(or type variables) T1, T2, ..., and Tn. A type variable can be any non-primitive type(class/interface/array/another type).  
4. Syntax:    class name<T1, T2, ..., Tn> { /* ... */ }        // type variables are T1, T2, ..., and Tn.      


        public class Box<T> {
            // T stands for "Type"
            private T t;
        
            public void set(T t) { this.t = t; }
            public T get() { return t; }
        }    
5. An invocation of a generic type is generally known as a **parameterized type**. For ex. Box<Integer> below     
        
        
        Box<Integer> integerBox = new Box<Integer>();   //pair of angle brackets on RHS, <>, is called the diamond. 

6. A **raw type** is the name of a generic class or interface without any type arguments. For ex. Box is raw type of generic type Box<T>        
   Assigning a raw type to a parameterized type or using a raw type to invoke generic methods, gives warning that raw types bypass generic type checks, deferring the catch of unsafe code to runtime.      

   
        Box<String> stringBox = new Box<>();
        Box rawBox = stringBox;               // OK
        rawBox.set(8);                        // warning: unchecked invocation to set(T)      
        
        Box rawBox = new Box();           // rawBox is a raw type of Box<T>
        Box<Integer> intBox = rawBox;     // warning: unchecked conversion

* **Generic methods:**          
Methods that introduce their own type parameters, but its scope is limited to the method where it is declared. Static, non-static generic methods and generic class constructors are allowed.        


        public class Util {
            public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
                return p1.getKey().equals(p2.getKey()) && p1.getValue().equals(p2.getValue()); 
                }
        }
        Pair<Integer, String> p1 = new Pair<>(1, "apple");
        Pair<Integer, String> p2 = new Pair<>(2, "pear");
        boolean same = Util.<Integer, String>compare(p1, p2);   //type has been explicitly provided 
        boolean same = Util.compare(p1, p2);                    //type inference by compiler    

* **Bounded Type Parameters:**  
Allows to restrict the types that can be used as type arguments in a parameterized type. We list the type parameter's name, followed by the **extends** keyword, followed by its upper bound(can be class or interface)     

           
        public <U extends Number> void inspect(U u){
            System.out.println("T: " + t.getClass().getName());
            System.out.println("U: " + u.getClass().getName());
        }          

* **Multiple Bounds**   
A type variable with multiple bounds is a subtype of all the types listed in the bound. If one of the bounds is a class, it must be specified first.    


        Class A { /* ... */ }
        interface B { /* ... */ }
        interface C { /* ... */ }
        
        class D <T extends A & B & C> { /* ... */ }

The implementation of the method is straightforward, but it does not compile because the greater than operator (>) applies only to primitive types but NOT objects.     


        public static <T> int countGreaterThan(T[] anArray, T elem) {
            int count = 0;
            for (T e : anArray)
                if (e > elem)  // compiler error
                    ++count;
            return count;
        }
To fix the problem, **use a type parameter bounded by the Comparable<T> interface**. The resulting code will be:

     
        public interface Comparable<T> {
            public int compareTo(T o);
        }
        
        public static <T extends Comparable<T>> int countGreaterThan(T[] anArray, T elem) {
            int count = 0;
            for (T e : anArray)
                if (e.compareTo(elem) > 0)
                    ++count;
            return count;
        }


7. In general, if Foo is a subtype of Bar, and G is some generic type declaration, it is **NOT** the case that G<Foo> is a subtype of G<Bar>.   

* **Generic Classes and Subtyping:**       
ArrayList<String> is subtype of List<String>, which is subtype of Collection<String> - **ArrayList<E> implements List<E>, and List<E> extends Collection<E>**   
PayloadList<String,String>, PayloadList<String,Integer>, PayloadList<String,Exception> are subtypes of List<String> - **interface PayloadList<E,P> extends List<E>**

**Type inference** is compiler's ability to look at each method invocation and corresponding declaration to determine the type argument(or arguments). The inference algorithm tries to find the most specific type that works with all of the arguments.       

        public class BoxDemo {
          public static <U> void addBox(U u, java.util.List<Box<U>> boxes) {
            Box<U> box = new Box<>(); box.set(u); boxes.add(box);
          }  

        BoxDemo.addBox(Integer.valueOf(20), listOfIntegerBoxes);
        BoxDemo.<Integer>addBox(Integer.valueOf(10), listOfIntegerBoxes);       //specify the type parameter with a type witness 
        
Using the diamond you can take advantage of type inference during generic class instantiation:      
        
        Map<String, List<String>> myMap = new HashMap<>();       

Constructors can be generic (ie, declare their own formal type parameters) in both generic and non-generic classes.     
        
        class MyClass<X> {
          <T> MyClass(T t) { }
        }
        
        MyClass<Integer> myObject = new MyClass<>("");
        
**Target type** of an expression is the data type that the Java compiler expects depending on where the expression appears.     


        List<String> listOne = Collections.emptyList();     // target typing in assignment
        processStringList(Collections.emptyList());         // target typing in method invocation


**Wildcards**       
**?** - the wildcard and represents an unknown type. 
1. **Upper Bounded Wildcards:** restricts the unknown type to be a specific type or a subtype of that type and is represented using the extends keyword.         


        List<? extends Number> (Works on List of Number and its subtypes Integer, Double, and Float)   

        public static double sumOfList(List<? extends Number> list) {
            double s = 0.0;
            for (Number n : list)   { s += n.doubleValue(); }
            return s;
        }
        List<Integer> li = Arrays.asList(1, 2, 3);
        System.out.println("sum = " + sumOfList(li));       // sum = 6.0
        List<Double> ld = Arrays.asList(1.2, 2.3, 3.5);
        System.out.println("sum = " + sumOfList(ld));       // sum = 7.0

2. **Unbounded Wildcards:** is specified using the wildcard character (?), like **List<?>**. This is called a list of unknown type. There are two scenarios where an unbounded wildcard is a useful approach:       
- If you are writing a method that can be implemented using functionality provided in the Object class.          
- When the code is using methods in the generic class that don't depend on the type parameter. For example, List.size or List.clear. In fact, Class<?> is so often used because most of the methods in Class<T> do not depend on T.                      


        public static void printList(List<Object> list) {       //Not generic - it prints only a list of Object instances; it cannot print List<Integer>, List<String>, List<Double> etc., because they are not subtypes of List<Object>.       
            for (Object elem : list) System.out.println(elem + " ");
            System.out.println();
        }
        
        public static void printList(List<?> list) {            //Because for any concrete type A, List<A> is a subtype of List<?>, you can use printList to print a list of any type:
            for (Object elem: list) System.out.print(elem + " "); 
            System.out.println();
        }


3. **Lower Bounded Wildcards**  restricts the unknown type to be a specific type or a super type of that type.    
        
        
        public static void addNumbers(List<? super Integer> list) {     //works on lists of Integer and the supertypes of Integer, such as Integer, Number, and Object  
            for (int i = 1; i <= 10; i++) { list.add(i); } }
            
4. **Wildcards and Subtyping**      

 
        class A { /* ... */ }
        class B extends A { /* ... */ }
        
        List<B> lb = new ArrayList<>();
        List<A> la = lb;   // compile-time error

To create a relationship between these classes so that the code can access Number's methods through List<Integer>'s elements, use an upper bounded wildcard:

        List<? extends Integer> intList = new ArrayList<>();
        List<? extends Number>  numList = intList;          // OK. List<? extends Integer> is a subtype of List<? extends Number>            
        
5. **Wildcard capture:** The compiler infers the type of a wildcard. For example, a list may be defined as List<?> but, when evaluating an expression, the compiler infers a particular type from the code.        
 
6. **Wildcard Guidelines:**     
    - An "in" variable is defined with an upper bounded wildcard, using the extends keyword.(An "in" variable serves up data to the code)       
    - An "out" variable is defined with a lower bounded wildcard, using the super keyword.(An "out" variable holds data for use elsewhere)      
    - In the case where the "in" variable can be accessed using methods defined in the Object class, use an unbounded wildcard.     
    - In the case where the code needs to access the variable as both an "in" and an "out" variable, do not use a wildcard.     
    
**Type Erasure:** Java compiler erases all type parameters and replaces each with its first bound if the type parameter is bounded, or Object if the type parameter is unbounded. It ensures that no new classes are created for parameterized types; consequently, generics incur no runtime overhead. To implement generics, the Java compiler applies type erasure to:                    
1. Replace all type parameters in generic types with their bounds or Object if the type parameters are unbounded. The produced bytecode, therefore, contains only ordinary classes, interfaces, and methods.            
2. Insert type casts if necessary to preserve type safety.          
3. Generate bridge methods to preserve polymorphism in extended generic types.      


        public class Node<T> {
            private T data;
            private Node<T> next;
            public Node(T data, Node<T> next) { this.data = data; this.next = next; }
            public T getData() { return data; }
        }
        
        public class Node {
            private Object data;    //Because the type parameter T is unbounded, the Java compiler replaces it with Object                                              
            private Node next;
            public Node(Object data, Node next) { this.data = data; this.next = next; }
            public Object getData() { return data; }
        }    
        

        public static <T extends Shape> void draw(T shape) 
        public static void draw(Shape shape)    // compiler replaces T with Shape
        
* **Bridge method:**                
1. Compiler sometimes creates a synthetic method, which is called **a bridge method**, as part of the type erasure process. Given the following two classes:       


        public class Node<T> {
            public T data;
            public Node(T data) { this.data = data; }
            public void setData(T data) { System.out.println("Node.setData"); this.data = data; }
        }
        
        public class MyNode extends Node<Integer> {
            public MyNode(Integer data) { super(data); }
            public void setData(Integer data) { System.out.println("MyNode.setData"); super.setData(data); }
        }              
2. After type erasure, method signatures don't match; the Node.setData(T) becomes Node.setData(Object). As a result, the MyNode.setData(Integer) does not override the Node.setData(Object):       


        public class Node {
            public Object data;
            public Node(Object data) { this.data = data; }
            public void setData(Object data) { System.out.println("Node.setData"); this.data = data; }
        }
        
        public class MyNode extends Node {
            public MyNode(Integer data) { super(data); }
            public void setData(Integer data) { System.out.println("MyNode.setData"); super.setData(data); }
        }
3. To solve this problem and preserve the polymorphism of generic types after type erasure, the Java compiler generates a bridge method to ensure that subtyping works as expected.        
        
        
        class MyNode extends Node {
            // Bridge method generated by the compiler
            public void setData(Object data) { setData((Integer) data); }
            public void setData(Integer data) { System.out.println("MyNode.setData"); super.setData(data); }
        }
        
* [**Restrictions on Generics:**](https://docs.oracle.com/javase/tutorial/java/generics/restrictions.html)        
1. Cannot Instantiate Generic Types with Primitive Types: 
        
        List<int> list = new ArrayList<>();              
2. Cannot Create Instances of Type Parameters:        
        
        E elem = new E();  // compile-time error
3. Cannot Declare Static Fields Whose Types are Type Parameters:    
        
        private static T obj;    
4. Cannot Use Casts or instanceof With Parameterized Types:      

        if (list instanceof ArrayList<Integer>)  
                OR 
        List<Integer> li = new ArrayList<>();
        List<Number>  ln = (List<Number>) li; 
5. Cannot Create Arrays of Parameterized Types:      
        
        List<Integer>[] arrayOfLists = new List<Integer>[2]; 
6. Cannot Create, Catch, or Throw Objects of Parameterized Types:

        class MathException<T> extends Exception { /* ... */ }    // compile-time error
        class QueueFullException<T> extends Throwable { /* ... */ // compile-time error
        
        public static <T extends Exception, J> void execute(List<J> jobs) {
            try {
                for (J job : jobs)
                    // ...
            } catch (T e) {   // compile-time error
                // ...
            }
        }        

7. Cannot Overload a Method Where the Formal Parameter Types of Each Overload Erase to the Same Raw Type:            
        
        public class Example {
            public void print(Set<String> strSet) { }
            public void print(Set<Integer> intSet) { }
        }
        

### Regular Expression         
1. The java.util.regex package primarily consists of three classes: Pattern, Matcher, and PatternSyntaxException:       
    - A [**Pattern**](https://docs.oracle.com/javase/tutorial/essential/regex/pattern.html) object is a compiled representation of a regular expression. The Pattern class provides no public constructors. To create a pattern, you must first invoke one of its public static compile methods, which will then return a Pattern object. These methods accept a regular expression as the first argument; the first few lessons of this trail will teach you the required syntax.
    - A [**Matcher**](https://docs.oracle.com/javase/tutorial/essential/regex/matcher.html) object is the engine that interprets the pattern and performs match operations against an input string. Like the Pattern class, Matcher defines no public constructors. You obtain a Matcher object by invoking the matcher method on a Pattern object.
    - A **PatternSyntaxException** object is an unchecked exception that indicates a syntax error in a regular expression pattern.
     
2.  **Metacharacter** is a character with special meaning interpreted by the matcher. The metacharacters supported by this API are <([{\^-=$!|]})?*+.>. 
To force a metacharacter to be treated as an ordinary character, precede the metacharacter with a backslash, or enclose it within \Q (which starts the quote) and \E (which ends it).   

3. In the context of regular expressions, a **character class** is a set of characters enclosed within square brackets. It specifies the characters that will successfully match a single character from a given input string.      


    [abc]	        a, b, or c (simple class)
    [^abc]	        Any character except a, b, or c (negation)
    [a-zA-Z]	a through z, or A through Z, inclusive (range)
    [a-d[m-p]]	a through d, or m through p: [a-dm-p] (union)
    [a-z&&[def]]	d, e, or f (intersection)
    [a-z&&[^bc]]	a through z, except for b and c: [ad-z] (subtraction)
    [a-z&&[^m-p]]	a through z, and not m through p: [a-lq-z] (subtraction) 
4. The Pattern API contains a number of useful **predefined character classes**, which offer convenient shorthands for commonly used regular expressions:       


        .	Any character (may or may not match line terminators)
        \d	A digit: [0-9]
        \D	A non-digit: [^0-9]
        \s	A whitespace character: [ \t\n\x0B\f\r]
        \S	A non-whitespace character: [^\s]
        \w	A word character: [a-zA-Z_0-9]
        \W	A non-word character: [^\w]              
        
5. **Constructs** beginning with a backslash are called **escaped constructs**. If you are using an escaped construct within a string literal, you must precede the backslash with another backslash for the string to compile.     
        
        
        private final String REGEX = "\\d"; // a single digit        
6. **Quantifiers** allow you to specify the number of occurrences to match against. For convenience, the three sections of the Pattern API specification describing greedy, reluctant, and possessive quantifiers are presented below.         


        Greedy	Reluctant	Possessive	Meaning
        X?	    X??	        X?+	        X, once or not at all
        X*	    X*?	        X*+	        X, zero or more times
        X+	    X+?	        X++	        X, one or more times
        X{n}	X{n}?	    X{n}+	    X, exactly n times
        X{n,}	X{n,}?	    X{n,}+	    X, at least n times
        X{n,m}	X{n,m}?	    X{n,m}+	    X, at least n but not more than m times

7. **Capturing groups** are a way to treat multiple characters as a single unit. They are created by placing the characters to be grouped inside a set of parentheses.  
The section of the input string matching the capturing group(s) is saved in memory for later recall via backreference. A **backreference** is specified in the regular expression as a backslash (\) followed by a digit indicating the number of the group to be recalled. 

8. You can make your pattern matches more precise by specifying where pattern matching takes place with **boundary matchers**.      


        Boundary Construct	Description
        ^	                The beginning of a line
        $	                The end of a line
        \b	                A word boundary
        \B	                A non-word boundary
        \A	                The beginning of the input
        \G	                The end of the previous match
        \Z	                The end of the input but for the final terminator, if any
        \z	                The end of the input


### JVM Architecture                      
* JRE is the implementation of JVM. The compiler compiles the Java file into a Java .class file, then that .class file is input into the JVM, which loads and executes the class file. JVM is divided into three main subsystems:        
* [Java Memory Model](http://tutorials.jenkov.com/java-concurrency/java-memory-model.html)      
* [JVM Architecture](https://dzone.com/articles/jvm-architecture-explained) 
1. **ClassLoader Subsystem:**  It loads, links. and initializes the class file when it refers to a class for the first time at runtime, not compile time.      
    1. **Loading** - Classes will be loaded by this component. BootStrap ClassLoader, Extension ClassLoader, and Application ClassLoader are the three ClassLoaders that will help in achieving it.
        - **BootStrap ClassLoader** – Responsible for loading classes from the bootstrap classpath, nothing but rt.jar. Highest priority will be given to this loader.
        - **Extension ClassLoader** – Responsible for loading classes which are inside the ext folder (jre\lib).
        - **Application ClassLoader** –Responsible for loading Application Level Classpath, path mentioned Environment Variable, etc.
    
    2. **Linking**
        - **Verify** – Bytecode verifier will verify whether the generated bytecode is proper or not if verification fails we will get the verification error.
        - **Prepare** – For all static variables memory will be allocated and assigned with default values.
        - **Resolve** – All symbolic memory references are replaced with the original references from Method Area.
    
    3. **Initialization:** This is the final phase of ClassLoading; here, all static variables will be assigned with the original values, and the static block will be executed.        

2. **Runtime Data Area:** It is divided into five major components:     
    1. **Method Area** – All the class-level data will be stored here, including static variables. There is only one method area per JVM, and it is a shared resource.
    2. **Heap Area** – All the Objects and their corresponding instance variables and arrays will be stored here. There is also one Heap Area per JVM. Since the Method and Heap areas share memory for multiple threads, the data stored is not thread-safe.
    3. **Stack Area** – For every thread, a separate runtime stack will be created. For every method call, one entry will be made in the stack memory which is called Stack Frame. All local variables will be created in the stack memory. The stack area is thread-safe since it is not a shared resource. The Stack Frame is divided into three subentities:
        - **Local Variable Array** – Related to the method how many local variables are involved and the corresponding values will be stored here.
        - **Operand stack** – If any intermediate operation is required to perform, operand stack acts as runtime workspace to perform the operation.
        - **Frame data** – All symbols corresponding to the method is stored here. In the case of any exception, the catch block information will be maintained in the frame data.
    4. **PC Registers** – Each thread will have separate PC Registers, to hold the address of current executing instruction once the instruction is executed the PC register will be updated with the next instruction.
    5. **Native Method stacks** – Native Method Stack holds native method information. For every thread, a separate native method stack will be created.

3. **Execution Engine:** The bytecode, which is assigned to the Runtime Data Area, will be executed by the Execution Engine. The Execution Engine reads the bytecode and executes it piece by piece.        
    1. **Interpreter** – The interpreter interprets the bytecode faster but executes slowly. The disadvantage of the interpreter is that when one method is called multiple times, every time a new interpretation is required.
    2. **JIT Compiler** – The JIT Compiler neutralizes the disadvantage of the interpreter. The Execution Engine will be using the help of the interpreter in converting byte code, but when it finds repeated code it uses the JIT compiler, which compiles the entire bytecode and changes it to native code. This native code will be used directly for repeated method calls, which improve the performance of the system.
        - **Intermediate Code Generator** – Produces intermediate code
        - **Code Optimizer** – Responsible for optimizing the intermediate code generated above
        - **Target Code Generator** – Responsible for Generating Machine Code or Native Code
        - **Profiler** – A special component, responsible for finding hotspots, i.e. whether the method is called multiple times or not.
    3. **Garbage Collector:** Collects and removes unreferenced objects. Garbage Collection can be triggered by calling System.gc(), but the execution is not guaranteed. Garbage collection of the JVM collects the objects that are created.

**Java Native Interface (JNI):** JNI will be interacting with the Native Method Libraries and provides the Native Libraries required for the Execution Engine.
**Native Method Libraries:** This is a collection of the Native Libraries, which is required for the Execution Engine.  

### Reference Types
1. **Strong Reference:**
The object on the heap it is not garbage collected while there is a strong reference pointing to it, or if it is strongly reachable through a chain of strong references.   
        
        
        StringBuilder builder = new StringBuilder();
              

2. **Weak Reference:**
In simple terms, a weak reference to an object from the heap is most likely to not survive after the next garbage collection process. A nice use case for weak references are caching scenarios. 


        WeakReference<StringBuilder> reference = new WeakReference<>(new StringBuilder());
A nice implementation for caching scenarios is the collection WeakHashMap<K,V>, its entries actually extend the WeakReference class and uses its ref field as the map’s key:        

        private static class Entry<K,V> extends WeakReference<Object> implements Map.Entry<K,V> {
            V value;
Once a key from the WeakHashMap is garbage collected, the entire entry is removed from the map.

3. **Soft Reference:**      
These types of references are used for more memory-sensitive scenarios, since those are going to be garbage collected only when your application is running low on memory. Therefore, as long as there is no critical need to free up some space, the garbage collector will not touch softly reachable objects. The Javadocs state, “all soft references to softly-reachable objects are guaranteed to have been cleared before the virtual machine throws an OutOfMemoryError.”        


        SoftReference<StringBuilder> reference = new SoftReference<>(new StringBuilder());


4. **Phantom Reference:**       
Used to schedule post-mortem cleanup actions, since we know for sure that objects are no longer alive. Used only with a reference queue, since the .get() method of such references will always return null. These types of references are considered preferable to finalizers.     
    
### String Pool         
For strings, Java manages a string pool in memory. This means that Java stores and reuse strings whenever possible. This is mostly true for string literals.        


        String localPrefix = "297"; //1
        String prefix = "297";      //2    
        String localPrefix = new Integer(297).toString();   //3  created in heap  
        prefix == localPrefix       //1 and 2 are equal, but 1 and 3 are different     
        String localPrefix = new Integer(297).toString().intern(); // we can force the JVM to add it to the string pool by adding the .intern()
        
### Garbage Collection      
1. The Java runtime environment has a **garbage collector** that periodically frees the memory used by objects that are no longer referenced.         
2. System.gc() is a request to Java to run the garbage collector, but it’s, again, up to it whether or not to do that.           
3. GC works in two simple steps known as **Mark and Sweep**:            
    **Mark** – It is where the garbage collector identifies which pieces of memory are in use and which are not.        
    **Sweep** – This step removes objects identified during the “mark” phase.              
4. When an object is created, it is allocated on the Eden space. Because the Eden space is not that big, it gets full quite fast. The garbage collector runs on the Eden space and marks objects as alive.
Once an object survives a garbage collecting process, it gets moved into a survivor space S0. The 2nd time the garbage collector runs on the Eden space, it moves all surviving objects into the S1 space. Also, everything that is currently on S0 is moved into the S1 space.     
If an object survives for X rounds of garbage collection (X depends on the JVM implementation, in my case it’s 8), it is most likely that it will survive forever, and it gets moved into the Old space.        

5. Memory related errors:       
    

        java.lang.StackOverFlowError — indicates that Stack Memory is full
        java.lang.OutOfMemoryError: Java heap space — indicates that Heap Memory is full
        java.lang.OutOfMemoryError: GC Overhead limit exceeded — indicates that GC has reached its overhead limit
        java.lang.OutOfMemoryError: Permgen space — indicates that Permanent Generation space is full
        java.lang.OutOfMemoryError: Metaspace — indicates that Metaspace is full (since Java 8)
        java.lang.OutOfMemoryError: Unable to create new native thread — indicates that JVM native code can no longer create a new native thread from the underlying operating system because so many threads have been already created and they consume all the available memory for the JVM
        java.lang.OutOfMemoryError: request size bytes for reason — indicates that swap memory space is fully consumed by application
        java.lang.OutOfMemoryError: Requested array size exceeds VM limit– indicates that our application uses an array size more than the allowed size for the underlying platform

Starting with Java 8, the Metaspace replaces the PermGen.   
PermGen (Permanent Generation) is a special heap space separated from the main memory heap. It contains data about bytecode(class and method objects), names, and JIT information. With its limited memory size, PermGen is involved in generating the famous OutOfMemoryError. Simply put, the class loaders weren't garbage collected properly and, as a result, generated a memory leak.      
-XX:PermSize=[size] is the initial or minimum size of the PermGen space         
-XX:MaxPermSize=[size] is the maximum size          

**JVM has five types of GC implementations:**   
1. **Serial Garbage Collector:**  This is the simplest GC implementation, as it basically works with a single thread. 
As a result, this GC implementation freezes all application threads when it runs. Hence, it is not a good idea to use it in multi-threaded applications like server environments.
        
        java -XX:+UseSerialGC -jar Application.java
2. **Parallel Garbage Collector:** This uses multiple threads for managing heap space. But it also freezes other application threads while performing GC.   
        
        java -XX:+UseParallelGC -jar Application.java
3. **CMS Garbage Collector:**   The Concurrent Mark Sweep (CMS) implementation uses multiple garbage collector threads for garbage collection. 
It's designed for applications that prefer shorter garbage collection pauses, and that can afford to share processor resources with the garbage collector while the application is running.           
  
        java -XX:+UseParNewGC -jar Application.java 
4. **G1 Garbage Collector:** G1 (Garbage First) Garbage Collector is designed for applications running on multi-processor machines with large memory space.     
G1 collector partitions the heap into a set of equal-sized heap regions, each a contiguous range of virtual memory. When performing garbage collections, G1 shows a concurrent global marking phase (i.e. phase 1 known as Marking) to determine the liveness of objects throughout the heap.   
After the mark phase is completed, G1 knows which regions are mostly empty. It collects in these areas first, which usually yields a significant amount of free space (i.e. phase 2 known as Sweeping). It is why this method of garbage collection is called Garbage-First.        

        java -XX:+UseG1GC -jar Application.java
5. **Z Garbage Collector:**  ZGC (Z Garbage Collector) is a scalable low-latency garbage collector which debuted in Java 11 as an experimental option for Linux. JDK 14 introduced  ZGC under the Windows and macOS operating systems. ZGC has obtained the production status from Java 15 onwards.
ZGC performs all expensive work concurrently, without stopping the execution of application threads for more than 10 ms, which makes it suitable for applications that require low latency. 
It uses load barriers with colored pointers to perform concurrent operations when the threads are running and they are used to keep track of heap usage.          
Reference coloring (colored pointers) is the core concept of ZGC. It means that ZGC uses some bits (metadata bits) of reference to mark the state of the object. It also handles heaps ranging from 8MB to 16TB in size. 
Furthermore, pause times do not increase with the heap, live-set, or root-set size. Similar to G1, Z Garbage Collector partitions the heap, except that heap regions can have different sizes.  

        java -XX:+UnlockExperimentalVMOptions -XX:+UseZGC Application.java      //before version 15
        java -XX:+UseZGC Application.java       //From version 15 we don't need experimental mode on

**Metaspace** is used to store the metadata about your loaded classes in the JVM. It is a native memory region(NOT part of heap) that grows automatically by default, and JVM reduces the chance to get the OutOfMemory error. We also have new flags to tune the memory:           
    - MetaspaceSize and MaxMetaspaceSize – we can set the Metaspace upper bounds.       
    - MinMetaspaceFreeRatio – is the minimum percentage of class metadata capacity free after garbage collection        
    - MaxMetaspaceFreeRatio – is the maximum percentage of class metadata capacity free after a garbage collection to avoid a reduction in the amount of space      


### Serialization  
To **serialize** an object means to convert its state to a byte stream so that the byte stream can be reverted back into a copy of the object. A Java object is serializable if its class or any of its superclasses implements either the java.io.Serializable interface or its subinterface, java.io.Externalizable.       
**Deserialization** is the process of converting the serialized form of an object back into a copy of the object.        

