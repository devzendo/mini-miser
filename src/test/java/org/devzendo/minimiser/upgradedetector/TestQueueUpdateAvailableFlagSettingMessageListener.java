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

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.messagequeue.MessageQueueEvent;
import org.devzendo.minimiser.messagequeue.MessageQueueModifiedEvent;
import org.devzendo.minimiser.messagequeue.StubMessageQueuePrefs;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * When updated/fresh install is detected, and the 'check updates allowed'
 * flag is not set in prefs, post a message requesting the setting of the flag.
 * 
 * @author matt
 *
 */
public final class TestQueueUpdateAvailableFlagSettingMessageListener {
    private MiniMiserPrefs prefs;
    private MessageQueue messageQueue;
    private QueueUpdateAvailableFlagSettingMessageListener listener;
    private MessageQueueModifiedRecorder recorder;
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        prefs = new StubMessageQueuePrefs();
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(prefs));
        recorder = new MessageQueueModifiedRecorder();
        messageQueue.addMessageQueueEventObserver(recorder);
        final PluginRegistry pluginRegistry = new DummyAppPluginRegistry("Test App", "0.1.0");
        listener = new QueueUpdateAvailableFlagSettingMessageListener(prefs, messageQueue, pluginRegistry);
    }
    
    private class MessageQueueModifiedRecorder implements Observer<MessageQueueEvent> {

        private int queueSize = 0;
        
        public final int getQueueSize() {
            return queueSize;
        }

        public void eventOccurred(final MessageQueueEvent observableEvent) {
            if (observableEvent instanceof MessageQueueModifiedEvent) {
                final MessageQueueModifiedEvent mqme = (MessageQueueModifiedEvent) observableEvent;
                queueSize = mqme.getNewQueueSize();
            }
        }
        
    }
    /**
     * Quite how the flag can be set on a fresh install is beyond me, but let's
     * test the case anyway
     */
    @Test
    public void flagSetOnFreshInstallShouldYieldNoMessage() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new FreshInstallEvent("1.0.0"));
        
        Assert.assertEquals(0, recorder.getQueueSize());
    }
    

    /**
     * 
     */
    @Test
    public void flagSetOnUpgradeShouldYieldNoMessage() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new SoftwareUpgradedEvent("1.0.0", "0.5.0"));
        
        Assert.assertEquals(0, recorder.getQueueSize());
    }
    
    /**
     * This is the usual fresh startup case
     */
    @Test
    public void flagNotSetOnFreshInstallShouldYieldMessage() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new FreshInstallEvent("1.0.0"));
        
        Assert.assertEquals(1, recorder.getQueueSize());
    }
    

    /**
     * 
     */
    @Test
    public void flagNotSetOnUpgradeShouldYieldMessage() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new SoftwareUpgradedEvent("1.0.0", "0.5.0"));
        
        Assert.assertEquals(1, recorder.getQueueSize());
    }
}
