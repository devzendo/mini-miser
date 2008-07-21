package uk.me.gumbley.minimiser.gui.menu;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.recentlist.RecentListEvent;

/**
 * Tests the linkage between the menu items enabling/disabling and
 * various system events.
 * 
 * @author matt
 *
 */
public final class TestMenuMediator extends LoggingTestCase {

    private StubMenu stubMenu;
    private OpenDatabaseList openDatabaseList;
    private RecentFilesList recentFilesList;

    /**
     * Get all necessaries
     */
    @Before
    public void getMediatorPrerequisites() {
        stubMenu = new StubMenu();
        openDatabaseList = new OpenDatabaseList();
        recentFilesList = EasyMock.createStrictMock(RecentFilesList.class);
        recentFilesList.addRecentListEventObserver(EasyMock.isA(MenuMediatorImpl.RecentListEventObserver.class));
    }

    private void startMediator() {
        new MenuMediatorImpl(stubMenu, openDatabaseList, recentFilesList);
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWithNothingOpen() {
        startMediator();

        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseEnabledWithOneOpen() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void recentMenuGetsWiredUp() {
        // the expectation is set in the Before section
        EasyMock.replay(recentFilesList);

        startMediator();

        EasyMock.verify(recentFilesList);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void recentListGetsAddedWhenOpenListIsAddedTo() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        
        recentFilesList.add(databaseDescriptor);
        EasyMock.replay(recentFilesList);

        startMediator();

        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        
        EasyMock.verify(recentFilesList);
    }
    
    /**
     * 
     */
    @Test
    @Ignore
    public void recentMenuGetsRebuiltWhenOpenListIsAddedTo() {
        // WOZERE simulate event propagation of the recent list
        // perhaps with a stub recent files list that doesn't use prefs
        recentFilesList = new StubRecentFilesList();
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertFalse(stubMenu.isRecentListBuilt());
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(stubMenu.isRecentListBuilt());
    }

    private final class StubRecentFilesList implements RecentFilesList {
        public void add(DatabaseDescriptor databaseDescriptor) {
            // TODO Auto-generated method stub
        }

        public void addRecentListEventObserver(
                Observer<RecentListEvent> observer) {
            // TODO Auto-generated method stub
        }

        public int getCapacity() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getNumberOfEntries() {
            // TODO Auto-generated method stub
            return 0;
        }

        public DatabaseDescriptor[] getRecentFiles() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        openDatabaseList.removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastOfMultipleClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.addOpenedDatabase(databaseDescriptor2);
        openDatabaseList.removeClosedDatabase(databaseDescriptor1);
        Assert.assertTrue(stubMenu.isCloseEnabled());
        openDatabaseList.removeClosedDatabase(databaseDescriptor2);
        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testEmptySwitchList() {
        startMediator();

        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 0);
        Assert.assertNull(stubMenu.getCurrentDatabase());
    }
    
    /**
     * 
     */
    @Test
    public void testPopulatedSwitchList() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 1);
        Assert.assertEquals("one", stubMenu.getCurrentDatabase());
    }
    
    /**
     * 
     */
    @Test
    public void testSwitchOnOpenNew() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.addOpenedDatabase(databaseDescriptor2);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 2);
        Assert.assertEquals("two", stubMenu.getCurrentDatabase());
    }

    /**
     * 
     */
    @Test
    public void testSwitchBackOnClose() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.addOpenedDatabase(databaseDescriptor2);
        openDatabaseList.removeClosedDatabase(databaseDescriptor2);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 1);
        Assert.assertEquals("one", stubMenu.getCurrentDatabase());
    }

    /**
     * 
     */
    @Test
    public void testNoCurrentWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.removeClosedDatabase(databaseDescriptor1);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 0);
        Assert.assertNull(stubMenu.getCurrentDatabase());
        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
}
