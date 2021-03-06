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

package org.devzendo.minimiser.recentlist;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests the recent files list model
 * 
 * @author matt
 *
 */
public final class TestRecentFilesList  {
    private static final Logger LOGGER = Logger
            .getLogger(TestRecentFilesList.class);
    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * 
     */
    @Test
    public void shouldBeEmptyOnStartup() {
        final MiniMiserPrefs prefs = EasyMock.createMock(MiniMiserPrefs.class);
        EasyMock.expect(prefs.getRecentFiles()).andReturn(new String[0]);
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertEquals(0, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[0], recentFilesList.getRecentDatabases()));
        
        EasyMock.verify(prefs);
    }
    
    private <T> boolean arrayEqual(final T[] a, final T[] b) {
        return Arrays.asList(a).equals(Arrays.asList(b));
    }
    
    /**
     * 
     */
    @Test
    public void instantiateShouldLoadPrefs() {
        final MiniMiserPrefs prefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        prefs.getRecentFiles();
        EasyMock.expectLastCall().andReturn((new String[0]));
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertEquals(0, recentFilesList.getNumberOfEntries());
    }

    /**
     * 
     */
    @Test
    public void addOneShouldHaveOneInPrefs() {
        final String expectedEscapedPair = DatabasePairEncapsulator.escape("one", "/tmp/foo");

        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addOneShouldFireListener() {
        final String expectedEscapedPair = DatabasePairEncapsulator.escape("one", "/tmp/foo");

        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListChangedEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListChangedEvent.class));
        EasyMock.replay(obs);

        recentFilesList.addRecentListEventObserver(obs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        EasyMock.verify(obs);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addNonReorderingOneShouldNotFireListenerTwice() {
        final String expectedEscapedPair = DatabasePairEncapsulator.escape("one", "/tmp/foo");

        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListChangedEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListChangedEvent.class));
        // but only once...
        EasyMock.replay(obs);

        recentFilesList.addRecentListEventObserver(obs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        EasyMock.verify(obs);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addReorderingOneShouldFireListenerTwice() {
        final String oneExpectedEscapedPair = DatabasePairEncapsulator.escape("one", "/tmp/foo");
        final String twoExpectedEscapedPair = DatabasePairEncapsulator.escape("two", "/tmp/foo");

        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {oneExpectedEscapedPair}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {twoExpectedEscapedPair, oneExpectedEscapedPair}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {oneExpectedEscapedPair, twoExpectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListChangedEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListChangedEvent.class)); // one
        obs.eventOccurred(EasyMock.isA(RecentListChangedEvent.class)); // two
        obs.eventOccurred(EasyMock.isA(RecentListChangedEvent.class)); // one moved
        EasyMock.replay(obs);

        recentFilesList.addRecentListEventObserver(obs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));
        recentFilesList.add(new DatabaseDescriptor("two", "/tmp/foo"));
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        EasyMock.verify(obs);
    }

    private MiniMiserPrefs getInitiallyEmptyPrefs() {
        final MiniMiserPrefs prefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(prefs.getRecentFiles()).andReturn((new String[0]));
        return prefs;
    }
    
    /**
     * 
     */
    @Test
    public void addSameOneOnlyYieldsOneButDoesntSaveTwiceSinceNoReordering() {
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        
        final String expectedEscapedPair = DatabasePairEncapsulator.escape("one", "");
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        //EasyMock.expectLastCall().times(2); no!
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("one"));
        
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addTwoStoresTwoInPrefs() {
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("one", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("two", ""), 
                DatabasePairEncapsulator.escape("one", "")}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("two"));
        
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(
            new DatabaseDescriptor[] {
                    new DatabaseDescriptor("two"),
                    new DatabaseDescriptor("one")
                    }, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void listHasPositiveMaximumSize() {
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertTrue(recentFilesList.getCapacity() > 1);
    }

    /**
     * 
     */
    @Test
    public void addingNewToAFullListPushesLastOut() {
        LOGGER.debug("*** addingNewToAFullListPushesLastOut");
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("4", ""),
                DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("fish", ""),
                DatabasePairEncapsulator.escape("4", ""),
                DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", "")}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);

        // test
        recentFilesList.add(new DatabaseDescriptor("1")); // 1
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("2")); // 2,1
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("3")); // 3,2,1
        Assert.assertEquals(3, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("4")); // 4,3,2,1
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("fish")); // fish,4,3,2
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        EasyMock.verify(prefs);
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {
                new DatabaseDescriptor("fish"),
                new DatabaseDescriptor("4"),
                new DatabaseDescriptor("3"),
                new DatabaseDescriptor("2"),
                }, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameToFullListDoesntPushAnythingOut() {
        LOGGER.debug("*** addingSameToFullListDoesntPushAnythingOut");
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("4", ""),
                DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        // no save when 4 re-added
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);
        
        // test
        recentFilesList.add(new DatabaseDescriptor("1")); // 1
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("2")); // 2,1
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("3")); // 3,2,1
        Assert.assertEquals(3, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("4")); // 4,3,2,1
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("4")); // 4,3,2,1 but not re-saved
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        EasyMock.verify(prefs);
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {
                new DatabaseDescriptor("4"),
                new DatabaseDescriptor("3"),
                new DatabaseDescriptor("2"),
                new DatabaseDescriptor("1"),
                }, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameLaterReordersToHaveItFirst() {
        LOGGER.debug("*** addingSameLaterReordersToHaveItFirst");
        final MiniMiserPrefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("4", ""),
                DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", ""),
                DatabasePairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("1", ""),
                DatabasePairEncapsulator.escape("4", ""),
                DatabasePairEncapsulator.escape("3", ""),
                DatabasePairEncapsulator.escape("2", "")}));
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);
        
        // test
        recentFilesList.add(new DatabaseDescriptor("1")); // 1
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("2")); // 2,1
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("3")); // 3,2,1
        Assert.assertEquals(3, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("4")); // 4,3,2,1
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        recentFilesList.add(new DatabaseDescriptor("1")); // 1,4,3,2
        Assert.assertEquals(4, recentFilesList.getNumberOfEntries());
        EasyMock.verify(prefs);
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {
                new DatabaseDescriptor("1"),
                new DatabaseDescriptor("4"),
                new DatabaseDescriptor("3"),
                new DatabaseDescriptor("2"),
                }, recentFilesList.getRecentDatabases()));

        EasyMock.verify(prefs);
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void saveAndLoadRestoresOK() throws IOException {
        final MiniMiserPrefs prefs = TestPrefs.createUnitTestPrefsFile();
        final File prefsFile = new File(prefs.getAbsolutePath());
        try {
            final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
            recentFilesList.add(new DatabaseDescriptor("1", "/tmp/foo"));

            final RecentFilesList reopenRecentFilesList = new DefaultRecentFilesListImpl(prefs);
            Assert.assertEquals(1, reopenRecentFilesList.getNumberOfEntries());
            final DatabaseDescriptor[] recentFiles = reopenRecentFilesList.getRecentDatabases();
            Assert.assertEquals("1", recentFiles[0].getDatabaseName());
            Assert.assertEquals("/tmp/foo", recentFiles[0].getDatabasePath());
        } finally {
            prefsFile.delete();
        }
    }
}
