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

import org.devzendo.commoncode.patterns.observer.ObservableEvent;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * Something has happened with the OpenTabList, this is the notification
 * you receive from the OpenTabList, if you're an observer.
 * 
 * Note that only the TabDescriptor is included in the equality code.
 * 
 * @author matt
 *
 */
public abstract class TabEvent implements ObservableEvent {
    private final TabDescriptor tabDescriptor;
    private final DatabaseDescriptor databaseDescriptor;

    /**
     * Create a TabEvent describing a specific tab.
     * @param database the database descriptor that owns this tab
     * @param tab the tab descriptor of the tab that
     * has opened.
     */
    public TabEvent(final DatabaseDescriptor database, final TabDescriptor tab) {
        this.databaseDescriptor = database;
        this.tabDescriptor = tab;
    }

    /**
     * Obtain the DatabaseDescriptor associated with this event.
     * @return the DatabaseDescriptor
     */
    public final DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }

    /**
     * Obtain the TabDescriptor associated with this event.
     * @return the TabDescriptor
     */
    public final TabDescriptor getTabDescriptor() {
        return tabDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tabDescriptor == null) ? 0 : tabDescriptor.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TabEvent other = (TabEvent) obj;
        if (tabDescriptor == null) {
            if (other.tabDescriptor != null) {
                return false;
            }
        } else if (!tabDescriptor.equals(other.tabDescriptor)) {
            return false;
        }
        return true;
    }
}
