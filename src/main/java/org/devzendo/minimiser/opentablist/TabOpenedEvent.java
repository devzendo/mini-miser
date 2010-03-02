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

package org.devzendo.minimiser.opentablist;

import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * A tab has been opened, this is the notification you receive from the 
 * OpenTabList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class TabOpenedEvent extends TabEvent {

    /**
     * Create a TabOpenedEvent describing a specific tab.
     * @param databaseDescriptor the descriptor of the database that owns
     * this tab
     * @param tabDescriptor the descriptor of the tab that has opened.
     */
    public TabOpenedEvent(final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        super(databaseDescriptor, tabDescriptor);
    }
}
