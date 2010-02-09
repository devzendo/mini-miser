package org.devzendo.minimiser.opentablist;

import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * A tab has been removed, this is the notification you receive from the
 * OpenTabList, if you're an observer.
 *
 * @author matt
 *
 */
public final class TabRemovedEvent extends TabEvent {

    /**
     * Create a TabRemovedEvent describing a specific tab.
     * @param databaseDescriptor the descriptor of the database that owns
     * this tab
     * @param tabDescriptor the descriptor of the tab that has been removed.
     */
    public TabRemovedEvent(final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        super(databaseDescriptor, tabDescriptor);
    }
}
