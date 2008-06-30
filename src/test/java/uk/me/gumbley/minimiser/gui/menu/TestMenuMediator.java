package uk.me.gumbley.minimiser.gui.menu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;

/**
 * Tests the linkage between the menu items enabling/disabling and
 * various system events.
 * 
 * @author matt
 *
 */
/**
 * @author matt
 *
 */
public final class TestMenuMediator extends LoggingTestCase {
    private StubMenu stubMenu;
    private OpenDatabaseList openDatabaseList;

    /**
     * Get all necessaries
     */
    @Before
    public void getMediator() {
        stubMenu = new StubMenu();
        openDatabaseList = new OpenDatabaseList();
        new MenuMediatorImpl(stubMenu, openDatabaseList);
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWithNothingOpen() {
        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseEnabledWithOneOpen() {
        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one"));
        Assert.assertTrue(stubMenu.isCloseEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastClosed() {
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
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 0);
        Assert.assertNull(stubMenu.getCurrentDatabase());
    }
    
    /**
     * 
     */
    @Test
    public void testPopulatedSwitchList() {
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
        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.removeClosedDatabase(databaseDescriptor1);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 0);
        Assert.assertNull(stubMenu.getCurrentDatabase());
        Assert.assertFalse(stubMenu.isCloseEnabled());
    }
}
