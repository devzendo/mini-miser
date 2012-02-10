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

package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.commonapp.lifecycle.LifecycleManager;
import org.devzendo.commonapp.prefs.PrefsFactory;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.HeadlessStatusBar;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.devzendo.minimiser.messagequeue.StubMessageQueueMiniMiserPrefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the wiring of the Status Bar to Message Queue adapters that are
 * controlled as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/StatusBarAdaptersLifecycleTestCase.xml")
public final class TestStatusBarAdapterLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private HeadlessStatusBar headlessStatusBar;
    private MessageQueue messageQueue;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);

        headlessStatusBar = getSpringLoader().getBean("statusBar", HeadlessStatusBar.class);
        Assert.assertNotNull(headlessStatusBar);

        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(new StubMessageQueueMiniMiserPrefs());

        messageQueue = getSpringLoader().getBean("messageQueue", MessageQueue.class);
        Assert.assertNotNull(messageQueue);
    }

    /**
     *
     */
    @Test
    public void taskExecutedOnStartup() {
        final SimpleMessage simpleMessage = new SimpleMessage("subject", "content");

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(simpleMessage);
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.removeMessage(simpleMessage);

        lifecycleManager.startup();

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(simpleMessage);
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }

    /**
     *
     */
    @Test
    public void launchViewerButtonBoundOnStartup() {
        Assert.assertFalse(headlessStatusBar.isLaunchMessageQueueActionListenerSet());

        lifecycleManager.startup();

        Assert.assertTrue(headlessStatusBar.isLaunchMessageQueueActionListenerSet());
    }
}
