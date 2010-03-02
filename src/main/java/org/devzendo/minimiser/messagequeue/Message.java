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
 * A non-persistent, asynchronously-delivered user message.
 * Messages have subjects, an indication of their importance, and content.
 * @author matt
 *
 */
public interface Message {
    
    /**
     * What is the subject of this message?
     * @return the message subject
     */
    String getSubject();
    
    enum Importance {
        /**
         * A message with low importance 
         */
        LOW,
        /**
         * A message with medium importance
         */
        MEDIUM,
        /**
         * A message with high importance
         */
        HIGH
    };
    
    /**
     * How important is this message?
     * @return the message importance
     */
    Importance getMessageImportance();
    
    /**
     * What is the content of this message?
     * @return an Object containing the content; may be a String, may be a
     * Component.
     */
    Object getMessageContent();
}
