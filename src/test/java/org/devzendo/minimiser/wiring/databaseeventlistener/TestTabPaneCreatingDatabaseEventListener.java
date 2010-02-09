package org.devzendo.minimiser.wiring.databaseeventlistener;

import java.awt.Component;
import java.awt.Label;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commoncode.gui.GUIValueObtainer;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tabfactory.StubRecordingTab;
import org.devzendo.minimiser.gui.tabfactory.StubTabFactory;
import org.devzendo.minimiser.gui.tabfactory.TabFactory;
import org.devzendo.minimiser.gui.tabpanemanager.TabListPrefs;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.prefs.Prefs;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the adapter that adapts between DatabaseEvents and the
 * creation and population of the database descriptor's
 * TabbedPane attribute.
 * <p>
 * Tests that tabs defined for the various
 * TabIdentifiers are loaded and attached if they are permanent
 * or stored in prefs as an open tab.
 * <p>
 * Tests that they are disposed of
 * and removed from the open tab list on close.
 * <p>
 * Tests that the previously-active tab is made active on open.
 * <p>
 * Tests that the open tabs are persisted on database close.
 * <p>
 * Tests that the active tab is persisted on database close.
 *
 * @author matt
 *
 */
public final class TestTabPaneCreatingDatabaseEventListener extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestTabPaneCreatingDatabaseEventListener.class);
    private static final String DATABASE = "db";
    private OpenDatabaseList openDatabaseList;
    private OpenTabList openTabList;
    private TabFactory tabFactory;
    private TabPaneCreatingDatabaseEventListener adapter;

    private volatile int tabCount = 0;
    private volatile boolean component0IsLabel = false;
    private volatile boolean component1IsLabel = false;
    private volatile boolean tab0NameOk = false;
    private volatile boolean tab1NameOk = false;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        openDatabaseList = new OpenDatabaseList();
        openTabList = new OpenTabList();
        tabFactory = new StubTabFactory(openTabList);
    }

    /**
     * @throws Exception on gui value obtainer failure
     *
     */
    @Test
    public void openingDatabaseCausesTabsStoredInPrefsAndPermanentTabsToBeAddedToTheOpenTabList() throws Exception {
        // Add SQL into prefs - OVERVIEW is permanent, so we should see [SQL, OVERVIEW]
        // Just test that OVERVIEW is still permanent first
        Assert.assertTrue(SystemTabIdentifiers.OVERVIEW.isTabPermanent());

        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getOpenTabs(DATABASE)).andReturn(new String[] {"SQL"});
        EasyMock.expect(prefs.getActiveTab(DATABASE)).andReturn("SQL");

        EasyMock.replay(prefs);

        final TabListPrefs tabListPrefs = new TabListPrefs(prefs);

        adapter = new TabPaneCreatingDatabaseEventListener(tabListPrefs, tabFactory, openTabList);
        openDatabaseList.addDatabaseEventObserver(adapter);

        final List<TabDescriptor> emptyTabDescriptors = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(emptyTabDescriptors); // db not added yet
        Assert.assertEquals(0, emptyTabDescriptors.size());

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        openDatabaseList.addOpenedDatabase(databaseDescriptor);

        final JTabbedPane tabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
        // The JTabbedPane should now have been added to the DD
        Assert.assertNotNull(tabbedPane);

        final Boolean sqlIsActiveTab = new GUIValueObtainer<Boolean>().obtainFromEventThread(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                final Label selectedComponent = (Label) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                return selectedComponent.getText().equals(SystemTabIdentifiers.SQL.getDisplayableName());
            }
        });
        Assert.assertTrue(sqlIsActiveTab);

        // The OVERVIEW and SQL tabs should have been added to the openTabList
        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(2, tabsForDatabase.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, tabsForDatabase.get(0).getTabIdentifier());
        Assert.assertTrue(tabsForDatabase.get(0).getTab() instanceof StubRecordingTab);
        Assert.assertEquals(SystemTabIdentifiers.SQL, tabsForDatabase.get(1).getTabIdentifier());
        Assert.assertTrue(tabsForDatabase.get(1).getTab() instanceof StubRecordingTab);

        // And the StubRecordingTab's component should have been added to the
        // JTabbedPane
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                LOGGER.debug("Testing graphical additions on EDT");
                tabCount = tabbedPane.getTabCount();
                final Component componentAt0 = tabbedPane.getComponentAt(0);
                LOGGER.debug("component at 0 is a " + componentAt0);
                component0IsLabel = componentAt0 instanceof Label;
                tab0NameOk = SystemTabIdentifiers.OVERVIEW.getDisplayableName().equals(tabbedPane.getTitleAt(0));
                final Component componentAt1 = tabbedPane.getComponentAt(1);
                LOGGER.debug("component at 1 is a " + componentAt1);
                component1IsLabel = componentAt1 instanceof Label;
                tab1NameOk = SystemTabIdentifiers.SQL.getDisplayableName().equals(tabbedPane.getTitleAt(1));
            }
        });
        Assert.assertEquals(2, tabCount);
        Assert.assertTrue(component0IsLabel);
        Assert.assertTrue(component1IsLabel);
        Assert.assertTrue(tab0NameOk);
        Assert.assertTrue(tab1NameOk);

        EasyMock.verify(prefs);
    }

    /**
     *
     */
    @Test
    public void closingOpenDatabaseRemovesFromTheOpenTabList() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getOpenTabs(DATABASE)).andReturn(new String[] {"SQL"});
        EasyMock.expect(prefs.getActiveTab(DATABASE)).andReturn("SQL");

        prefs.setOpenTabs(EasyMock.eq(DATABASE), EasyMock.aryEq(new String[] {"SQL"}));
        prefs.setActiveTab(EasyMock.eq(DATABASE), EasyMock.eq("SQL"));

        EasyMock.replay(prefs);

        final TabListPrefs tabListPrefs = new TabListPrefs(prefs);

        adapter = new TabPaneCreatingDatabaseEventListener(tabListPrefs, tabFactory, openTabList);
        openDatabaseList.addDatabaseEventObserver(adapter);

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        openDatabaseList.addOpenedDatabase(databaseDescriptor);

        // Remember the tabs, they'll be disposed of by the close and not
        // obtainable by getTabsForDatabase, but we want to make sure they've
        // been disposed correctly.
        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        final StubRecordingTab overviewTab = (StubRecordingTab) tabsForDatabase.get(0).getTab();
        final StubRecordingTab sqlTab = (StubRecordingTab) tabsForDatabase.get(1).getTab();

        // Now close it and the open tab list should have been cleared
        openDatabaseList.removeClosedDatabase(databaseDescriptor);

        final List<TabDescriptor> clearTabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(clearTabsForDatabase);
        Assert.assertEquals(0, clearTabsForDatabase.size());

        // The TabFactory test tests for the correct calls on the correct
        // threads, but let's make sure that the TabFactory is actually being
        // called!
        Assert.assertTrue(overviewTab.isDisposeComponentCalled());
        Assert.assertTrue(overviewTab.isDisposedOnEventThread());
        Assert.assertTrue(sqlTab.isDisposeComponentCalled());
        Assert.assertTrue(sqlTab.isDisposedOnEventThread());

        EasyMock.verify(prefs);
    }

    /**
     * @throws Exception on gui value obtainer failure
     *
     */
    @Test
    public void openTabsAndPreviouslyActiveTabIsPersistedOnDatabaseClose() throws Exception {
        LOGGER.debug("** openTabsAndPreviouslyActiveTabIsPersistedOnDatabaseClose start");

        LOGGER.debug("Creating prefs");
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getOpenTabs(DATABASE)).andReturn(
            new String[] {SystemTabIdentifiers.SQL.getTabName(), SystemTabIdentifiers.CATEGORIES.getTabName()});
        EasyMock.expect(prefs.getActiveTab(DATABASE)).andReturn("SQL");
        prefs.setOpenTabs(EasyMock.eq(DATABASE), EasyMock.aryEq(
            new String[] {SystemTabIdentifiers.SQL.getTabName(), SystemTabIdentifiers.CATEGORIES.getTabName()}));
        prefs.setActiveTab(EasyMock.eq(DATABASE), EasyMock.eq("SQL"));
        EasyMock.replay(prefs);
        LOGGER.debug("Prefs created");

        final TabListPrefs tabListPrefs = new TabListPrefs(prefs);

        adapter = new TabPaneCreatingDatabaseEventListener(tabListPrefs, tabFactory, openTabList);
        openDatabaseList.addDatabaseEventObserver(adapter);

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        LOGGER.debug("Test now adding opened database");
        openDatabaseList.addOpenedDatabase(databaseDescriptor);

        final Boolean sqlIsActiveTab = new GUIValueObtainer<Boolean>().obtainFromEventThread(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                final JTabbedPane tabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
                final Label selectedComponent = (Label) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                LOGGER.debug("selected component is " + selectedComponent);
                return selectedComponent.getText().equals(SystemTabIdentifiers.SQL.getDisplayableName());
            }
        });
        Assert.assertTrue(sqlIsActiveTab);

        LOGGER.debug("Test now removing closed database");
        openDatabaseList.removeClosedDatabase(databaseDescriptor);

        EasyMock.verify(prefs);
        LOGGER.debug("** openTabsAndPreviouslyActiveTabIsPersistedOnDatabaseClose end");
    }
}
