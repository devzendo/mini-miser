package uk.me.gumbley.minimiser.wiring.tabeventlistener;

import org.junit.Before;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the TabEventListener that gets a TabOpenedEvent and adds it into the
 * JTabbedPane for the database.
 * 
 * @author matt
 *
 */
public class TestTabDisplayingTabEventListener extends LoggingTestCase {
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        new TabDisplayingTabEventListener();
        // WOZERE - not passing the database descriptor around with the tab event will make
        // it impossile to attach the tabs correctly
    }
}
