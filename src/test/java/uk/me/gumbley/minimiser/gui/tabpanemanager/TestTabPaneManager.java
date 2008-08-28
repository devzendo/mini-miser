package uk.me.gumbley.minimiser.gui.tabpanemanager;

import javax.swing.JTabbedPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * Tests the TabPaneManager
 * 
 * @author matt
 *
 */
public final class TestTabPaneManager extends LoggingTestCase {
    
    private HeadlessTabPaneManager tabPaneManager;

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
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
    }
    
    /**
     * 
     */
    @Test
    public void addNewTabPane() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        tabPaneManager.addTabPane(dd);
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        Assert.assertSame(tabbedPane, tabPaneManager.getTabPane("one"));
        Assert.assertSame(tabbedPane, tabPaneManager.getLastAddedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void addExistingTabPaneOverwrites() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        tabPaneManager.addTabPane(dd);
        
        final JTabbedPane tabbedPane2 = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane2);
        tabPaneManager.addTabPane(dd);
        
        Assert.assertNotSame(tabbedPane, tabbedPane2);
        
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        Assert.assertSame(tabbedPane2, tabPaneManager.getTabPane("one"));
        Assert.assertSame(tabbedPane2, tabPaneManager.getLastAddedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void addNullDatabaseDescriptorWithNoTabbedPaneDoesntAdd() {
        tabPaneManager.addTabPane(null);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastAddedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void addDatabaseDescriptorWithNoTabbedPaneDoesntAdd() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        tabPaneManager.addTabPane(dd);
        
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
        tabPaneManager.addTabPane(dd);
        
        final DatabaseDescriptor dd2 = new DatabaseDescriptor("one"); // same name
        tabPaneManager.addTabPane(dd2);
        
        Assert.assertEquals(1, tabPaneManager.getNumberOfTabPanes());
        final JTabbedPane actualTabbedPane = tabPaneManager.getTabPane("one");
        Assert.assertSame(tabbedPane, actualTabbedPane);
        Assert.assertEquals(tabbedPane, tabPaneManager.getLastAddedTabbedPane());
    }

    /**
     * 
     */
    @Test
    public void getNonExistantNamedDatabaseReturnsNull() {
        Assert.assertNull(tabPaneManager.getTabPane("wah"));
    }
    
    /**
     * 
     */
    @Test
    public void removeNullRemovesNothing() {
        tabPaneManager.removeTabPane(null);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastRemovedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void removeNoTabRemovesNothing() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        tabPaneManager.removeTabPane(dd);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastRemovedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void removeNonStoredRemovesNothing() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);

        tabPaneManager.removeTabPane(dd);
        
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getLastRemovedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void removeActuallyRemoves() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane = new JTabbedPane();
        dd.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        tabPaneManager.addTabPane(dd);
        
        tabPaneManager.removeTabPane(dd);
        Assert.assertSame(tabbedPane, tabPaneManager.getLastRemovedTabbedPane());
        Assert.assertEquals(0, tabPaneManager.getNumberOfTabPanes());
        Assert.assertNull(tabPaneManager.getTabPane("one"));
    }
    
    /**
     * 
     */
    @Test
    public void switchNullSwitchesNothing() {
        tabPaneManager.switchToTabPane(null);
        Assert.assertNull(tabPaneManager.getLastSwitchedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void switchActuallySwitches() {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        dd1.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane1);
        tabPaneManager.addTabPane(dd1);
        
        Assert.assertNull(tabPaneManager.getLastSwitchedTabbedPane());
        
        tabPaneManager.switchToTabPane(dd1);
        
        Assert.assertSame(tabbedPane1, tabPaneManager.getLastSwitchedTabbedPane());
        
        final DatabaseDescriptor dd2 = new DatabaseDescriptor("one");
        final JTabbedPane tabbedPane2 = new JTabbedPane();
        dd2.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane2);
        tabPaneManager.addTabPane(dd2);
        
        Assert.assertSame(tabbedPane1, tabPaneManager.getLastSwitchedTabbedPane());
        
        tabPaneManager.switchToTabPane(dd2);
        
        Assert.assertSame(tabbedPane2, tabPaneManager.getLastSwitchedTabbedPane());
    }
    
    /**
     * 
     */
    @Test
    public void hideHides() {
        Assert.assertFalse(tabPaneManager.isCleared());
        
        tabPaneManager.hideTabPanes();
        
        Assert.assertTrue(tabPaneManager.isCleared());
    }
}
