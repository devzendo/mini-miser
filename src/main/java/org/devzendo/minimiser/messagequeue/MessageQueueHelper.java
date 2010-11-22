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

import org.devzendo.commonapp.spring.springloader.SpringLoader;

/**
 * Toolkit for easily adding Messages to the MessageQueue..
 * 
 * @author matt
 *
 */
public final class MessageQueueHelper {
    private static MessageQueue messageQueue;

    /**
     * 
     */
    private MessageQueueHelper() {
        // no instances
    }
    
    /**
     * Initialise the toolkit by obtaining the things it needs to add messages
     * to the Message Queue.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        messageQueue = springLoader.getBean("messageQueue", MessageQueue.class);
    }

    /**
     * @see MessageQueue.addMessage(Message)
     * @param message the message to add.
     */
    public static void addMessage(final Message message) {
        messageQueue.addMessage(message);
    }
}
