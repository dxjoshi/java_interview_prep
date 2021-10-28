package concurrency;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Callables {
    public static Callable<Integer> simpleCallable  = () -> {
        TimeUnit.SECONDS.sleep(5);
        Random random = new Random();
        return random.nextInt();
    };

}
