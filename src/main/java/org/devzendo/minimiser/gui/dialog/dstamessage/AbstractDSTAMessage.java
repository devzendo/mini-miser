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

package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.Component;

/**
 * A message that can be set such that it isn't shown again.
 * 
 * @author matt
 *
 */
public abstract class AbstractDSTAMessage implements DSTAMessage {
    private final DSTAMessageFactory messageFactory;
    private final DSTAMessageId messageId;
    private final String messageText; // the message contains either this...
    private final Component messageContent; // or this

    /**
     * Construct the message.
     * @param factory the message factory
     * @param msgId the Key for this message
     * @param string the message text
     */
    public AbstractDSTAMessage(final DSTAMessageFactory factory, final DSTAMessageId msgId, final String string) {
        this.messageFactory = factory;
        this.messageId = msgId;
        this.messageText = string;
        this.messageContent = null;
    }

    /**
     * Construct the message
     * @param factory the factory
     * @param msgId the key for this message
     * @param content the message content
     */
    public AbstractDSTAMessage(final DSTAMessageFactory factory, final DSTAMessageId msgId, final Component content) {
        this.messageFactory = factory;
        this.messageId = msgId;
        this.messageContent = content;
        this.messageText = null;
    }

    /**
     * The subclass indicates that it does not want to be shown again.
     */
    protected final void setDontShowThisAgain() {
        messageFactory.setDontShowThisAgain(messageId);
    }

    /**
     * @return the text of the message
     */
    public final String getMessageText() {
        return messageText;
    }

    /**
     * @return the content of the message
     */
    public final Component getMessageContent() {
        return messageContent;
    }
}
