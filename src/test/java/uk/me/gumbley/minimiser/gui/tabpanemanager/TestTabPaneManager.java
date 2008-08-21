package uk.me.gumbley.minimiser.gui.tabpanemanager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the TabPaneManager
 * 
 * @author matt
 *
 */
public final class TestTabPaneManager extends LoggingTestCase {
    
    private TabPaneManager tabPaneManager;

    /**
     * Marsupial! Have you got what's there for my enjoyment?
     */
    @Before
    public void getPrerequisites() {
        tabPaneManager = new HeadlessTabPaneManager();
    }
    
    /**
     * 
     */
    @Test
    public void emptiness() {
        //Assert.assertEquals(0, tabPaneManager)
        Assert.assertTrue(true); // nop!
    }
}
