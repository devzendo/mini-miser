package uk.me.gumbley.minimiser.gui.recent;

import java.util.Arrays;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.recent.DefaultRecentFilesListImpl.DbPair;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * Tests the recent files list model
 * 
 * @author matt
 *
 */
public final class TestRecentFilesList extends LoggingTestCase {
    private static final String SEP = DefaultRecentFilesListImpl.NAME_PATH_SEPARATOR;
    private static final Logger LOGGER = Logger
            .getLogger(TestRecentFilesList.class);
    /**
     * 
     */
    @Test
    public void shouldBeEmptyOnStartup() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertEquals(0, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[0], recentFilesList.getRecentFiles()));
    }
    
    private boolean arrayEqual(final DatabaseDescriptor[] a, final DatabaseDescriptor[] b) {
        return Arrays.asList(a).equals(Arrays.asList(b));
    }
    
    /**
     * 
     */
    @Test
    public void addOneShouldHaveOneInPrefs() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one" + SEP + "/tmp/foo"}));
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one", "/tmp/foo"));
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentFiles()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addSameOneOnlyYieldsOneButDoesntSaveTwiceSinceNoReordering() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one" + SEP}));
        //EasyMock.expectLastCall().times(2);
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("one"));
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
        Assert.assertTrue(arrayEqual(new DatabaseDescriptor[] {new DatabaseDescriptor("one")}, recentFilesList.getRecentFiles()));
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addTwoStoresTwoInPrefs() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"two" + SEP, "one" + SEP}));
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
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void listHasPositiveMaximumSize() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        Assert.assertTrue(recentFilesList.getCapacity() > 1);
    }

    /**
     * 
     */
    @Test
    public void addingNewToAFullListPushesLastOut() {
        LOGGER.debug("*** addingNewToAFullListPushesLastOut");
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"3" + SEP, "2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"4" + SEP, "3" + SEP, "2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"fish" + SEP, "4" + SEP, "3" + SEP, "2" + SEP}));
        EasyMock.replay(prefs);
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
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameToFullListDoesntPushAnythingOut() {
        LOGGER.debug("*** addingSameToFullListDoesntPushAnythingOut");
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"3" + SEP, "2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"4" + SEP, "3" + SEP, "2" + SEP, "1" + SEP}));
        // no save when 4 re-added
        EasyMock.replay(prefs);
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
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void addingSameLaterReordersToHaveItFirst() {
        LOGGER.debug("*** addingSameLaterReordersToHaveItFirst");
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        final RecentFilesList recentFilesList = new DefaultRecentFilesListImpl(prefs);
        final int capacity = recentFilesList.getCapacity();
        // If the default capacity of the list changes, hand-crank more test
        // data, as it's painful to generate it. This assertion will remind
        // you!
        Assert.assertEquals(4, capacity);
        // generate expected prefs storage
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"3" + SEP, "2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"4" + SEP, "3" + SEP, "2" + SEP, "1" + SEP}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"1" + SEP, "4" + SEP, "3" + SEP, "2" + SEP}));
        EasyMock.replay(prefs);
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
        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @Test
    public void testEscapeWhenNoQuotes() {
        Assert.assertEquals("\"one\",\"path\"", DefaultRecentFilesListImpl.escape("one", "path"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        final DbPair unescaped = DefaultRecentFilesListImpl.unescape("\"one\",\"two\"");
        Assert.assertEquals("one", unescaped.getName());
        Assert.assertEquals("two", unescaped.getPath());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnescapeFailure() {
        DefaultRecentFilesListImpl.unescape("\"f\\\"oo\",\"/tmp/\\\"quote"); // no end quote
    }
    
    /**
     * 
     */
    @Test
    public void testRoundTripWithQuotes() {
        final StringBuilder name = new StringBuilder();
        name.append('f');
        name.append('"');
        name.append("oo");
        final StringBuilder path = new StringBuilder();
        path.append("/tmp/");
        path.append('"');
        path.append("quote");
        final String escaped = DefaultRecentFilesListImpl.escape(name.toString(), path.toString());
        Assert.assertEquals("\"f\\\"oo\",\"/tmp/\\\"quote\"", escaped);

        final DbPair unescaped = DefaultRecentFilesListImpl.unescape(escaped);
        Assert.assertEquals(name.toString(), unescaped.getName());
        Assert.assertEquals(path.toString(), unescaped.getPath());
    }
}
