package concurrency;

import java.util.Random;
import java.util.concurrent.*;

public class ExecutorServiceTutorial {
    public static void main(String[] args) {
        Callable<Integer> simpleCallable  = () -> {
            TimeUnit.SECONDS.sleep(5);
            Random random = new Random();
            return random.nextInt();
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> callableResult = executorService.submit(simpleCallable);
        Future<Integer> callableResultTwo = executorService.submit(simpleCallable);

        Integer result = getResult(callableResult);


        try {
            System.out.println("Shutting down executor");
        // shutdown() initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
        // Invocation has no additional effect if already shut down. This method does not wait for previously submitted tasks to complete execution.
            executorService.shutdown();

        // awaitTermination() blocks until all tasks have completed execution after a shutdown request, or the timeout occurs,
        // or the current thread is interrupted, whichever happens first.
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Tasks interrupted");
        } finally {
        // isTerminated() returns true if all tasks have completed following shut down.
            if (!executorService.isTerminated()) {
                System.out.println("Cancelling all pending tasks");
            }

        // shutdownNow() attempts(not guarantees) to stop all actively executing tasks, halts the processing of waiting tasks,
        // and returns a list of the tasks that were awaiting execution. It does not wait for actively executing tasks to terminate.
        // For example, typical implementations will cancel via Thread.interrupt(), so any task that fails to respond to interrupts may never terminate.
            executorService.shutdownNow();
            System.out.println("shutdown complete");
        }

        Integer resultTwo = getResult(callableResultTwo);   // throws InterruptedException as shutdownNow() got called while this task was ongoing
    }

    public static <T> T getResult(Future<T> callableResult)  {
        T result = null;
        try {
            // isDone() returns true if this task completed due to normal termination, an exception, or cancellation
            System.out.println(callableResult.isDone());
            // get() waits if necessary for the computation to complete, and then retrieves its result.
            result = callableResult.get();

            // get(long timeout, TimeUnit unit) waits if necessary for at most the given time for the computation to complete,
            // and then retrieves its result, if available.
            result = callableResult.get(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {  // if the current thread was interrupted while waiting
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {    // if the current thread was interrupted while waiting
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("Due to callableResult.get(1, TimeUnit.SECONDS)");
            e.printStackTrace();
        }
        return result;
    }
}
