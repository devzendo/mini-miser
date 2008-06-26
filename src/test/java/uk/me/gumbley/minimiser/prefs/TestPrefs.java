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
        final File tempFile = File.createTempFile("minimiser-unit-test", "prefs").getAbsoluteFile();
        tempFile.deleteOnExit();
        prefs = new Prefs(tempFile.getAbsolutePath());
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
    public void testGetStoredWizardPanelSize() {
        prefs.setWizardPanelSize(WIZARD_PANEL_SIZE);
        Assert.assertEquals(WIZARD_PANEL_SIZE, prefs.getWizardPanelSize());
    }
}
