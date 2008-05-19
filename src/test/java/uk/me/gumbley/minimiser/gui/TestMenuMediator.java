package uk.me.gumbley.minimiser.gui;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the linkage between the menu items enabling/disabling and
 * various system events.
 * 
 * @author matt
 *
 */
public class TestMenuMediator {
    private MenuMediator menuMediator;

    @Before
    public void getMediator() {
        menuMediator = new MenuMediatorImpl();
    }
    
    @Test
    public void testCloseDisabledWithNothingOpen() {
        // WOZERE
    }
}
