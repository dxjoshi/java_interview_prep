package concurrency;

import java.util.concurrent.TimeUnit;

public class ConcurrencyUtils {
    public static void sleep(long timeoutInSec) {
        try {
            TimeUnit.SECONDS.sleep(timeoutInSec);
        }catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ex.printStackTrace();
        }
    }
}
