package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabListPrefs;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * A database has been opened, so create a JTabbedPane for it,
 * populate it with {the previously-open view tabs UNION the
 * permanent tabs}
 * 
 * TODO WOZERE: not developed TDD.
 * 
 * @author matt
 *
 */
public final class TabPaneCreatingDatabaseEventListener implements Observer<DatabaseEvent> {
    private static final Logger LOGGER = Logger
        .getLogger(TabPaneCreatingDatabaseEventListener.class);    
    private final TabListPrefs prefs;
    
    // Used by the run-on-EDT code in createTabbedPaneOnEventThread
    private final Object lock = new Object();
    private JTabbedPane tabbedPane;
    

    /**
     * Construct the tab pane creator.
     * @param prefsStore the prefs, from which previous tab view lists will be
     * read
     */
    public TabPaneCreatingDatabaseEventListener(final TabListPrefs prefsStore) {
        prefs = prefsStore;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        // this is called on a background thread - Recent Opener,
        // Open Wizard background, or Lifecycle startup.
        assert !SwingUtilities.isEventDispatchThread();
        LOGGER.info("creating tabbed pane...");
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            final JTabbedPane pane = createTabbedPaneOnEventThread(openEvent.getDatabaseDescriptor()); 
            openEvent.getDatabaseDescriptor().setAttribute(AttributeIdentifier.TabbedPane, pane);
        }
    }

    private JTabbedPane createTabbedPaneOnEventThread(final DatabaseDescriptor descriptor) {
        if (SwingUtilities.isEventDispatchThread()) {
            return createTabbedPane(descriptor);
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    synchronized (lock) {
                        tabbedPane = createTabbedPane(descriptor);
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (final InterruptedException e) {
                return null;
            }
            synchronized (lock) {
                return tabbedPane;
            }
        }
    }
        
    private synchronized JTabbedPane createTabbedPane(final DatabaseDescriptor descriptor) {
        assert SwingUtilities.isEventDispatchThread();
        LOGGER.info("Creating tabbed pane views for database '" + descriptor.getDatabaseName() + "'");
        // WOZERE - must run on EDT, and return tabbedpane to calling thread.
        
        final JTabbedPane pane = new JTabbedPane();
        // restore previous open view tabs from prefs, or if not any, default to
        // overview tab?
        pane.add("Tab One", createPanel1());
        pane.add("Tab Two", createPanel2());
        pane.add("Tab Three", createPanel3());
        return pane;
    }
    private JPanel createPanel1() {
        final JPanel panel = new JPanel();
        panel.add(new JButton("One"));
        return panel;
    }
    private JPanel createPanel2() {
        final JPanel panel = new JPanel();
        panel.add(new JButton("Two"));
        return panel;
    }
    private JPanel createPanel3() {
        final JPanel panel = new JPanel();
        panel.add(new JButton("Three"));
        return panel;
    }
}
