package concurrency;

import static java.lang.Thread.*;

public class ThreadTutorial {
    public static void main(String[] args) throws Exception {
        ThreadTutorial threadTutorial = new ThreadTutorial();
        //threadTutorial.threadClass();
    }

    public void threadClass() throws Exception {
        //Interrupts this thread.
        Thread.currentThread().interrupt();

        //Tests whether the current thread has been interrupted.  The
        //**interrupted status** of the thread is cleared by this method.
        interrupted();

        //Tests whether this thread has been interrupted.  The **interrupted
        //status** of the thread is unaffected by this method.
        Thread.currentThread().isInterrupted();


/*
Thread.currentThread();
Thread.yield();
Thread.sleep();
Thread.clone();
Thread.start();
Thread.run();
Thread.stop();
Thread.stop();
Thread.destroy();
Thread.isAlive();
Thread.suspend();
Thread.resume();
Thread.setPriority();
Thread.getPriority();
Thread.setName();
Thread.getName();
Thread.getThreadGroup();
Thread.activeCount();
Thread.enumerate();
Thread.countStackFrames();
Thread.join();
Thread.join();
Thread.join();
Thread.dumpStack();
Thread.setDaemon();
Thread.isDaemon();
Thread.checkAccess();
Thread.toString();
Thread.getContextClassLoader();
Thread.setContextClassLoader();
Thread.holdsLock();
Thread.getStackTrace();
Thread.getAllStackTraces();
*/
    }
}
