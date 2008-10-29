package uk.me.gumbley.minimiser.gui.menu;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTabbedPane;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;

/**
 * Tests the linkage between the menu items enabling/disabling and
 * various system events.
 * 
 * @author matt
 *
 */
public final class TestMenuMediator extends MenuMediatorUnittestCase {

    /**
     * 
     */
    @Test
    public void testCloseDisabledWithNothingOpen() {
        startMediator();

        Assert.assertFalse(getStubMenu().isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseEnabledWithOneOpen() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(getStubMenu().isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void recentListGetsAddedWhenOpenListIsAddedTo() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        
        getRecentFilesList().add(databaseDescriptor);

        startMediator();

        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        
        Assert.assertTrue(Arrays.asList(getRecentFilesList().getRecentDatabases()).contains(databaseDescriptor));
    }
    
    /**
     */
    @Test
    public void recentMenuGetsRebuiltWhenOpenListIsAddedTo() {
        Assert.assertEquals(0, getStubMenu().getRecentDatabases().length);
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertFalse(getStubMenu().isRecentListBuilt());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(getStubMenu().isRecentListBuilt());
        Assert.assertEquals(1, getStubMenu().getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptor, getStubMenu().getRecentDatabases()[0]);
    }
    
    /**
     * 
     */
    @Test
    public void openRecentWhenAlreadyOpenedShouldSwitch() {
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorOne);

        final AtomicBoolean switchedToOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseSwitchedEvent) {
                    final DatabaseSwitchedEvent dse = (DatabaseSwitchedEvent) observableEvent;
                    if (dse.getDatabaseName().equals("one")) {
                        switchedToOne.set(true);
                    }
                }
            }
        });

        final DatabaseDescriptor databaseDescriptorTwo = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorTwo);

        Assert.assertTrue(getStubMenu().isRecentListBuilt());
        Assert.assertEquals(2, getStubMenu().getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptorTwo, getStubMenu().getRecentDatabases()[0]);

        Assert.assertFalse(switchedToOne.get());
    
        getStubMenu().injectOpenRecentRequest("one", "/tmp/one");

        Assert.assertTrue("did not switch to already-opened on recent open of already-opened db", switchedToOne.get());
    }
    
    /**
     * 
     */
    @Test
    public void openRecentWhenNotCurrentlyOpenedShouldOpen() {
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        ((StubRecentFilesList) getRecentFilesList()).addDatabaseSilently(databaseDescriptorOne);

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) observableEvent;
                    if (doe.getDatabaseName().equals("one")) {
                        openedOne.set(true);
                    }
                }
            }
        });

        Assert.assertFalse(openedOne.get());
    
        getStubMenu().injectOpenRecentRequest("one", "/tmp/one");
        Assert.assertTrue("did not open not-opened on recent open of not-opened db", openedOne.get());
    }
    
    /**
     * This isn't a full test, it only checks the opentablist for tab addition
     * - it could check that tabs are actually added to the JTabbedPane
     */
    @Test
    public void viewMenuAddsAndRemovesTabs() {
        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        final Object lock = new Object();
        synchronized (lock) {
            final JTabbedPane tabbedPane = new JTabbedPane(); // EDT be damned!
            databaseDescriptorOne.setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        }
        
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorOne);
        getOpenTabList().addDatabase(databaseDescriptorOne);

        startMediator();
        
        final List<TabDescriptor> tabsForDatabase = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(0, tabsForDatabase.size());
        

        // open the tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, TabIdentifier.SQL, true);

        final List<TabDescriptor> tabsForDatabaseWithSql = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabaseWithSql);
        Assert.assertEquals(1, tabsForDatabaseWithSql.size());
        final TabDescriptor sql = tabsForDatabaseWithSql.get(0);
        Assert.assertEquals(TabIdentifier.SQL, sql.getTabIdentifier());
        
        // close the tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, TabIdentifier.SQL, false);

        final List<TabDescriptor> tabsForDatabaseWithNoTabs = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabaseWithNoTabs);
        Assert.assertEquals(0, tabsForDatabaseWithNoTabs.size());
    }

    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertEquals("one", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(getStubMenu().isCloseEnabled());
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastOfMultipleClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        Assert.assertEquals("one", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor1);
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
        Assert.assertTrue(getStubMenu().isCloseEnabled());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor2);
        Assert.assertFalse(getStubMenu().isCloseEnabled());
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
    }
    
    /**
     * 
     */
    @Test
    public void testEmptySwitchList() {
        startMediator();

        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 0);
        Assert.assertNull(getStubMenu().getCurrentDatabase());
    }
    
    /**
     * 
     */
    @Test
    public void testPopulatedSwitchList() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 1);
        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());
    }
    
    /**
     * 
     */
    @Test
    public void testSwitchOnOpenNew() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 2);
        Assert.assertEquals("two", getStubMenu().getCurrentDatabase());
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
    }

    /**
     * 
     */
    @Test
    public void testSwitchBackOnClose() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor2);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 1);
        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());
    }

    /**
     * 
     */
    @Test
    public void windowMenuSwitchesDatabase() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);

        Assert.assertEquals("two", getStubMenu().getCurrentDatabase());
        
        getStubMenu().injectWindowMenuRequest(databaseDescriptor1);
        
        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());
        
    }
    /**
     * 
     */
    @Test
    public void testNoCurrentWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor1);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 0);
        Assert.assertNull(getStubMenu().getCurrentDatabase());
        Assert.assertFalse(getStubMenu().isCloseEnabled());
    }

    /**
     * 
     */
    @Test
    public void openerOpeningAddsToDatabaseList() {
        startMediator();

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) observableEvent;
                    if (doe.getDatabaseName().equals("one")) {
                        openedOne.set(true);
                    }
                }
            }
        });

        getStubOpener().openDatabase("one", "/tmp/one", null);
        Assert.assertTrue(openedOne.get());
    }
    
    /**
     * @throws IOException on fail
     * 
     */
    @Test
    public void hiddenTabsChangedInPrefsRebuildsViewMenu() throws IOException {
        startMediator();
        
        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());
        
        getPrefs().setTabHidden("SQL");
        
        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
        
        getStubMenu().resetViewMenuBuiltFlag();
        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());
        
        getPrefs().clearTabHidden("SQL");
        
        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }
    
    /**
     * This is testing the stub menu really - switching causes the menu impls
     * to rebuild the view menu.
     */
    @Test
    public void databaseSwitchRebuildsViewMenu() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);

        getStubMenu().resetViewMenuBuiltFlag();
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());
        
        getStubMenu().injectWindowMenuRequest(databaseDescriptor1);
        
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }
}
