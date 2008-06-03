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
        Assert.assertEquals("0,0,640,480", prefs.getWindowGeometry());
    }
    
    /**
     * 
     */
    @Test
    public void testGetStoredGeometry() {
        prefs.setWindowGeometry("20,20,700,500");
        Assert.assertEquals("20,20,700,500", prefs.getWindowGeometry());
    }
}
