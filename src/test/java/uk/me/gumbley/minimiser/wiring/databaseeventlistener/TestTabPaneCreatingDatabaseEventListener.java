package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.gui.tabpanefactory.StubTabFactory;
import uk.me.gumbley.minimiser.gui.tabpanefactory.TabFactory;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabListPrefs;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Tests the adapter that adapts between DatabaseEvents and the
 * creation and population of the database descriptor's
 * TabbedPane attribute. Tests that tabs defined for the various
 * TabIdentifiers are loaded and attached if they are permanent
 * or stored in prefs as an open tab.
 * 
 * 
 * @author matt
 *
 */
public final class TestTabPaneCreatingDatabaseEventListener extends LoggingTestCase {

    private static final String DATABASE = "db";
    private OpenDatabaseList openDatabaseList;
    private OpenTabList openTabList;
    private TabFactory tabFactory;
    private TabPaneCreatingDatabaseEventListener adapter;

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
     * 
     */
    @Test
    public void openingDatabaseCausesTabsStoredInPrefsAndPermanentTabsToBeAddedToTheOpenTabList() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getOpenTabs(DATABASE)).andReturn(new String[] {"SQL"});
        EasyMock.replay(prefs);

        final TabListPrefs tabListPrefs = new TabListPrefs(prefs);
        
        adapter = new TabPaneCreatingDatabaseEventListener(tabListPrefs, tabFactory);
        openDatabaseList.addDatabaseEventObserver(adapter);
        
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        openDatabaseList.addOpenedDatabase(databaseDescriptor);
        
        // The JTabbedPane should now have been added to the DD
        Assert.assertNotNull(databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane));
        
        // The SQL tab should have been added to the openTabList
        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(2, tabsForDatabase.size());
        Assert.assertEquals(TabIdentifier.SQL, tabsForDatabase.get(0).getTabIdentifier());
        Assert.assertEquals(TabIdentifier.OVERVIEW, tabsForDatabase.get(1).getTabIdentifier());
    }
}
