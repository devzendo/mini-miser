package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabListPrefs;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
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

    private OpenDatabaseList openDatabaseList;
    private OpenTabList openTabList;
    private TabPaneCreatingDatabaseEventListener adapter;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        openDatabaseList = new OpenDatabaseList();
    }
    
    @Test
    public void tabsGetAddedWhenPrefsHoldsThen() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getOpenTabs("db")).andReturn(new String[] {"SQL"});
        EasyMock.replay(prefs);

        final TabListPrefs tabListPrefs = new TabListPrefs(prefs);
        
        adapter = new TabPaneCreatingDatabaseEventListener(tabListPrefs);
        openDatabaseList.addDatabaseEventObserver(adapter);
        
        // WOZERE need a fake tab factory
    }
    
}
