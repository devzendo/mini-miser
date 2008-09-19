 package uk.me.gumbley.minimiser.gui.menu;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.MainFrameTitle;
import uk.me.gumbley.minimiser.gui.StubMainFrameTitle;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;

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
    private StubOpener stubOpener;
    private StubOpenerAdapterFactory stubOpenerAdapterFactory;
    private MainFrameTitle mainFrameTitle;
    private Prefs prefs;
    private File prefsFile;

    /**
     * Get all necessaries
     * @throws IOException on prefs file creation failure
     */
    @SuppressWarnings("unchecked")
    @Before
    public void getMediatorPrerequisites() throws IOException {
        stubMenu = new StubMenu();
        openDatabaseList = new OpenDatabaseList();
        recentFilesList = EasyMock.createStrictMock(RecentFilesList.class);
        recentFilesList.addRecentListEventObserver(EasyMock.isA(Observer.class));
        stubOpener = new StubOpener();
        stubOpenerAdapterFactory = new StubOpenerAdapterFactory();
        mainFrameTitle = new StubMainFrameTitle();
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
    }

    private void startMediator() {
        new MenuMediatorImpl(stubMenu, openDatabaseList, recentFilesList, stubOpener, 
            stubOpenerAdapterFactory, mainFrameTitle, prefs);
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
     * I'm using a stub recent list as coding the event notification in EasyMock
     * was becoming painful. This test overrides the recent list created in
     * the before section. 
     */
    @Test
    public void recentMenuGetsRebuiltWhenOpenListIsAddedTo() {
        recentFilesList = new StubRecentFilesList();
        Assert.assertEquals(0, stubMenu.getRecentDatabases().length);
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertFalse(stubMenu.isRecentListBuilt());
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(stubMenu.isRecentListBuilt());
        Assert.assertEquals(1, stubMenu.getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptor, stubMenu.getRecentDatabases()[0]);
    }
    
    /**
     * I'm using a stub recent list as coding the event notification in EasyMock
     * was becoming painful. This test overrides the recent list created in
     * the before section. 
     */
    @Test
    public void openRecentWhenAlreadyOpenedShouldSwitch() {
        recentFilesList = new StubRecentFilesList();
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(databaseDescriptorOne);

        final AtomicBoolean switchedToOne = new AtomicBoolean(false);
        openDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {
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
        openDatabaseList.addOpenedDatabase(databaseDescriptorTwo);

        Assert.assertTrue(stubMenu.isRecentListBuilt());
        Assert.assertEquals(2, stubMenu.getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptorTwo, stubMenu.getRecentDatabases()[0]);

        Assert.assertFalse(switchedToOne.get());
    
        stubMenu.injectOpenRecentRequest("one", "/tmp/one");

        Assert.assertTrue("did not switch to already-opened on recent open of already-opened db", switchedToOne.get());
    }
    

    /**
     * I'm using a stub recent list as coding the event notification in EasyMock
     * was becoming painful. This test overrides the recent list created in
     * the before section. 
     */
    @Test
    public void openRecentWhenNotCurrentlyOpenedShouldOpen() {
        final StubRecentFilesList stubRecentFilesList = new StubRecentFilesList(); 
        recentFilesList = stubRecentFilesList;
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        stubRecentFilesList.addDatabaseSilently(databaseDescriptorOne);

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        openDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {
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
    
        stubMenu.injectOpenRecentRequest("one", "/tmp/one");
        Assert.assertTrue("did not open not-opened on recent open of not-opened db", openedOne.get());
    }

    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertNull(mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        Assert.assertEquals("one", mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(stubMenu.isCloseEnabled());
        Assert.assertNull(mainFrameTitle.getCurrentDatabaseName());
    }
    
    /**
     * 
     */
    @Test
    public void testCloseDisabledWhenLastOfMultipleClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        Assert.assertNull(mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        Assert.assertEquals("one", mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.addOpenedDatabase(databaseDescriptor2);
        Assert.assertEquals("two", mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.removeClosedDatabase(databaseDescriptor1);
        Assert.assertEquals("two", mainFrameTitle.getCurrentDatabaseName());
        Assert.assertTrue(stubMenu.isCloseEnabled());
        openDatabaseList.removeClosedDatabase(databaseDescriptor2);
        Assert.assertFalse(stubMenu.isCloseEnabled());
        Assert.assertNull(mainFrameTitle.getCurrentDatabaseName());
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
        Assert.assertNull(mainFrameTitle.getCurrentDatabaseName());
        openDatabaseList.addOpenedDatabase(databaseDescriptor1);
        openDatabaseList.addOpenedDatabase(databaseDescriptor2);
        Assert.assertTrue(stubMenu.getNumberOfDatabases() == 2);
        Assert.assertEquals("two", stubMenu.getCurrentDatabase());
        Assert.assertEquals("two", mainFrameTitle.getCurrentDatabaseName());
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

    /**
     * 
     */
    @Test
    public void openerOpeningAddsToDatabaseList() {
        startMediator();

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        openDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) observableEvent;
                    if (doe.getDatabaseName().equals("one")) {
                        openedOne.set(true);
                    }
                }
            }
        });

        stubOpener.openDatabase("one", "/tmp/one", null);
        Assert.assertTrue(openedOne.get());
    }
    
    /**
     * @throws IOException on fail
     * 
     */
    @Test
    public void hiddenTabsChangedInPrefsRebuildsViewMenu() throws IOException {
        startMediator();
        
        Assert.assertFalse(stubMenu.isTabHidden("SQL"));
        Assert.assertFalse(stubMenu.isViewMenuBuilt());
        
        prefs.setTabHidden("SQL");
        
        Assert.assertTrue(stubMenu.isTabHidden("SQL"));
        Assert.assertTrue(stubMenu.isViewMenuBuilt());
        
        stubMenu.resetViewMenuBuiltFlag();
        Assert.assertTrue(stubMenu.isTabHidden("SQL"));
        Assert.assertFalse(stubMenu.isViewMenuBuilt());
        
        prefs.clearTabHidden("SQL");
        
        Assert.assertFalse(stubMenu.isTabHidden("SQL"));
        Assert.assertTrue(stubMenu.isViewMenuBuilt());
    }
}
