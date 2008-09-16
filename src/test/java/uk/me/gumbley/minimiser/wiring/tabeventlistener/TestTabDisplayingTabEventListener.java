package uk.me.gumbley.minimiser.wiring.tabeventlistener;

import java.awt.Label;
import javax.swing.JTabbedPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
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
    private OpenTabList openTabList;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        openTabList = new OpenTabList();
        tabDisplayingTabEventListener = new TabDisplayingTabEventListener(openTabList);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddWithNullDatabaseDescriptor() {
        final Label label = new Label("the label");
        final Tab tab = new StubTab(label);
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        
        final TabOpenedEvent toe = new TabOpenedEvent(null, td);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }


    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddWithNullTabDescriptor() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);

        final TabOpenedEvent toe = new TabOpenedEvent(dd, null);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddTabToDatabaseThatIsntAddedToOpenTabList() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        // not added to openTabList
        
        final Label label = new Label("the label");
        final Tab tab = new StubTab(label);
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddTabWithNoTab() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        openTabList.addDatabase(dd);
        
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, null);
        
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }  
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddTabWithNoTabImpl() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        openTabList.addDatabase(dd);
        
        final Tab tab = new StubTab(null);
        // no tabimpl
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddWithNullTabbedPane() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        // no TabbedPane attribute

        final Label label = new Label("the label");
        final Tab tab = new StubTab(label);
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        tabDisplayingTabEventListener.eventOccurred(toe);
    }
    
    /**
     * 
     */
    @Test
    public void addTabToTabbedPane() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("db");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        openTabList.addDatabase(dd);
        
        final Label label = new Label("the label");
        final Tab tab = new StubTab(label);
        final TabDescriptor td = new TabDescriptor(TabIdentifier.OVERVIEW, tab);
        
        final TabOpenedEvent toe = new TabOpenedEvent(dd, td);

        Assert.assertEquals(0, tabbedPane.getTabCount());
        
        tabDisplayingTabEventListener.eventOccurred(toe);

        Assert.assertEquals(1, tabbedPane.getTabCount());
        Assert.assertEquals(TabIdentifier.OVERVIEW.getDisplayableName(), tabbedPane.getTitleAt(0));
    }
}
