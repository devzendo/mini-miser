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

package org.devzendo.minimiser.prefs;

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.prefs.MiniMiserPrefs.PrefsSection;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests user preference storage API
 * 
 * @author matt
 *
 */
public final class TestPrefs {
    private static final String WINDOW_GEOMETRY = "20,20,700,500";
    private static final String WIZARD_PANEL_SIZE = "200,300";
    private static final String FOO = "foo";
    private MiniMiserPrefs prefs;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * Create a temporary file to hold prefs data, that's deleted after
     * the JVM exits.
     * 
     * @throws IOException on failure
     */
    @Before
    public void getPrefs() throws IOException {
        prefs = createUnitTestPrefsFile();
    }
    
    /**
     * Create a temporary prefs file for unit tests. Please ensure it gets
     * deleted!
     * @return a new Prefs object.
     * @throws IOException on failure
     */
    public static MiniMiserPrefs createUnitTestPrefsFile() throws IOException {
        final File tempFile = File.createTempFile("minimiser-unit-test", "prefs").getAbsoluteFile();
        tempFile.deleteOnExit();
        return new DefaultMiniMiserPrefsImpl(tempFile.getAbsolutePath());
    }

    /**
     * 
     */
    @Test
    public void testGetDefaultGeometry() {
        Assert.assertEquals("", prefs.getWindowGeometry(FOO));
    }
    
    /**
     * 
     */
    @Test
    public void testGetStoredGeometry() {
        prefs.setWindowGeometry(FOO, WINDOW_GEOMETRY);
        Assert.assertEquals(WINDOW_GEOMETRY, prefs.getWindowGeometry(FOO));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefaultWizardPanelSize() {
        Assert.assertEquals("", prefs.getWizardPanelSize());
    }
    
    /**
     * 
     */
    @Test
    public void testGetStoredWizardPanelSize() {
        prefs.setWizardPanelSize(WIZARD_PANEL_SIZE);
        Assert.assertEquals(WIZARD_PANEL_SIZE, prefs.getWizardPanelSize());
    }
    
    /**
     * 
     */
    @Test
    public void shouldInitiallyHaveEmptyRecentFiles() {
        final String[] recentFiles = prefs.getRecentFiles();
        Assert.assertNotNull(recentFiles);
        Assert.assertEquals(0, recentFiles.length);
    }
    
    /**
     * 
     */
    @Test
    public void shouldHaveOneRecentFileAfterAddOfOne() {
        prefs.setRecentFiles(new String[] {"/tmp/foo/foo"});
        final String[] recentFiles = prefs.getRecentFiles();
        Assert.assertNotNull(recentFiles);
        Assert.assertEquals(1, recentFiles.length);
        Assert.assertEquals("/tmp/foo/foo", recentFiles[0]);
    }
    
    /**
     * 
     */
    @Test
    public void shouldHaveOneOpenFileAfterAddOfOne() {
        prefs.setOpenFiles(new String[] {"/tmp/foo/foo"});
        final String[] openFiles = prefs.getOpenFiles();
        Assert.assertNotNull(openFiles);
        Assert.assertEquals(1, openFiles.length);
        Assert.assertEquals("/tmp/foo/foo", openFiles[0]);
    }
    
    /**
     * 
     */
    @Test
    public void shouldHaveLastActiveFileAfterSettingIt() {
        prefs.setLastActiveFile("one");
        Assert.assertEquals("one", prefs.getLastActiveFile());
    }
    
    /**
     * 
     */
    @Test
    public void shouldHaveNullLastActiveFileAfterClearingIt() {
        prefs.setLastActiveFile("one");
        prefs.clearLastActiveFile();
        Assert.assertNull(prefs.getLastActiveFile());
    }
    
    /**
     * 
     */
    @Test
    public void unstoredDatabaseHasNoTabsReturned() {
        final String[] openTabNames = prefs.getOpenTabs("one");
        Assert.assertNotNull(openTabNames);
        Assert.assertEquals(0, openTabNames.length);
    }
    
    /**
     * 
     */
    @Test
    public void storedEmptyDatabaseHasNoTabsOpen() {
        prefs.setOpenTabs("one", new String[0]);
        final String[] openTabNames = prefs.getOpenTabs("one");
        Assert.assertNotNull(openTabNames);
        Assert.assertEquals(0, openTabNames.length);
    }
    
    /**
     * 
     */
    @Test
    public void storedDatabaseWithTabsStored() {
        prefs.setOpenTabs("one", new String[] {"tabone", "tabtwo", "tabthree"});
        final String[] openTabNames = prefs.getOpenTabs("one");
        Assert.assertNotNull(openTabNames);
        Assert.assertEquals(3, openTabNames.length);
        Assert.assertEquals("tabone", openTabNames[0]);
        Assert.assertEquals("tabtwo", openTabNames[1]);
        Assert.assertEquals("tabthree", openTabNames[2]);
    }
    
    /**
     * 
     */
    @Test
    public void activeDatabaseTabIsStoredAndRetrieved() {
        Assert.assertNull(prefs.getActiveTab("one"));
        prefs.setActiveTab("one", "tabone");
        Assert.assertEquals("tabone", prefs.getActiveTab("one"));
    }
    
    /**
     * 
     */
    @Test
    public void tabsCanBeHidden() {
        Assert.assertFalse(prefs.isTabHidden("SQL"));
        prefs.setTabHidden("SQL");
        Assert.assertTrue(prefs.isTabHidden("SQL"));
        prefs.clearTabHidden("SQL");
        Assert.assertFalse(prefs.isTabHidden("SQL"));
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void changeListenersFiredOnAppropriateChanges() {

        final Observer<PrefsEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new PrefsEvent(PrefsSection.HIDDEN_TABS)));
        EasyMock.replay(obs);

        prefs.addChangeListener(obs);
        
        prefs.setTabHidden("SQL");
        
        EasyMock.verify(obs);
        
        EasyMock.reset(obs);
        obs.eventOccurred(EasyMock.eq(new PrefsEvent(PrefsSection.HIDDEN_TABS)));
        EasyMock.replay(obs);
        
        prefs.clearTabHidden("SQL");

        EasyMock.verify(obs);
        
        EasyMock.reset(obs);
        obs.eventOccurred(EasyMock.eq(new PrefsEvent(PrefsSection.BOOLEAN_FLAGS)));
        EasyMock.replay(obs);
        
        prefs.setBooleanFlag(BooleanFlagsForTests.TEST, true);

        EasyMock.verify(obs);
    }
    
