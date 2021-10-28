package concurrency;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static concurrency.Callables.simpleCallable;

public class ExecutorServiceTutorial {
    public static void main(String[] args) {
        commonMethods();
        invokeAllUsage();

    }

    private static void invokeAllUsage() {
        ExecutorService executorService = Executors.newWorkStealingPool();
        ExecutorService executorServiceTwo = Executors.newWorkStealingPool();
        List<Callable<Integer>> tasks = Arrays.asList(simpleCallable, simpleCallable, simpleCallable);
        try {
            // invokeAll(tasks) executes the given tasks, returning a list of Futures holding their status and results when all complete(either normally or by throwing an exception).
            // Future.isDone() is true for each element of the returned list.
            // The results of this method are undefined if the given collection is modified while this operation is in progress.
            List<Future<Integer>> results = executorService.invokeAll(tasks);

            // invokeAll(tasks, 10, TimeUnit.SECONDS) executes the given tasks, returning a list of Futures holding their status and results when all complete(either normally or by throwing an exception) or the timeout expires, whichever happens first.
            // Upon return, tasks that have not completed are cancelled.
            List<Future<Integer>> resultsWithTimeOut = executorServiceTwo.invokeAll(tasks, 10, TimeUnit.SECONDS);

            // Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do.
            // Upon normal or exceptional return,tasks that have not completed are cancelled.
            Integer result = executorService.invokeAny(tasks);

            // Executes the given tasks, returning the result of one that has completed successfully (i.e., without throwing an exception), if any do before the given timeout elapses.
            // Upon normal or exceptional return, tasks that have not completed are cancelled.
            Integer resultTwo = executorService.invokeAny(tasks, 10, TimeUnit.SECONDS);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Task interrupted!!");
        } catch (ExecutionException ex) {   // if no task successfully completes
            ex.printStackTrace();
        } catch (TimeoutException ex) {     // if the given timeout elapses before any task successfully completes
            ex.printStackTrace();
        } catch (RejectedExecutionException ex) {
            ex.printStackTrace();           // if tasks cannot be scheduled for execution
        }
    }

    public static void commonMethods() {
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
