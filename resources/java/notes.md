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
        
                // ------------------For Blocked state-------------
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
  The Future's get(), a blocking method, will return the task's result upon successful completion.   
  The Future's isDone() tells if the future has already finished execution.

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
  
        