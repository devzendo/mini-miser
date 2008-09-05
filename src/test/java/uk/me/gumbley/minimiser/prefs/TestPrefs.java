package uk.me.gumbley.minimiser.prefs;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests user preference storage API
 * 
 * @author matt
 *
 */
public final class TestPrefs extends LoggingTestCase {
    private static final String WINDOW_GEOMETRY = "20,20,700,500";
    private static final String WIZARD_PANEL_SIZE = "200,300";
    private static final String FOO = "foo";
    private Prefs prefs;

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
    public static Prefs createUnitTestPrefsFile() throws IOException {
        final File tempFile = File.createTempFile("minimiser-unit-test", "prefs").getAbsoluteFile();
        tempFile.deleteOnExit();
        return new DefaultPrefsImpl(tempFile.getAbsolutePath());
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
}
