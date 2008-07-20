package uk.me.gumbley.minimiser.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Manages a pool of worker threads.
 * 
 * @author matt
 *
 */
public final class WorkerPool {
    private ExecutorService executorService;

    /**
     * Construct a WorkerPool
     */
    public WorkerPool() {
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Submit a task for execution
     * @param task the Runnable to execute.
     */
    public void submit(final Runnable task) {
        executorService.submit(task);
    }
    
    /**
     * Submit a task for execution, obtaining a Future to obtain its completion.
     * @param <T> the type that this Callable will return upon completion
     * @param task the task
     * @return a Future<T> that can be polled, or .get().
     */
    public <T> Future<T> submit(final Callable<T> task) {
        return executorService.submit(task);
    }
}
