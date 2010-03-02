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
 * Stub DSTA Message that allows the DSTA 'checkbox' to be checked by tests.
 * @author matt
 *
 */
public final class StubDSTAMessage extends AbstractDSTAMessage {

    /**
     * Construct a stub message
     * @param factory the factory
     * @param msgId the key for this message
     * @param string the message text
     */
    public StubDSTAMessage(final DSTAMessageFactory factory, final DSTAMessageId msgId, final String string) {
        super(factory, msgId, string);
    }


    /**
     * Construct a stub message
     * @param factory the factory
     * @param msgId the key for this message
     * @param content the message content
     */
    public StubDSTAMessage(final StubDSTAMessageFactory factory, final DSTAMessageId msgId, final Component content) {
        super(factory, msgId, content);
    }

    /**
     * the stub wants this message blocking
     */
    public void getOutOfMyFace() {
        setDontShowThisAgain();
    }
}
