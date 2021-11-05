## Java Interview Refresher:  

## Topics:
* [Thread Basics](#thread-basics)
* [ThreadPoolExecutor](#threadpoolexecutor)
* [ForkJoinPool](#forkjoinpool)
* [CompletionService](#completionservice)
* [Executor Factory Methods](#executor-factory-methods)
* [Concurrency Questions](#concurrency-questions)
* [Iterators](#iterators)
* [Collections](#collections)
* [Arrays Class](#arrays-class)
* [Collections Class](#collections-class)
* [ConcurrentModificationException](#concurrentmodificationexception)
* [Comparator and Comparable Interface](#comparator-and-comparable-interface)
* [Java 8](#java-8)

Generics
Exception
Collections
Overloading and Overriding
Serialization



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
* **Comparator:**       
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
  
         
      