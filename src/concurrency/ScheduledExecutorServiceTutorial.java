package concurrency;

import java.util.concurrent.*;

public class ScheduledExecutorServiceTutorial {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // Creates and executes a ScheduledFuture that becomes enabled after the given delay.
        ScheduledFuture<Integer> scheduledCallableResult = executorService.schedule(Callables.simpleCallable, 5, TimeUnit.SECONDS);

        long initialDelay = 1;
        long delay = 2;

        // Creates and executes a periodic action that executes after the given initial delay, and subsequently with the given delay between the termination of one execution and the commencement of the next.
        // If any execution of the task encounters an exception, subsequent executions are suppressed.Otherwise, the task will only terminate via cancellation or termination of the executor.
        executorService.scheduleWithFixedDelay(Runnables.simpleRunnable, initialDelay, delay, TimeUnit.SECONDS);

        // Creates and executes a periodic action whose executions commence after initialDelay then initialDelay+period and so on.
        // If any execution of the task encounters an exception, subsequent executions are suppressed. Otherwise, the task will only terminate via cancellation or termination of the executor.
        // If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute.
        executorService.scheduleAtFixedRate(Runnables.simpleRunnable, initialDelay, delay, TimeUnit.SECONDS);


        TimeUnit.SECONDS.sleep(2);
        // getDelay() returns the remaining delay associated with the ScheduledFuture object, in the given time unit.
        // zero or negative values indicate that the delay has already elapsed.
        System.out.printf("Remaining delay: %d sec%n", scheduledCallableResult.getDelay(TimeUnit.SECONDS));
        Integer result = scheduledCallableResult.get();
        System.out.println("callable returned: " + result);
    }
}
