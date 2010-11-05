/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.tabpanemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test the higher-level tab list prefs storage and logic.
 *
 * @author matt
 *
 */
public final class TestTabListPrefs {
    private Prefs prefs;
    private File prefsFile;

    private TabListPrefs tabListPrefs;
    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

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
