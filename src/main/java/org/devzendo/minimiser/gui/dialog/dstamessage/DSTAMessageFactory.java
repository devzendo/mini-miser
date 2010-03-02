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
 * Creates popup messages that allow the user to indicate that they don't want
 * to see the message again, with this choice being stored in prefs.
 * 
 * @author matt
 *
 */
public interface DSTAMessageFactory {

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param string the text of the message
     * @return a DSTAMessage
     */
    DSTAMessage possiblyShowMessage(DSTAMessageId messageId, String string);

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param content a graphical component holding the message
     * @return a DSTAMessage
     */
    DSTAMessage possiblyShowMessage(DSTAMessageId messageId, Component content);

    /**
     * Indicate that this message should not be shown again; called by messages
     * when the user checks the DSTA checkbox
     * @param messageId the key for this message
     */
    void setDontShowThisAgain(DSTAMessageId messageId);
}
