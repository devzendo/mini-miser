package uk.me.gumbley.minimiser.pluginmanager;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests correct behaviour of the AppDetails bean
 * @author matt
 *
 */
public final class TestAppDetails {
    private static final String UNKNOWN = "unknown";
    private AppDetails mAppDetails;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mAppDetails = new AppDetails();
    }
    
    /**
     * 
     */
    @Test
    public void unknownOnStartup() {
        Assert.assertEquals(UNKNOWN, mAppDetails.getApplicationName());
        Assert.assertEquals(UNKNOWN, mAppDetails.getApplicationVersion());
        Assert.assertFalse(mAppDetails.isApplicationVersionSet());
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantSetNullName() {
        mAppDetails.setApplicationName(null);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantSetEmptyName() {
        mAppDetails.setApplicationName("");
    }
    

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantSetNullVersion() {
        mAppDetails.setApplicationVersion(null);
        Assert.assertFalse(mAppDetails.isApplicationVersionSet());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantSetEmptyVersion() {
        mAppDetails.setApplicationVersion("");
        Assert.assertFalse(mAppDetails.isApplicationVersionSet());
    }
    
    /**
     * 
     */
    @Test
    public void setOperationsWork() {
        final String applicationName = "foo";
        mAppDetails.setApplicationName(applicationName);
        Assert.assertEquals(applicationName, mAppDetails.getApplicationName());

        final String applicationVersion = "0.1.0";
        mAppDetails.setApplicationVersion(applicationVersion);
        Assert.assertEquals(applicationVersion, mAppDetails.getApplicationVersion());

        Assert.assertTrue(mAppDetails.isApplicationVersionSet());
    }
}
