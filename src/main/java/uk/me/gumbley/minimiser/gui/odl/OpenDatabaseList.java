package uk.me.gumbley.minimiser.gui.odl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * Maintains a list of open databases, and fires events when this changes.
 * 
 * @author matt
 *
 */
public final class OpenDatabaseList {
    private static final Logger LOGGER = Logger
            .getLogger(OpenDatabaseList.class);
    private final ObserverList<DatabaseEvent> observerList;
    private final List<DatabaseDescriptor> databaseList;
    private int currentDatabaseIndex; 
   
    
    /**
     * Create an OpenDatabaseList.
     */
    public OpenDatabaseList() {
        observerList = new ObserverList<DatabaseEvent>();
        databaseList = new ArrayList<DatabaseDescriptor>();
        currentDatabaseIndex = -1;
    }

    /**
     * How many databases are open?
     * @return the number of open databases.
     */
    public int getNumberOfDatabases() {
        return databaseList.size();
    }

    /**
     * Add an observer of database events.
     * @param observer the observer to add.
     */
    public void addDatabaseEventObserver(final Observer<DatabaseEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * Remove an observer of database events.
     * @param observer the observer to remove.
     */
    public void removeDatabaseEventObserver(final Observer<DatabaseEvent> observer) {
        observerList.removeListener(observer);
    }

    /**
     * Add a database that has been opened, and notify all registered
     * listeners of it. Switch the current database to this new one.
     * @param descriptor info about the database.
     */
    public void addOpenedDatabase(final DatabaseDescriptor descriptor) {
        if (!databaseList.contains(descriptor)) {
            LOGGER.info("Adding " + descriptor.getDatabaseName());
            databaseList.add(descriptor);
            currentDatabaseIndex = databaseList.size() - 1;
            observerList.eventOccurred(new DatabaseOpenedEvent(descriptor.getDatabaseName()));
        }
        LOGGER.info("Switching to " + descriptor.getDatabaseName());
        observerList.eventOccurred(new DatabaseSwitchedEvent(descriptor.getDatabaseName()));
    }

    /**
     * Remove a database that has been closed, and notify all registered
     * listeners of it. Switch to the database that's the same index in the
     * list, unless we've just closed the one at the end of the list, in
     * which case, switch to the one at the new end of list. 
     * @param descriptor info about the database.
     */
    public void removeClosedDatabase(final DatabaseDescriptor descriptor) {
        if (!databaseList.contains(descriptor)) {
            throw new IllegalStateException(
                String.format("Could not remove closed database %s since it is not in list", descriptor.getDatabaseName()));
        }
        databaseList.remove(descriptor);
        LOGGER.info("before curr = " + currentDatabaseIndex);
        if (currentDatabaseIndex == databaseList.size()) {
            currentDatabaseIndex--;
        }
        LOGGER.info("after curr = " + currentDatabaseIndex);
        observerList.eventOccurred(new DatabaseClosedEvent(descriptor.getDatabaseName()));
        if (currentDatabaseIndex == -1) {
            observerList.eventOccurred(new DatabaseListEmptyEvent());
        } else {
            observerList.eventOccurred(new DatabaseSwitchedEvent(databaseList.get(currentDatabaseIndex).getDatabaseName()));
        }
    }

    /**
     * Get the list of open databases
     * @return a list of open databases, as a List<DatabaseDescriptor>.
     */
    public List<DatabaseDescriptor> getOpenDatabases() {
        final ArrayList<DatabaseDescriptor> clone = new ArrayList<DatabaseDescriptor>();
        clone.addAll(databaseList);
        return clone;
    }

    /**
     * @return the currently selected DatabaseDescriptor, or null if there is
     * none.
     */
    public DatabaseDescriptor getCurrentDatabase() {
        if (currentDatabaseIndex >= 0) {
            return databaseList.get(currentDatabaseIndex);
        }
        return null;
    }

    /**
     * Switch to an existing database
     * @param databaseName the name to switch to
     */
    public void switchDatabase(final String databaseName) {
        for (DatabaseDescriptor descriptor : databaseList) {
            if (descriptor.getDatabaseName().equals(databaseName)) {
                LOGGER.info("Switching to " + databaseName);
                observerList.eventOccurred(new DatabaseSwitchedEvent(databaseName));
                return;
            }
        }
    }

    /**
     * Does the list contain this database?
     * @param databaseDescriptor a database descriptor
     * @return true if contained, false if not contained.
     * 
     */
    public boolean containsDatabase(final DatabaseDescriptor databaseDescriptor) {
        return databaseList.contains(databaseDescriptor);
    }
}
