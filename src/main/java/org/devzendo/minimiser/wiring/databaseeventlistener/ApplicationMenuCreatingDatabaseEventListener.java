package org.devzendo.minimiser.wiring.databaseeventlistener;

import javax.swing.SwingUtilities;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * When a database is opened, add an empty ApplicationMenu to its DatabaseDescriptor.
 * This will be customised by any MenuProvidingFacade.
 *
 * @author matt
 *
 */
public final class ApplicationMenuCreatingDatabaseEventListener implements
        Observer<DatabaseEvent> {

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            handleDatabaseOpenedEvent((DatabaseOpenedEvent) databaseEvent);
        }
    }

    private void handleDatabaseOpenedEvent(final DatabaseOpenedEvent openEvent) {
        // this is called on a background thread - Recent Opener,
        // Open Wizard background, or Lifecycle startup.
        assert !SwingUtilities.isEventDispatchThread();

        final DatabaseDescriptor databaseDescriptor = openEvent.getDatabaseDescriptor();
        databaseDescriptor.setAttribute(AttributeIdentifier.ApplicationMenu, new ApplicationMenu());
    }
}
