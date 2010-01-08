package uk.me.gumbley.minimiser.wiring.lifecycle;

import javax.swing.JFrame;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.MainFrameFactory;
import uk.me.gumbley.minimiser.gui.menu.MenuMediatorUnittestCase;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/menu/TestMenuInitialisingLifecycle.xml")
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
