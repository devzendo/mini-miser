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

package org.devzendo.minimiser.recentlist;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * Maintains a short list of recently-opened files.
 * New entries are added at the head, pushing out the last entries if the
 * capacity has been exceeded.
 * Existing entries re-opened are moved to the head.
 * 
 * @author matt
 *
 */
public interface RecentFilesList {
    /**
     * The default capacity for the recent files list.
     * Note that if this changes, the unit tests will have to be modified, as
     * they currently have an assumption on this.
     */
    int DEFAULT_CAPACITY = 4;

    /**
     * @return the number of entries in the recent files list.
     */
    int getNumberOfEntries();

    /**
     * Add an entry to the recent files list, pushing out older items if this
     * new entry is not currently present. If it is present, move it to the head
     * of the list.
     * @param databaseDescriptor the database descriptor to add.
     */
    void add(final DatabaseDescriptor databaseDescriptor);

    /**
     * How many unique entries can this list contain before throwing older
     * entries out upon addition?
     * @return the capacity of the list.
     */
    int getCapacity();

    /**
     * Return the array of recent databases
     * @return an array of database descriptors, never null
     */
    DatabaseDescriptor[] getRecentDatabases();
    
    /**
     * Add an observer that will be notified every time the recent list changes.
     * @param observer the observer
     */
    void addRecentListEventObserver(Observer<RecentListChangedEvent> observer);
}