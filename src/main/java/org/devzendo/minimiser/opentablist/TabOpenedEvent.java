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
