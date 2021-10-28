package concurrency;

public class Runnables {
    public static Runnable simpleRunnable = () -> System.out.println("Started runnable at: "+ System.currentTimeMillis());
}
