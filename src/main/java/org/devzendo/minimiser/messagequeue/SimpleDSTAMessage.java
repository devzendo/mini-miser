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

/**
 * A SimpleDSTAMessage is a message that presents a checkbox allowing the user
 * to indicate "Don't Show This Again".
 * <p>
 * This is the unobtrusive, message queue-based version of creating a
 * DSTAMessage which is an in-your-face dialog.
 * 
 * @author matt
 *
 */
public final class SimpleDSTAMessage extends AbstractMessage implements Message {

    private DSTAMessageId dstaMessageId;
    private boolean dontShowAgain;

    /**
     * @param subject the message subject
     * @param content the message content
     * @param messageId the DSTA message id
     */
    public SimpleDSTAMessage(final String subject, final Object content, final DSTAMessageId messageId) {
        super(subject, content, Importance.MEDIUM);
        this.dstaMessageId = messageId;
        dontShowAgain = false;
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     * @param messageId the DSTA message id
     */
    public SimpleDSTAMessage(final String subject, final Object content, final Importance importance, final DSTAMessageId messageId) {
        super(subject, content, importance);
        this.dstaMessageId = messageId;
    }
    
    /**
     * Has the user set the 'dont' show this again' flag already for this
     * message? This does NOT indicate that this has been stored in prefs,
     * just that it has been requested. It'll be written to prefs when the
     * message is removed.
     * @return the uncommitted DSTA state 
     */
    public boolean dontShowAgain() {
        return dontShowAgain;
    }

    /**
     * Indicate that the user has chosen not to see this message again. This is
     * uncommitted, that is, held within the message until the message is
     * removed from the message queue, at which time it is used to make the
     * choice permanent in prefs.
     * @param dsta true if they don't want to see it again
     */
    public void setDontShowAgain(final boolean dsta) {
        this.dontShowAgain = dsta;
    }

    /**
     * Obtain the DSTA message id for this message
     * @return the DSTA message id
     */
    public DSTAMessageId getDstaMessageId() {
        return dstaMessageId;
    }
}
