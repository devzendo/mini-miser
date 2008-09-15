package uk.me.gumbley.minimiser.wiring.tabeventlistener;

import java.awt.Label;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.StubTab;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.opentablist.TabOpenedEvent;


/**
 * Tests the TabEventListener that gets a TabOpenedEvent and adds it into the
 * JTabbedPane for the database.
 * 
 * @author matt
 *
 */
public final class TestTabDisplayingTabEventListener extends LoggingTestCase {
    
    private TabDisplayingTabEventListener tabDisplayingTabEventListener;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        tabDisplayingTabEventListener = new TabDisplayingTabEventListener();
    }
    
    /**
     * 
     */
    @Test
    public void addTabToTabbedPane() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final Label label = new Label("the label");
        final Tab tab = new StubTab(label);
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        tabDisplayingTabEventListener.eventOccurred(toe);

        // WOZERE - need to test the thing that the adapter adapts to
    }
}
