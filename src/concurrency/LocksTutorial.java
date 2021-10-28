package concurrency;

import sun.applet.resources.MsgAppletViewer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class LocksTutorial {
    public static void main(String[] args) {
        // Lock Interface
        //Commonly, a lock provides exclusive access to a shared resource: only one thread at a time can acquire the lock and all access to the shared resource requires that the lock be acquired first.
        //However, some locks may allow concurrent access to a shared resource, such as the read lock of a ReadWriteLock
        reenterantLock();
        readWriteLock();
        stampedLock();
    }

    private static void reenterantLock() {
        // ReenterantLock
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

    }

    private static void readWriteLock() {
        // ReadWriteLock
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

        ExecutorServiceTutorial.shutdown(executorService);

    }

    private static void stampedLock() {
        // StampedLock
        StampedLock stampedLock = new StampedLock();
        ExecutorService executorServiceTwo = Executors.newFixedThreadPool(2);
        Map<String, String> map = new HashMap<>();
        Runnable writeTaskTwo = () -> {
            long stamp = stampedLock.writeLock();
            try {
                TimeUnit.SECONDS.sleep(1);
                map.put("key", "value");
            } catch (Exception ex) {
                System.out.println("Error while updating data");
            } finally {
                //If the current thread is the holder of this lock then the hold count is decremented.
                //If the hold count is now zero then the lock is released.
                //If the current thread is not the holder of this lock then IllegalMonitorStateException is thrown.
                stampedLock.unlock(stamp);
            }
        };
        executorServiceTwo.submit(writeTaskTwo);

        Runnable readTaskTwo = () -> {
            //Acquires the read lock if the write lock is not held by another thread and returns immediately.
            //If the write lock is held by another thread then the current thread waits until the read lock has been acquired.
            long stamp = stampedLock.readLock();
            try {
                map.get("key");
            } catch (Exception ex) {
                System.out.println("Error while fetching data");
            } finally {
                // If the number of readers is now zero then the lock is made available for write lock attempts.
                stampedLock.unlock(stamp);
            }
        };
        IntStream.range(1, 5).forEach(i -> executorServiceTwo.submit(readTaskTwo));

        ExecutorServiceTutorial.shutdown(executorServiceTwo);

    }
}
