package uk.me.gumbley.minimiser.pluginmanager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Is the POM's version and artifact name available at runtime?
 * @author matt
 *
 */
public final class TestAppDetailsPropertiesLoader extends LoggingTestCase {
    private AppDetailsPropertiesLoader mAppDetailsPropertiesLoader;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mAppDetailsPropertiesLoader = new AppDetailsPropertiesLoader();
    }
    
    /**
     * 
     */
    @Test
    public void appDetailsPropertiesContainAVersion() {
        final String version = mAppDetailsPropertiesLoader.getVersion();
        validateFilteredProperty(version);
    }

    /**
     * 
     */
    @Test
    public void appDetailsPropertiesContainAName() {
        final String name = mAppDetailsPropertiesLoader.getName();
        validateFilteredProperty(name);
    }

    private void validateFilteredProperty(final String property) {
        Assert.assertNotNull(property);
        Assert.assertTrue(property.length() > 0);
        Assert.assertFalse(property.contains("$")); // has it been filtered?
        // Eclipse will fail the test here since I'm not filtering
        // resources via m2eclipse - it slows the build down, and
        // in the version of the plugin I'm using, Just Doesn't
        // Work.
        
        // It /does/ work, with the latest version of the plugin.
    }
}
