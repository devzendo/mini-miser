package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import javax.swing.JTabbedPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tabpanemanager.HeadlessTabPaneManager;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;

/**
 * Tests the adapter that adapts between DatabaseEvents and the TabPaneManager
 * that's managed as a Lifecycle.
 * 
 * These tests are almost identical to those in TestTabPaneManager, but they
 * test the correct behaviour of the adapter by sensing the TabPaneManager
 * when the adapter has reacted to database events triggered by operations
 * on the OpenDatabaseList.
 * 
 * @author matt
 *
 */
public final class TestTabPaneManagerDatabaseEventListener extends LoggingTestCase {

    private OpenDatabaseList openDatabaseList;
    private HeadlessTabPaneManager tabPaneManager;
    private TabPaneManagerDatabaseEventListener adapter;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        openDatabaseList = new OpenDatabaseList();
        tabPaneManager = new HeadlessTabPaneManager();
        adapter = new TabPaneManagerDatabaseEventListener(tabPaneManager);
        openDatabaseList.addDatabaseEventObserver(adapter);
    }
    
    /**
     * 
     */
    @Test
    public void openDatabaseAddsTabPane() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        openDatabaseList.addOpenedDatabase(dd);
        
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        Assert.assertSame(tabbedPane, tabPaneManager.getTabPane("one"));
        Assert.assertSame(tabbedPane, tabPaneManager.getLastAddedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void addNullDatabaseDescriptorDoesntAdd() {
        openDatabaseList.addOpenedDatabase(null);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastAddedTabbedPane());
    }

    /**
     * 
     */
    @Test
    public void addDatabaseDescriptorWithNoTabbedPaneDoesntAdd() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(dd);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastAddedTabbedPane());
    }

    /**
     * 
     */
    @Test
    public void addExistingDatabaseDescriptorWithNullTabbedPaneDoesntOverwrite() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        openDatabaseList.addOpenedDatabase(dd);
        
        final DatabaseDescriptor dd2 = new DatabaseDescriptor("one"); // same name
        openDatabaseList.addOpenedDatabase(dd2);
        
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        final JTabbedPane actualTabbedPane = tabPaneManager.getTabPane("one");
        Assert.assertSame(tabbedPane, actualTabbedPane);
        Assert.assertEquals(tabbedPane, tabPaneManager.getLastAddedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void removeNullRemovesNothing() {
        openDatabaseList.removeClosedDatabase(null);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastRemovedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void removePenultimateActuallyRemovesButDoesntClear() {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        dd1.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane1);
        openDatabaseList.addOpenedDatabase(dd1);

        final DatabaseDescriptor dd2 = new DatabaseDescriptor("two");
        final JTabbedPane tabbedPane2 = new JTabbedPane();
        dd2.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane2);
        openDatabaseList.addOpenedDatabase(dd2);
        
        openDatabaseList.removeClosedDatabase(dd1);
        Assert.assertSame(tabbedPane1, tabPaneManager.getLastRemovedTabbedPane());
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getTabPane("one"));
        Assert.assertSame(tabbedPane2, tabPaneManager.getTabPane("two"));
        Assert.assertFalse(tabPaneManager.isCleared());
    }
    
    /**
     * 
     */
    @Test
    public void removeLastActuallyRemovesAndClears() {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        dd1.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane1);
        openDatabaseList.addOpenedDatabase(dd1);

        openDatabaseList.removeClosedDatabase(dd1);
        Assert.assertSame(tabbedPane1, tabPaneManager.getLastRemovedTabbedPane());
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getTabPane("one"));
        Assert.assertTrue(tabPaneManager.isCleared());
    }
}
