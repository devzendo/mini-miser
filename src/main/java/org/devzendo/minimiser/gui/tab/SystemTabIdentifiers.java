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

package org.devzendo.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.List;

/**
 * The TabIdentifiers provided by the framework are...
 *
 * @author matt
 *
 */
public class SystemTabIdentifiers {

    /**
     * The SQL debug/developer tab
     */
    public static final TabIdentifier SQL = new TabIdentifier("SQL", "SQL", false, 'S', true, "tabSQL", null);

    /**
     * The overview tab
     */
    public static final TabIdentifier OVERVIEW = new TabIdentifier("OVERVIEW", "Overview", true, 'O', true, "tabOVERVIEW", null);

    /**
     * The Categories tab
     */
    public static final TabIdentifier CATEGORIES = new TabIdentifier("CATEGORIES", "Categories", false, 'C', true, "tabCATEGORIES", null);

    private static List<TabIdentifier> permanentIds;
    static {
        permanentIds = new ArrayList<TabIdentifier>();
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            if (tabId.isTabPermanent()) {
                permanentIds.add(tabId);
            }
        }
    }

    /**
     * Obtain a list of the permanent system tab identifiers, in TabIdentifier order.
     * @return the permanent TabIdentifiers.
     */
    public static List<TabIdentifier> getPermanentTabIdentifiers() {
        return permanentIds;
    }

    /**
     * @param tabName a tab name
     * @return the system TabIdentifier for the given tabName
     */
    public static TabIdentifier valueOf(final String tabName) {
        for (final TabIdentifier systemTabIdentifier : values()) {
            if (systemTabIdentifier.getTabName().equals(tabName)) {
                return systemTabIdentifier;
            }
        }
        throw new IllegalArgumentException("Unknown tab name '" + tabName + "'");
    }

    /**
     * @return all the system TabIdentifiers
     */
    public static TabIdentifier[] values() {
        return new TabIdentifier[] {SQL, OVERVIEW, CATEGORIES};
    }
}
