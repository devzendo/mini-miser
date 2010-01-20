package org.devzendo.minimiser.wiring.lifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.startupqueue.StartupQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the StartupQueue that's controlled as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/StartupQueueLifecycleTestCase.xml")
public final class TestStartupQueueLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private StartupQueue startupQueue;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);

        startupQueue = getSpringLoader().getBean("startupQueue", StartupQueue.class);
        Assert.assertNotNull(startupQueue);
    }

    /**
     *
     */
    @Test(timeout = 5000)
    public void taskExecutedOnStartup() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        final Runnable task = new Runnable() {
            public void run() {
                executed.set(true);
            }
        };
        startupQueue.addRunnable(task);

        Assert.assertFalse(executed.get());

        lifecycleManager.startup();

        startupQueue.join();

        Assert.assertTrue(executed.get());
    }
}
