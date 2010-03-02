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

package org.devzendo.minimiser.gui.messagequeueviewer;

import org.devzendo.minimiser.messagequeue.BooleanFlagSettingMessage;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.SimpleDSTAMessage;
import org.devzendo.minimiser.messagequeue.SimpleMessage;

/**
 * A MessageRendererFactory is a factory for MessageRenderers. Given a message
 * type, create the correct MessageRenderer used to display it in the
 * MessageQueueViewer.
 * <p>
 * Messages just contain content. MessageRenderers provide graphical components
 * that display that content.
 * 
 * @author matt
 *
 */
public final class MessageRendererFactory {

    /**
     * Given a Message, create a MessageRenderer that can render this message.
     * @param message some kind of Message
     * @return a MessageRenderer for this Message
     */
    public MessageRenderer createRenderer(final Message message) {
        if (message instanceof SimpleMessage) {
            return new SimpleMessageRenderer((SimpleMessage) message);
        } else if (message instanceof SimpleDSTAMessage) {
            return new SimpleDSTAMessageRenderer((SimpleDSTAMessage) message);
        } else if (message instanceof BooleanFlagSettingMessage) {
            return new BooleanFlagSettingMessageRenderer((BooleanFlagSettingMessage) message);
        }
        return null;
    }
}
