package concurrency;

import sun.applet.resources.MsgAppletViewer;

import java.sql.Time;
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
        stampedOptimisticLock();
        stampedTryConvertToWriteLock();
    }

    private static void stampedTryConvertToWriteLock() {
        StampedLock stampedLock = new StampedLock();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
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
    }


    private static void stampedOptimisticLock() {
        StampedLock stampedLock = new StampedLock();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable writeTask = () -> {
            long stamp = stampedLock.writeLock();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception ex) {
                System.out.println("Error while updating data");
            } finally {
                stampedLock.unlock(stamp);
            }
        };
        executorService.submit(writeTask);

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
        executorService.submit(readTask);
        executorService.submit(readTask);

        ExecutorServiceTutorial.shutdown(executorService);
    }
}
