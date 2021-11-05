package concurrency;

import java.util.concurrent.TimeUnit;

public class Runnables {
    public static Runnable simpleRunnable = () -> System.out.println("Started runnable at: "+ System.currentTimeMillis());

    public static Runnable waitingRunnable = () -> {
        long startTime = System.currentTimeMillis();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Sleep interrupted!!");
        }
        System.out.println("Started runnable at: " + startTime);
    };
}
