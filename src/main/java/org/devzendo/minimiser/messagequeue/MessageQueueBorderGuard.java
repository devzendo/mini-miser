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
 * MessageQueueBorderGuards vet incoming messages for suitability to be added to
 * the MessageQueue. They also process messages that have been on the
 * MessageQueue and that have been removed.
 * <p>
 * This is a GoF Strategy.
 * 
 * @author matt
 *
 */
public interface MessageQueueBorderGuard {

    /**
     * Is the given message allowed to be placed on the MessageQueue?
     * @param message the Message to investigate
     * @return true iff allowed to be placed on the MessageQueue
     */
    boolean isAllowed(Message message);

    /**
     * Messages that are allowed onto the queue are prepared - i.e. any state
     * stored persistently is moved into the message.
     * 
     * @param message the message being prepared
     */
    void prepareMessage(Message message);

    /**
     * When this message is being removed from the MessageQueue, process any
     * type-specific behaviour for this message, e.g. persisting the DSTA
     * flag for the message.
     * @param message the Message to process during removal
     */
    void processMessageRemoval(Message message);
}
