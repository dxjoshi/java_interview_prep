package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class LockTutorialsTwo {
    public static void main(String[] args) {
        semaphores();
    }

    private static void semaphores() {
        //Creates a Semaphore with the given number of permits and nonfair fairness setting.
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

        IntStream.range(0,10).forEach(x -> executorService.submit(task));
        ConcurrencyUtils.sleep(5);
        ExecutorServiceTutorial.shutdown(executorService);

    }
}
