package org.devzendo.minimiser.gui.tabpanemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers.getPermanentTabIdentifiers();
        Assert.assertEquals(permanentTabIdentifiers, openTabs);
    }

    /**
     *
     */
    @Test
    public void duplicateAndPermanentTabsNotStored() {
        final List<TabIdentifier> badTabs = new ArrayList<TabIdentifier>();
        badTabs.add(SystemTabIdentifiers.OVERVIEW); // permanent
        badTabs.add(SystemTabIdentifiers.OVERVIEW);
        badTabs.add(SystemTabIdentifiers.SQL); // not permanent, but duplicated
        badTabs.add(SystemTabIdentifiers.SQL);
        badTabs.add(SystemTabIdentifiers.SQL);
        tabListPrefs.setOpenTabs("database", badTabs);

        final List<TabIdentifier> openTabs = tabListPrefs.getOpenTabs("database");
        Assert.assertNotNull(openTabs);
        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers.getPermanentTabIdentifiers();
        Assert.assertTrue(openTabs.containsAll(permanentTabIdentifiers));
        Assert.assertTrue(openTabs.contains(SystemTabIdentifiers.SQL));
        Assert.assertEquals(permanentTabIdentifiers.size() + 1, openTabs.size());

        // now check underneath the high level TabListPrefs - no permanent tabs
        // should be stored
        final String[] lowLevelOpenTabNames = prefs.getOpenTabs("database");
        int sqlCount = 0;
        for (final String tabName : lowLevelOpenTabNames) {
            final TabIdentifier nonPermanentTabId = SystemTabIdentifiers.valueOf(tabName);
            Assert.assertFalse(permanentTabIdentifiers.contains(nonPermanentTabId));

            if (tabName.equals(SystemTabIdentifiers.SQL.getTabName())) {
                sqlCount++;
            }
        }
        Assert.assertEquals(1, sqlCount);
    }
}
