package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;

/**
 * Provides the body of Tab Pane Manager functionality without involving any
 * GUI code.
 * 
 * @author matt
 *
 */
public abstract class AbstractTabPaneManager implements TabPaneManager {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractTabPaneManager.class);
    private final Map<String, JTabbedPane> namedTabbedPanes;
    
    /**
     * Construct.
     */
    public AbstractTabPaneManager() {
        namedTabbedPanes = new HashMap<String, JTabbedPane>();
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getNumberOfTabPanes() {
        return namedTabbedPanes.size();
    }

    /**
     * Add the TabbedPane that's stored in the DatabaseDescriptor.
     * Do nothing if there's no tab pane stored.
     * Replace any existing tabbed pane with the same name.
     * @param databaseDescriptor the database Descriptor.
     */
    public final void addTabPane(final DatabaseDescriptor databaseDescriptor) {
        if (databaseDescriptor == null) {
            return;
        }
        LOGGER.info("Adding tab pane for database ' " + databaseDescriptor.getDatabaseName() + "'");
        final JTabbedPane tabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
        if (tabbedPane == null) {
            return;
        }
        final String databaseName = databaseDescriptor.getDatabaseName();
        namedTabbedPanes.put(databaseName, tabbedPane);
        tabPaneAdded(databaseName, tabbedPane);
    }
    
    /**
     * Get the TabbedPane for a named database
     * @param databaseName the database name
     * @return the TabbedPane, or null if there isn't one.
     */
    public final JTabbedPane getTabPane(final String databaseName) {
        return namedTabbedPanes.get(databaseName);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeTabPane(final DatabaseDescriptor databaseDescriptor) {
        if (databaseDescriptor == null) {
            return;
        }
        final String databaseName = databaseDescriptor.getDatabaseName();
        LOGGER.info("Removing tab pane for database ' " + databaseName + "'");
        final JTabbedPane tabbedPane = namedTabbedPanes.get(databaseName);
        if (tabbedPane == null) {
            return;
        }
        namedTabbedPanes.remove(databaseName);
        tabPaneRemoved(databaseName, tabbedPane);
    }
    
    /**
     * {@inheritDoc}
     */
    public void switchToTabPane(final DatabaseDescriptor databaseDescriptor) {
        if (databaseDescriptor == null) {
            return;
        }
        final String databaseName = databaseDescriptor.getDatabaseName();
        LOGGER.info("Switching to tab pane for database ' " + databaseName + "'");
        final JTabbedPane tabbedPane = namedTabbedPanes.get(databaseName);
        if (tabbedPane == null) {
            return;
        }
        tabPaneSwitched(databaseName, tabbedPane);
    }

    /**
     * A tabbed pane has been added to the map. Do whetever a subclass needs
     * to do with it.
     * @param databaseName the name of the database
     * @param tabbedPane the tabbedpane
     */
    public abstract void tabPaneAdded(String databaseName, JTabbedPane tabbedPane);


    /**
     * A tabbed pane has been removed from the map. Do whetever a subclass needs
     * to do with it.
     * @param databaseName the name of the database
     * @param tabbedPane the tabbedpane
     */
    public abstract void tabPaneRemoved(String databaseName, JTabbedPane tabbedPane);

    /**
     * A tabbed pane has been switched to. Do whetever a subclass needs
     * to do with it.
     * @param databaseName the name of the database
     * @param tabbedPane the tabbedpane
     */
    public abstract void tabPaneSwitched(String databaseName, JTabbedPane tabbedPane);
}
