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

import org.devzendo.commonspring.springloader.SpringLoader;

/**
 * Toolkit for easily creating DSTA Messages.
 * 
 * @author matt
 *
 */
public final class DSTAMessageHelper {
    /**
     * 
     */
    private DSTAMessageHelper() {
        // no instances
    }
    
    private static DSTAMessageFactory messageFactory;
    
    
    /**
     * Initialise the toolkit by obtaining the things it needs to create
     * DSTA Messages.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        messageFactory = springLoader.getBean("dstaMessageFactory", DSTAMessageFactory.class);
    }
    

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param string the text of the message
     * @return a DSTAMessage
     */
    public static DSTAMessage possiblyShowMessage(final DSTAMessageId messageId, final String string) {
        return messageFactory.possiblyShowMessage(messageId, string);
    }

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param content a graphical component holding the message
     * @return a DSTAMessage
     */
    public static DSTAMessage possiblyShowMessage(final DSTAMessageId messageId, final Component content) {
        return messageFactory.possiblyShowMessage(messageId, content);
    }


}
