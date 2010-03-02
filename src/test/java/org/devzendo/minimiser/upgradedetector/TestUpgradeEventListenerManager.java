/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.upgradedetector;

import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the ability to listen to the UpgradeDetector via a list of listener
 * beans.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/upgradedetector/UpgradeEventListenerTestCase.xml")
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
