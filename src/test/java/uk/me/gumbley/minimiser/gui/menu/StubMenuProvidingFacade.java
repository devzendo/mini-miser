package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProvidingFacade;

/**
 * A MenuProvidingFacade for tests.
 *
 * @author matt
 *
 */
public final class StubMenuProvidingFacade implements MenuProvidingFacade {

    private ApplicationMenu mGlobalApplicationMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private boolean mInitialisedOnEventDispatchThread;
    private RuntimeException mException;

    /**
     * {@inheritDoc}
     */
    public void initialise(
            final ApplicationMenu globalApplicationMenu,
            final OpenDatabaseList openDatabaseList,
            final MenuFacade menuFacade) {
        mGlobalApplicationMenu = globalApplicationMenu;
        mOpenDatabaseList = openDatabaseList;

        mInitialisedOnEventDispatchThread = SwingUtilities.isEventDispatchThread();

        if (mException != null) {
            throw mException;
        }

        final JMenu customMenu = new JMenu("Custom");
        mGlobalApplicationMenu.addCustomMenu(customMenu);

        mOpenDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {

            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent open = (DatabaseOpenedEvent) observableEvent;
                    final DatabaseDescriptor databaseDescriptor = open.getDatabaseDescriptor();
                    final ApplicationMenu applicationMenu = (ApplicationMenu) databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu);
                    applicationMenu.addCustomMenu(new JMenu("DB Custom"));
                }

            }

        });
    }

    /**
     * @return the globalApplicationMenu
     */
    public ApplicationMenu getGlobalApplicationMenu() {
        return mGlobalApplicationMenu;
    }

    /**
     * @return true iff initialise was called on the Swing Event Thread
     */
    public boolean initialisedOnEventThread() {
        return mInitialisedOnEventDispatchThread;
    }

    /**
     * @param exception a failure to inject that should get displayed by the problem reporter
     */
    public void injectFailure(final RuntimeException exception) {
        mException = exception;
    }
}
