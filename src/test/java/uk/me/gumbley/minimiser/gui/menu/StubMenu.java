package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuBar;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

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
    private boolean recentListBuilt;
    private DatabaseDescriptor[] recentDatabases;
    private ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;

    /**
     * 
     */
    public StubMenu() {
        databases = new ArrayList<String>();
        recentDatabases = new DatabaseDescriptor[0];
        currentDatabaseIndex = -1;
        recentListBuilt = false;
        openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();
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
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
    }

    /**
     * {@inheritDoc}
     */
    public void refreshRecentList(final DatabaseDescriptor[] databaseDescriptors) {
        this.recentDatabases = databaseDescriptors;
        recentListBuilt = true;
    }

    /**
     * @return whether or not the recent list has been built.
     */
    public boolean isRecentListBuilt() {
        return recentListBuilt;
    }

    /**
     * @return the details of the recently accessed databases
     */
    DatabaseDescriptor[] getRecentDatabases() {
        return recentDatabases;
    }
    
    /**
     * For unit tests, inject a request to open an entry from the recent list.
     * @param dbName the database name to open.
     * @param dbPath the database path to open
     */
    void injectOpenRecentRequest(final String dbName, final String dbPath) {
        openRecentSubmenuChoiceObservers.eventOccurred(new DatabaseNameAndPathChoice(dbName, dbPath));
    }

    /**
     * {@inheritDoc}
     */
    public void addOpenRecentObserver(final Observer<DatabaseNameAndPathChoice> observer) {
        openRecentSubmenuChoiceObservers.addObserver(observer);
    }
}
