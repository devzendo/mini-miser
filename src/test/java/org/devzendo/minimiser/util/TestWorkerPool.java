package org.devzendo.minimiser.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests the WorkerPool
 * 
 * @author matt
 *
 */
public final class TestWorkerPool {
    private WorkerPool workerPool;

    /**
     * 
     */
    @Before
    public void getWorkerPool() {
        workerPool = new WorkerPool();
    }
    
    /**
     * 
     */
    @Test(timeout = 2000)
    public void testRunnableGetsExecuted() {
        final AtomicBoolean done = new AtomicBoolean(false);
        final Runnable r = new Runnable() {
            public void run() {
                done.set(true);
            }
        };
        Assert.assertFalse(done.get());
        workerPool.submit(r);
        ThreadUtils.waitNoInterruption(500);
        Assert.assertTrue(done.get());
    }
    
    /**
     * @throws InterruptedException on failure
     * @throws ExecutionException on failure
     */
    @Test(timeout = 2000)
    public void testCallableReturnsResultInFuture() throws InterruptedException, ExecutionException {
        final Callable<Boolean> c = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                ThreadUtils.waitNoInterruption(1000);
                return Boolean.TRUE;
            }
        };
        final Future<Boolean> future = workerPool.submit(c);
        ThreadUtils.waitNoInterruption(250);
        Assert.assertFalse(future.isDone());
        ThreadUtils.waitNoInterruption(1500);
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.get());
    }
}
