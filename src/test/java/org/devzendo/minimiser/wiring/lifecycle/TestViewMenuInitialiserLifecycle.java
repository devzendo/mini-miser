package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.menu.MenuMediatorUnittestCase;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests the lifecycle that initialises the View Menu
 * @author matt
 *
 */
public final class TestViewMenuInitialiserLifecycle extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void initialiseViewMenuCorrectly() {
        getPrefs().setTabHidden("SQL");
        getPrefs().clearTabHidden("Categories");

        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        // Start lifecycle (manually)
        final ViewMenuInitialiserLifecycle lifecycle = new ViewMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // now it should have changed
        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }
}
