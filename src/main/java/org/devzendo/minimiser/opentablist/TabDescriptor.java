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

import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;

/**
 * A view tab. Contains an identifier, and a Tab (which contains a
 * graphical component).
 * <p>
 * Equality for TabDescriptors is intentionally limited to the TabIdentifier.
 * 
 * @author matt
 */
public final class TabDescriptor {
    private final TabIdentifier tabIdentifier;

    private final Tab tab;

    /**
     * Create a TabDescriptor with an identifier and implementation.
     * 
     * @param id
     *        the TabIdentifier for this tab
     * @param tabImpl
     *        the implementation for this tab
     */
    public TabDescriptor(final TabIdentifier id, final Tab tabImpl) {
        this.tabIdentifier = id;
        this.tab = tabImpl;
    }

    /**
     * Create a TabDescriptor with an identifier but no implementation.
     * 
     * @param id
     *        the TabIdentifier for this tab
     */
    public TabDescriptor(final TabIdentifier id) {
        this(id, null);
    }
    
    /**
     * Get the tab's TabIdentifier
     * 
     * @return the TabIdentifier
     */
    public TabIdentifier getTabIdentifier() {
        return tabIdentifier;
    }

    /**
     * Get the tab's Tab implementation
     * 
     * @return the Tab implementation.
     */
    public Tab getTab() {
        return tab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((tabIdentifier == null) ? 0 : tabIdentifier.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TabDescriptor other = (TabDescriptor) obj;
        if (tabIdentifier == null) {
            if (other.tabIdentifier != null) {
                return false;
            }
        } else if (!tabIdentifier.equals(other.tabIdentifier)) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return tabIdentifier.toString() + "(tab=" + (tab == null ? "null" : tab.getClass().getSimpleName()) + ")";
    }
}
