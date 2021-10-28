## Java Interview Refresher:  

## Topics:
* [Concurrency](#concurrency)
* Generics
Exception
Collections
Overloading and Overriding



### Concurrency
* [Concurrency vs. Parallelism](https://stackoverflow.com/questions/1050222/what-is-the-difference-between-concurrency-and-parallelism)
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
     

        