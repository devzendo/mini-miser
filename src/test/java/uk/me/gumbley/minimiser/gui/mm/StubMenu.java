package uk.me.gumbley.minimiser.gui.mm;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuBar;
import uk.me.gumbley.minimiser.gui.mm.MenuMediatorImpl.DatabaseSwitchObserver;

/**
 * A Menu that tests can interrogate to test the correctness of the
 * MenuMediator.
 * 
 * @author matt
 *
 */
public final class StubMenu implements Menu {
    private boolean closeMenuEnabled;
    private List<String> databases;
    private int currentDatabaseIndex;
    
    /**
     * 
     */
    public StubMenu() {
        databases = new ArrayList<String>();
        currentDatabaseIndex = -1;
    }
    
    /**
     * @return whether close is enabled
     */
    public boolean isCloseEnabled() {
        return closeMenuEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
        closeMenuEnabled = enabled;
    }

    /**
     * @return the number of elements in the database list.
     */
    public int getNumberOfDatabases() {
        return databases.size();
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabase(final String dbName) {
        databases.add(dbName);
    }

    /**
     * @return the current database name
     */
    public String getCurrentDatabase() {
        return currentDatabaseIndex == -1 ? null :
            databases.get(currentDatabaseIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void switchDatabase(final String dbName) {
        currentDatabaseIndex = databases.indexOf(dbName);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
        databases.remove(dbName);
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
        databases.clear();
        currentDatabaseIndex = -1;
        closeMenuEnabled = false;
    }

    /**
     * {@inheritDoc}
     */
    public JMenuBar getMenuBar() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final DatabaseSwitchObserver observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
    }
}
