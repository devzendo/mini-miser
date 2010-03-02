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



/**
 * A simple message.
 * 
 * @author matt
 *
 */
public final class SimpleMessage extends AbstractMessage {
    
    
    /**
     * @param subject the message subject
     * @param content the message content
     */
    public SimpleMessage(final String subject, final Object content) {
        super(subject, content);
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     */
    public SimpleMessage(final String subject, final Object content, final Importance importance) {
        super(subject, content, importance);
    }
}
