package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class SynchronizationAndLocks {

    int count = 0;
    int countThreadSafe = 0;

    private final Object lock = new Object();
    Runnable increment = () -> count += 1;
    Runnable syncIncrement = () -> {
        synchronized (lock) {
            countThreadSafe += 1;
        }
    };

    public static void main(String[] args) {
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

    }
}
