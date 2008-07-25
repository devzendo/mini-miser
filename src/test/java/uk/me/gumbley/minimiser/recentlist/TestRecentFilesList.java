package uk.me.gumbley.minimiser.recentlist;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;


/**
 * Tests the recent files list model
 * 
 * @author matt
 *
 */
public final class TestRecentFilesList extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestRecentFilesList.class);
    /**
     * 
     */
    @Test
    public void shouldBeEmptyOnStartup() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getRecentFiles()).andReturn(new String[0]);
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertEquals(0, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[0], recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[0], recentFilesList.getRecentFileNames()));
        
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
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
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
        final String expectedEscapedPair = DbPairEncapsulator.escape("one", "/tmp/foo");

        final Prefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"one"}, recentFilesList.getRecentFileNames()));

        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addOneShouldFireListener() {
        final String expectedEscapedPair = DbPairEncapsulator.escape("one", "/tmp/foo");

        final Prefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListEvent.class));
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
        final String expectedEscapedPair = DbPairEncapsulator.escape("one", "/tmp/foo");

        final Prefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListEvent.class));
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
        final String oneExpectedEscapedPair = DbPairEncapsulator.escape("one", "/tmp/foo");
        final String twoExpectedEscapedPair = DbPairEncapsulator.escape("two", "/tmp/foo");

        final Prefs prefs = getInitiallyEmptyPrefs();
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {oneExpectedEscapedPair}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {twoExpectedEscapedPair, oneExpectedEscapedPair}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {oneExpectedEscapedPair, twoExpectedEscapedPair}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final Observer<RecentListEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(RecentListEvent.class)); // one
        obs.eventOccurred(EasyMock.isA(RecentListEvent.class)); // two
        obs.eventOccurred(EasyMock.isA(RecentListEvent.class)); // one moved
        EasyMock.replay(obs);

        recentFilesList.addRecentListEventObserver(obs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));
        recentFilesList.add(new DatabaseDescriptor("two", "/tmp/foo"));
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));

        EasyMock.verify(obs);
    }

    private Prefs getInitiallyEmptyPrefs() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getRecentFiles()).andReturn((new String[0]));
        return prefs;
    }
    
    /**
     * 
     */
    @Test
    public void addSameOneOnlyYieldsOneButDoesntSaveTwiceSinceNoReordering() {
        final Prefs prefs = getInitiallyEmptyPrefs();
        
        final String expectedEscapedPair = DbPairEncapsulator.escape("one", "");
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {expectedEscapedPair}));
        //EasyMock.expectLastCall().times(2); no!
        EasyMock.replay(prefs);
        
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("one"));
        
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"one"}, recentFilesList.getRecentFileNames()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addTwoStoresTwoInPrefs() {
        final Prefs prefs = getInitiallyEmptyPrefs();
        
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("one", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("two", ""), 
                DbPairEncapsulator.escape("one", "")}));
        EasyMock.replay(prefs);

        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("two"));
        
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(
            new DatabaseDescriptor[] {
                    new DatabaseDescriptor("two"),
                    new DatabaseDescriptor("one")
                    }, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"two", "one"}, recentFilesList.getRecentFileNames()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void listHasPositiveMaximumSize() {
        final Prefs prefs = getInitiallyEmptyPrefs();
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
        final Prefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("4", ""),
                DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("fish", ""),
                DbPairEncapsulator.escape("4", ""),
                DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", "")}));
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
                }, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"fish", "4", "3", "2"}, recentFilesList.getRecentFileNames()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameToFullListDoesntPushAnythingOut() {
        LOGGER.debug("*** addingSameToFullListDoesntPushAnythingOut");
        final Prefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("4", ""),
                DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
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
                }, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"4", "3", "2", "1"}, recentFilesList.getRecentFileNames()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameLaterReordersToHaveItFirst() {
        LOGGER.debug("*** addingSameLaterReordersToHaveItFirst");
        final Prefs prefs = getInitiallyEmptyPrefs();
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("4", ""),
                DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", ""),
                DbPairEncapsulator.escape("1", "")}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DbPairEncapsulator.escape("1", ""),
                DbPairEncapsulator.escape("4", ""),
                DbPairEncapsulator.escape("3", ""),
                DbPairEncapsulator.escape("2", "")}));
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
                }, recentFilesList.getRecentFiles()));
        Assert.assertTrue(arrayEqual(new String[] {"1", "4", "3", "2"}, recentFilesList.getRecentFileNames()));
        EasyMock.verify(prefs);
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void saveAndLoadRestoresOK() throws IOException {
        final Prefs prefs = TestPrefs.createUnitTestPrefsFile();
        final File prefsFile = new File(prefs.getAbsolutePath());
        try {
            final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
            recentFilesList.add(new DatabaseDescriptor("1", "/tmp/foo"));

            final RecentFilesList reopenRecentFilesList = new DefaultRecentFilesListImpl(prefs);
            Assert.assertEquals(1, reopenRecentFilesList.getNumberOfEntries());
            final DatabaseDescriptor[] recentFiles = reopenRecentFilesList.getRecentFiles();
            Assert.assertEquals("1", recentFiles[0].getDatabaseName());
            Assert.assertEquals("/tmp/foo", recentFiles[0].getDatabasePath());
            Assert.assertTrue(arrayEqual(new String[] {"1"}, recentFilesList.getRecentFileNames()));
        } finally {
            prefsFile.delete();
        }
    }
}
