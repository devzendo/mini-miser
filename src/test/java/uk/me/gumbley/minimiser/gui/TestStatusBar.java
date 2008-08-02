package uk.me.gumbley.minimiser.gui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test the status bar.
 * @author matt
 *
 */
public class TestStatusBar {
    
    private HeadlessStatusBar headlessStatusBar;

    /**
     * 
     */
    @Before
    public void getRequisites() {
        headlessStatusBar = new HeadlessStatusBar();
    }
    
    /**
     * 
     */
    @Test
    public void emptiness() {
        Assert.assertEquals("", headlessStatusBar.getInternalMessage());
    }
}
