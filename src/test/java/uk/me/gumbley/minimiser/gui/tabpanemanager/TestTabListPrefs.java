package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;


/**
 * Test the higher-level tab list prefs storage and logic.
 * 
 * @author matt
 *
 */
public final class TestTabListPrefs extends LoggingTestCase {
    private Prefs prefs;
    private File prefsFile;
    
    private TabListPrefs tabListPrefs;

    /**
     * @throws IOException on prefs creation failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());

        tabListPrefs = new TabListPrefs(prefs);
    }
    
    /**
     * 
     */
    @After
    public void ditchPrefs() {
        prefsFile.deleteOnExit();
    }
    

    /**
     * 
     */
    @Test
    public void allPermanentTabsInOpenListAndInRightOrder() {
        final List<TabIdentifier> openTabs = tabListPrefs.getOpenTabs("database");
        Assert.assertNotNull(openTabs);
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        Assert.assertEquals(permanentTabIdentifiers, openTabs);
    }
    
    /**
     * 
     */
    @Test
    public void duplicateAndPermanentTabsNotStored() {
        final List<TabIdentifier> badTabs = new ArrayList<TabIdentifier>();
        badTabs.add(TabIdentifier.OVERVIEW); // permanent
        badTabs.add(TabIdentifier.OVERVIEW);
        badTabs.add(TabIdentifier.SQL); // not permanent, but duplicated
        badTabs.add(TabIdentifier.SQL);
        badTabs.add(TabIdentifier.SQL);
        tabListPrefs.setOpenTabs("database", badTabs);
        
        final List<TabIdentifier> openTabs = tabListPrefs.getOpenTabs("database");
        Assert.assertNotNull(openTabs);
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        Assert.assertTrue(openTabs.containsAll(permanentTabIdentifiers));
        Assert.assertTrue(openTabs.contains(TabIdentifier.SQL));
        Assert.assertEquals(permanentTabIdentifiers.size() + 1, openTabs.size());
        
        // now check underneath the high level TabListPrefs - no permanent tabs
        // should be stored
        final String[] lowLevelOpenTabNames = prefs.getOpenTabs("database");
        int sqlCount = 0;
        for (final String tabName : lowLevelOpenTabNames) {
            final TabIdentifier nonPermanentTabId = TabIdentifier.valueOf(tabName);
            Assert.assertFalse(permanentTabIdentifiers.contains(nonPermanentTabId));
            
            if (tabName.equals(TabIdentifier.SQL.toString())) {
                sqlCount++;
            }
        }
        Assert.assertEquals(1, sqlCount);
    }
}
