package uk.me.gumbley.minimiser.upgradedetector;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;


/**
 * Tests the ability to listen to the UpgradeDetector via a list of listener
 * beans.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/upgradedetector/UpgradeEventListenerTestCase.xml")
public final class TestUpgradeEventListenerManager extends SpringLoaderUnittestCase {
    
    private UpgradeEventListenerManager listenerManager;
    private StubRecordingUpgradeListener stub;

    /**
     * @param prefs the mock prefs
     */
    public void setupTestPrerequisites(final Prefs prefs) {
        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(prefs);

        listenerManager = getSpringLoader().getBean("listenerManager", UpgradeEventListenerManager.class);
        stub = getSpringLoader().getBean("stub", StubRecordingUpgradeListener.class);
        
        Assert.assertNotNull(listenerManager);
        Assert.assertNotNull(stub);
    }
    
    /**
     * 
     */
    @Test
    public void detectFreshInstallation() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn(null);
        prefs.setCurrentSoftwareVersion(EasyMock.eq("1.0.0"));
        EasyMock.replay(prefs);

        setupTestPrerequisites(prefs);
    
        Assert.assertNull(stub.getObservedEvent());
        
        listenerManager.checkForUpgrade("1.0.0");
        
        final UpgradeEvent upgradeEvent = stub.getObservedEvent();
        Assert.assertNotNull(upgradeEvent);
        Assert.assertTrue(upgradeEvent instanceof FreshInstallEvent);
        final FreshInstallEvent freshInstallEvent = (FreshInstallEvent) upgradeEvent;
        Assert.assertEquals("1.0.0", freshInstallEvent.getRunningVersion());
        
        // Now should have set the stored version
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void detectUpgradeInstallation() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        prefs.setCurrentSoftwareVersion(EasyMock.eq("1.1.0"));
        EasyMock.replay(prefs);

        setupTestPrerequisites(prefs);
    
        Assert.assertNull(stub.getObservedEvent());
        
        listenerManager.checkForUpgrade("1.1.0");
        
        final UpgradeEvent upgradeEvent = stub.getObservedEvent();
        Assert.assertNotNull(upgradeEvent);
        Assert.assertTrue(upgradeEvent instanceof SoftwareUpgradedEvent);
        final SoftwareUpgradedEvent softwareUpgradedEvent = (SoftwareUpgradedEvent) upgradeEvent;
        Assert.assertEquals("1.0.0", softwareUpgradedEvent.getPreviousVersion());
        Assert.assertEquals("1.1.0", softwareUpgradedEvent.getRunningVersion());

        
        EasyMock.verify(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void detectSameInstallation() {
        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.expect(prefs.getCurrentSoftwareVersion()).andReturn("1.0.0");
        EasyMock.replay(prefs);

        setupTestPrerequisites(prefs);
    
        Assert.assertNull(stub.getObservedEvent());
        
        listenerManager.checkForUpgrade("1.0.0");
        
        final UpgradeEvent upgradeEvent = stub.getObservedEvent();
        Assert.assertNull(upgradeEvent);
        
        EasyMock.verify(prefs);
    }
}