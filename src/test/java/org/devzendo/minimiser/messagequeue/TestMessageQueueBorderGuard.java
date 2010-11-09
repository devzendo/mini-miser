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

package org.devzendo.minimiser.messagequeue;

import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the MessageQueueBorderGuards
 * 
 * @author matt
 *
 */
public final class TestMessageQueueBorderGuard {
    
    private StubMessageQueueMiniMiserPrefs prefs;
    private MessageQueueBorderGuardFactory borderGuardFactory;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        prefs = new StubMessageQueueMiniMiserPrefs();
        borderGuardFactory = new MessageQueueBorderGuardFactory(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void simpleMessageIsntVetoed() {
        final SimpleMessage message = new SimpleMessage("subject", "text");
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertTrue(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIsntBlockedIsntVetoed() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertTrue(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIsBlockedIsVetoed() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        prefs.setDontShowThisAgainFlag(DSTAMessageId.TEST.toString());
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertFalse(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIndicatesBlockageIsPersistedOnRemoval() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        message.setDontShowAgain(true);
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(message.getDstaMessageId().toString()));
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.processMessageRemoval(message);
        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(message.getDstaMessageId().toString()));
    }
    
    /**
     * 
     */
    @Test
    public void updateCheckMessageIsPopulatedWithPersistentStateOfFlag() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        final BooleanFlagSettingMessage message = new BooleanFlagSettingMessage(
            "allow updates check?", "could the app check for updates?", CoreBooleanFlags.UPDATE_CHECK_ALLOWED, "Do this?");
        Assert.assertFalse(message.isBooleanFlagSet());
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.prepareMessage(message);
        Assert.assertTrue(message.isBooleanFlagSet());
    }
    
    /**
     * 
     */
    @Test
    public void updateCheckMessageThatChangesFlagChangesPersistentFlagOnRemoval() {
        final BooleanFlagSettingMessage message = new BooleanFlagSettingMessage(
            "allow updates check?", "could the app check for updates?", CoreBooleanFlags.UPDATE_CHECK_ALLOWED, "Do this?");
        message.setBooleanFlagValue(true);
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.processMessageRemoval(message);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
    }
}