    /**
     * 
     */
    @Test
    public void softwareVersionIsStoredAndRetrieved() {
        Assert.assertNull(prefs.getCurrentSoftwareVersion());
        prefs.setCurrentSoftwareVersion("1.1.2");
        Assert.assertEquals("1.1.2", prefs.getCurrentSoftwareVersion());
    }
    
    /**
     * 
     */
    @Test
    public void dontShowThisAgainFlagCanBeSetAndCleared() {
        final String messageId = "foo";
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(messageId));
        
        prefs.setDontShowThisAgainFlag(messageId);
        
        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(messageId));
        
        prefs.clearDontShowThisAgainFlag(messageId);
        
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(messageId));
        
        prefs.setDontShowThisAgainFlag(messageId);

        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(messageId));

        prefs.clearAllDontShowThisAgainFlags();

        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(messageId));
    }
    
    /**
     * 
     */
    @Test
    public void generalBooleanFlagCanBeChanged() {
        Assert.assertFalse(prefs.isBooleanFlagSet(BooleanFlagsForTests.TEST));
        
        prefs.setBooleanFlag(BooleanFlagsForTests.TEST, true);
        
        Assert.assertTrue(prefs.isBooleanFlagSet(BooleanFlagsForTests.TEST));
        
        prefs.setBooleanFlag(BooleanFlagsForTests.TEST, false);
        
        Assert.assertFalse(prefs.isBooleanFlagSet(BooleanFlagsForTests.TEST));
    }
    
    /**
     * 
     */
    @Test
    public void lastUpdateCheckDateCanBeChanged() {
        Assert.assertEquals("", prefs.getDateOfLastUpdateAvailableCheck());
        
        prefs.setDateOfLastUpdateAvailableCheck("8 Dec 2008");

        Assert.assertEquals("8 Dec 2008", prefs.getDateOfLastUpdateAvailableCheck());
    }
    
    /**
     * 
     */
    @Test
    public void lastRemoteUpdateVersionCanBeChanged() {
        Assert.assertEquals("", prefs.getLastRemoteUpdateVersion());
        
        prefs.setLastRemoteUpdateVersion("1.2.3");
        
        Assert.assertEquals("1.2.3", prefs.getLastRemoteUpdateVersion());
    }
}
