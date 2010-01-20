package org.devzendo.minimiser.wiring.lifecycle;

import javax.swing.JFrame;

import org.devzendo.minimiser.gui.MainFrameFactory;
import org.devzendo.minimiser.gui.menu.MenuMediatorUnittestCase;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.junit.Assert;
import org.junit.Test;



/**
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/menu/TestMenuInitialisingLifecycle.xml")
public final class TestMenuInitialisingLifecycle extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void menuIsInitialisedAfterConstruction() {
        final MainFrameFactory mainFrameFactory = getSpringLoader().getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(new JFrame());

        // Start lifecycle (manually)
        final MenuInitialisingLifecycle lifecycle = new MenuInitialisingLifecycle(getSpringLoader());
        lifecycle.startup();

        // Menu should have been initialised by the lifecycle
        Assert.assertTrue(getStubMenu().isInitialised());
    }
}
