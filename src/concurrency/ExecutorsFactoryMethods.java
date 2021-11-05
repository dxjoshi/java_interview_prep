package concurrency;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ExecutorsFactoryMethods {

    public static void main(String[] args) {

        threadPoolExecutor();
        forkJoinPool();
        executorsFactoryMethods();
        completionService();

        //Advanced Futures - CompletableFuture, CountedCompleter, ForkJoinTask, FutureTask

    }

    private static void completionService() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        BlockingQueue unboundedBlockingQueue = new LinkedBlockingQueue();

        //It can for example be used to manage asynchronous I/O, in which tasks that perform reads are submitted in one part of a program or system, and then acted upon in a different part of the program when the reads complete, possibly in a different order than they were requested.
        //A CompletionService that uses a supplied Executor to execute tasks.  This class arranges that submitted tasks are, upon completion, placed on a queue accessible using take.
        ExecutorCompletionService completionService = new ExecutorCompletionService(executorService, unboundedBlockingQueue);

        for (int i = 1; i < 11; i++) {
            completionService.submit(Callables.simpleCallable);
        }

        for (int i = 1; i < 11; i++) {
            try {
                completionService.take().get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private static void forkJoinPool() {

        RecursiveAction recursiveAction = new RecursiveAction() {
            @Override
            protected void compute() {
                System.out.println("Dummy action");
            }
        };

        RecursiveTask<Integer> recursiveTask = new RecursiveTask<Integer>() {
            int val = 1;
            @Override
            protected Integer compute() {
                return val*2;
            }
        };

        CountedCompleter<Integer> countedCompleter = new CountedCompleter<Integer>() {
            @Override
            public void compute() {
                System.out.println("NoOp");
            }
        };


        ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;

        //parallelism - the parallelism level. For default value, use java.lang.Runtime#availableProcessors()
        //factory - the factory for creating new threads. For default value, use defaultForkJoinWorkerThreadFactory
        //handler - the handler for internal worker threads that terminate due to unrecoverable errors encountered while executing tasks. For default value, use null.
        //asyncMode - if true, establishes local FIFO scheduling mode for forked tasks that are never joined. else LIFO (by default)
        //public ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, UncaughtExceptionHandler handler,boolean asyncMode) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4, defaultForkJoinWorkerThreadFactory, null, true);

        //Returns the common pool instance. This pool is statically constructed; its run state is unaffected by attempts to shutdown() or shutdownNow().
        //However this pool and any ongoing processing are automatically terminated upon program System.exit().
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        //Arranges for (asynchronous) execution of the given task.
        forkJoinPool.execute(recursiveTask);
        //Submits a ForkJoinTask for execution.
        forkJoinPool.submit(recursiveTask);
        //Performs the given task, returning its result upon completion.
        //If the computation encounters an unchecked Exception or Error, it is rethrown as the outcome of this invocation.
        //Rethrown exceptions behave in the same way as regular exceptions, but, when possible, contain stack traces of both the current thread as well as the thread actually encountering the exception;minimally only the latter.
        forkJoinPool.invoke(recursiveTask);

    }

    private static void executorsFactoryMethods() {
        /*--------------------------------ThreadPoolExecutor-----------------------------------*/

        //return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory));
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());

        //return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        //At any point, at most nThreads threads will be active processing tasks. If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is available.
        //If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.  The threads in the pool will exist until it is explicitly shutdown.
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5, Executors.defaultThreadFactory());

        //return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
        //Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available, and uses the provided ThreadFactory to create new threads when needed.
        // USUALLY FOR SHORT-LIVED TASKS
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

        /*--------------------------------ScheduledThreadPoolExecutor-----------------------------------*/

        //super(1, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue(), threadFactory);
        //Creates a single-threaded executor that can schedule commands to run after a given delay, or to execute periodically.
        //Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time.
        //Unlike the otherwise equivalent newScheduledThreadPool(1) the returned executor is guaranteed not to be reconfigurable to use additional threads.
        ExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());

        //super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue(), threadFactory);
        //Creates a thread pool that can schedule commands to run after a given delay, or to execute periodically.
        ExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(5, Executors.defaultThreadFactory());

        /*--------------------------------ForkJoinPool-----------------------------------*/

        //return new ForkJoinPool(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
        //Creates a thread pool that maintains enough threads to support the given parallelism level, and may use multiple queues to reduce contention.
        //The parallelism level corresponds to the maximum number of threads actively engaged in, or available to engage in, task processing. The actual number of threads may grow and shrink dynamically.
        //A work-stealing pool makes no guarantees about the order in which submitted tasks are executed.
        ExecutorService newWorkStealingPool = Executors.newWorkStealingPool(4);
    }

    private static void threadPoolExecutor() {
        List<Runnable> initialList = Arrays.asList(Runnables.simpleRunnable, Runnables.simpleRunnable, Runnables.simpleRunnable);
        //Creates an ArrayBlockingQueue with the given (fixed) capacity, the specified access policy and initially containing the elements of the given collection, added in traversal order of the collection's iterator.
        BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(10, true, initialList);
        //Creates a LinkedBlockingQueue with a capacity of Integer#MAX_VALUE.
        BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();
        //Creates a SynchronousQueue with the specified fairness policy.
        BlockingQueue<Runnable> synchronousQueue = new SynchronousQueue<>(true);

        //A handler that may be invoked by a ThreadPoolExecutor when execute() cannot accept a task.
        //This may occur when no more threads or queue slots are available because their bounds would be exceeded, or upon shutdown of the Executor.
        //In the absence of other alternatives, the method may throw an unchecked RejectedExecutionException, which will be propagated to the caller of execute().
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("Unable to process the runnable: "+ r);
            }
        };

        RejectedExecutionHandler discardOldestPolicy =  new ThreadPoolExecutor.DiscardOldestPolicy();

        //Constructs a new Thread.  Implementations may also initialize priority, name, daemon status, ThreadGroup, etc.
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        };

        //This factory creates all new threads used by an Executor in the same ThreadGroup. If there is a  java.lang.SecurityManager, it uses the group of System#getSecurityManager, else the group of the thread invoking this defaultThreadFactory method.
        //Each new thread is created as a non-daemon thread with priority set to the smaller of Thread.NORM_PRIORITY and the maximum priority permitted in the thread group.
        //New threads have names accessible via name  pool-N-thread-M, where N is the sequence number of this factory, and M is the sequence number of the thread created by this factory.
        ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
        ThreadFactory priviledgedThradFactory = Executors.privilegedThreadFactory();

        //corePoolSize - #threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set.
        //maximumPoolSize - maximum #threads to allow in the pool
        //keepAliveTime - when #threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
        //unit - the time unit for the keepAliveTime argument
        //workQueue - the queue to use for holding tasks before they are executed.  This queue will hold only  Runnable tasks submitted by the execute() method.
        //threadFactory - the factory to use when the executor creates a new thread
        //rejectionHandler - the handler to use when execution is blocked because the thread bounds and queue capacities are reached
        ThreadPoolExecutor threadPoolExecutorOne = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory(), discardOldestPolicy);
        ThreadPoolExecutor threadPoolExecutorTwo = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), rejectedExecutionHandler);
        ThreadPoolExecutor threadPoolExecutorThree = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory());
        ThreadPoolExecutor threadPoolExecutorFour = new ThreadPoolExecutor(2, 10 , 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));

    }

}
