package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.menu.MenuMediatorUnittestCase;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;


/**
 * Tests the lifecycle that initialises the Help|Check for updates
 * Menu Item
 * @author matt
 *
 */
public final class TestHelpCheckForUpdatesMenuInitialiserLifecycle extends MenuMediatorUnittestCase {

    /**
     * 
     */
    @Test
    public void isMenuDisabledWhenUpdatesDisabled() {
        Assert.assertFalse(getPrefs().isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        // Start lifecycle (manually)
        final HelpCheckForUpdatesMenuInitialiserLifecycle lifecycle = new HelpCheckForUpdatesMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());
    }

    /**
     * 
     */
    @Test
    public void isMenuEnabledWhenUpdatesEnabled() {
        getPrefs().setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);

        Assert.assertTrue(getPrefs().isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        // Start lifecycle (manually)
        final HelpCheckForUpdatesMenuInitialiserLifecycle lifecycle = new HelpCheckForUpdatesMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // now it should have changed
        Assert.assertTrue(getStubMenu().isHelpCheckForUpdatesEnabled());
    }
}
