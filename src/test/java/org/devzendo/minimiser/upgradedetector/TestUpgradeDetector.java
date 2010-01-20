package org.devzendo.minimiser.upgradedetector;

import junit.framework.Assert;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.prefs.Prefs;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests detection of fresh and upgrade installations.
 * 
 * @author matt
 *
 */
public final class TestUpgradeDetector extends LoggingTestCase {
    
    private static final String CURRENT_VERSION = "1.1.2";

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantDetectWithNull() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        detector.upgraded(null);
    }

    /**
     * 
     */
    @Test
    public void freshInstallDetects() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        Assert.assertTrue(detector.freshInstall());
        Assert.assertTrue(detector.upgraded(CURRENT_VERSION));
        Assert.assertNull(detector.getStoredVersion());
        
        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @Test
    public void differentStoredVersionDetects() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);

        Assert.assertFalse(detector.freshInstall());
        Assert.assertTrue(detector.upgraded(CURRENT_VERSION));
        Assert.assertEquals("1.0.0", detector.getStoredVersion());
        
        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @Test
    public void sameStoredVersionDoesNotDetect() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        Assert.assertFalse(detector.freshInstall());
        Assert.assertFalse(detector.upgraded(CURRENT_VERSION));
        Assert.assertEquals(CURRENT_VERSION, detector.getStoredVersion());
        

        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantStoreNullVersion() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        detector.storeCurrentVersion(null);
    }
    
    /**
     * 
     */
    @Test
    public void freshInstallDoesntDetectAfterStore() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        prefs.setCurrentSoftwareVersion(EasyMock.eq(CURRENT_VERSION));
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        Assert.assertTrue(detector.freshInstall());
        Assert.assertTrue(detector.upgraded(CURRENT_VERSION));
        
        detector.storeCurrentVersion(CURRENT_VERSION);
        
        Assert.assertFalse(detector.upgraded(CURRENT_VERSION));
        Assert.assertFalse(detector.freshInstall());
        Assert.assertEquals(CURRENT_VERSION, detector.getStoredVersion());

        EasyMock.verify(prefs);
    }

    /**
     * 
     */
    @Test
    public void differentStoredVersionDoesntDetectAfterStore() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        prefs.setCurrentSoftwareVersion(EasyMock.eq(CURRENT_VERSION));
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        Assert.assertFalse(detector.freshInstall());
        Assert.assertTrue(detector.upgraded(CURRENT_VERSION));
        
        detector.storeCurrentVersion(CURRENT_VERSION);
        
        Assert.assertFalse(detector.upgraded(CURRENT_VERSION));
        Assert.assertFalse(detector.freshInstall());
        Assert.assertEquals(CURRENT_VERSION, detector.getStoredVersion());
        
        EasyMock.verify(prefs);
    }

    /**
     * Probably superfluous, as you probably wouldn't store if the version is
     * the same (and thus no upgrade detection), but just for completeness.
     */
    @Test
    public void sameStoredVersionDoesntDetectAfterStore() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        prefs.setCurrentSoftwareVersion(EasyMock.eq(CURRENT_VERSION));
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(CURRENT_VERSION);
        EasyMock.replay(prefs);

        final UpgradeDetector detector = new UpgradeDetector(prefs);
        
        Assert.assertFalse(detector.freshInstall());
        Assert.assertFalse(detector.upgraded(CURRENT_VERSION));
        
        detector.storeCurrentVersion(CURRENT_VERSION); // probably wouldn't do this IRL
        
        Assert.assertFalse(detector.freshInstall());
        Assert.assertFalse(detector.upgraded(CURRENT_VERSION));
        Assert.assertEquals(CURRENT_VERSION, detector.getStoredVersion());
        
        EasyMock.verify(prefs);
    }

}
