## Java Interview Refresher:  

## Topics:
* [Concurrency](#concurrency)
* 



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
                        childThread.join(); // this joins childThread to current parentThread, so it can't execute ahead until childThread finishes
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

          