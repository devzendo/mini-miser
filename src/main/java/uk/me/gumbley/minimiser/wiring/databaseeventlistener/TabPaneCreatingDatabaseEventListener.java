package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * A database has been opened, so create a JTabbedPane for it, populate it with
 * the previously-open view tabs, or, if none have been stored (perhaps this is
 * the first time this database has been opened?) populate it with the Overview
 * view tab.
 * 
 * Note: not developed TDD.
 * 
 * @author matt
 *
 */
public final class TabPaneCreatingDatabaseEventListener implements Observer<DatabaseEvent> {
    
    private final Prefs prefs;

    /**
     * Construct the tab pane creator.
     * @param prefsStore the prefs, from which previous tab view lists will be
     * read
     */
    public TabPaneCreatingDatabaseEventListener(final Prefs prefsStore) {
        prefs = prefsStore;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        assert SwingUtilities.isEventDispatchThread();
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            final JTabbedPane tabbedPane = createTabbedPane(openEvent.getDatabaseDescriptor()); 
            openEvent.getDatabaseDescriptor().setAttribute(AttributeIdentifier.TabbedPane, tabbedPane);
        }
    }
    
    private synchronized JTabbedPane createTabbedPane(final DatabaseDescriptor descriptor) {
        // WOZERE - must run on EDT, and return tabbedpane to calling thread.
        
        final JTabbedPane tabbedPane = new JTabbedPane();
        // restore previous open view tabs from prefs, or if not any, default to
        // overview tab?
        tabbedPane.add("Tab One", new JPanel());
        tabbedPane.add("Tab Two", new JPanel());
        tabbedPane.add("Tab Three", new JPanel());
        return tabbedPane;
    }
}
