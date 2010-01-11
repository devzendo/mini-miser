package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import javax.swing.SwingUtilities;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.ApplicationMenu;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;

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
