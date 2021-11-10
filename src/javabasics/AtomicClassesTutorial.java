package javabasics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicClassesTutorial {
    public static void main(String[] args) {
        AtomicInteger atomicInteger  = new AtomicInteger(10);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorServiceTwo = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
        executorServiceTwo.submit(() -> atomicInteger.compareAndSet(11, 15));
        executorService.shutdown();
        try {
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
        executorServiceTwo.shutdown();

        System.out.println(atomicInteger.get());
    }
}
