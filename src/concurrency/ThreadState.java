package concurrency;

import java.util.concurrent.TimeUnit;

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
