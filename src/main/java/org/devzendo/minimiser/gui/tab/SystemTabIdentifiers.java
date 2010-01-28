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
    private static List<TabIdentifier> permanentIds;
    static {
        permanentIds = new ArrayList<TabIdentifier>();
        for (final TabIdentifier tabId : TabIdentifier.values()) {
            if (tabId.isTabPermanent()) {
                permanentIds.add(tabId);
            }
        }
    }
    /**
     * Obtain a list of the permanent tab identifiers, in TabIdentifier order.
     * @return the permanent TabIdentifiers.
     */
    public static List<TabIdentifier> getPermanentTabIdentifiers() {
        return permanentIds;
    }

}
