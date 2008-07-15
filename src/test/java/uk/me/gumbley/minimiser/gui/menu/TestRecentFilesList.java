package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.prefs.IPrefs;


/**
 * Tests the recent files list model
 * 
 * @author matt
 *
 */
public final class TestRecentFilesList extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestRecentFilesList.class);
    private IPrefs prefs;

    /**
     * Do setup
     */
    @Before
    public void getRecentFilesList() {
        prefs = EasyMock.createMock(IPrefs.class);
    }
    
    /**
     * Check prefs was updated as expected
     */
    @After
    public void verifyPrefsUsage() {
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void shouldBeEmptyOnStartup() {
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        Assert.assertEquals(0, recentFilesList.getNumberOfEntries());
    }
    
    @Test
    public void addOneShouldHaveOneInPrefs() {
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one"}));
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
    }
    
    @Test
    public void addSameOneOnlyYieldsOneButDoesntSaveTwiceSinceNoReordering() {
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one"}));
        //EasyMock.expectLastCall().times(2);
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("one"));
        Assert.assertEquals(1, recentFilesList.getNumberOfEntries());
    }
    
    @Test
    public void addTwoStoresTwoInPrefs() {
        EasyMock.checkOrder(prefs, true);
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one"}));
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {"one", "two"}));
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        recentFilesList.add(new DatabaseDescriptor("one"));
        recentFilesList.add(new DatabaseDescriptor("two"));
        Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
    }
    
    @Test
    public void listHasPositiveMaximumSize() {
        EasyMock.replay(prefs);
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        Assert.assertTrue(recentFilesList.getCapacity() > 1);
    }
    /*
    @Test
    public void addingNewToAFullListPushesLastOut() {
        LOGGER.debug("*** addingNewToAFullListPushesLastOut");
        EasyMock.checkOrder(prefs, true);
        // generate
        // 1
        // 1,2
        // 1,2,3
        // 1,2,3,4
        // fish,1,2,3
        final RecentFilesList recentFilesList = new RecentFilesList(prefs);
        for (int i = 1; i < recentFilesList.getCapacity() + 2; i++) {
            final ArrayList<String> list = new ArrayList<String>();
            if (i == recentFilesList.getCapacity() + 1) {
                list.add("fish");
            }
            for (int j = 0; j < i && j < recentFilesList.getCapacity(); j++) {
                list.add("" + (j + 1));
            }
            LOGGER.debug("Test data: " + list);
            prefs.setRecentFiles(EasyMock.aryEq(list.toArray(new String[0])));
        }
        EasyMock.replay(prefs);
        // WOZERE this test data has an empty array at first, and
        // the last one contains '4'
        //recentFilesList.add(new DatabaseDescriptor("one"));
        //recentFilesList.add(new DatabaseDescriptor("two"));
        //Assert.assertEquals(2, recentFilesList.getNumberOfEntries());
    }
    
    @Test
    public void addingSameToFullListDoesntPushAnythingOut() {
        
    }
    
    @Test
    public void addingSameLaterReordersToHaveItFirst() {
        
    }
    */
}
