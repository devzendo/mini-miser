package org.devzendo.minimiser.openlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;


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
        LOGGER.debug("Adding observer " + observer.getClass().getSimpleName());
        observerList.addObserver(observer);
    }

    /**
     * Remove an observer of database events.
     * @param observer the observer to remove.
     */
    public void removeDatabaseEventObserver(final Observer<DatabaseEvent> observer) {
        LOGGER.debug("Removing observer " + observer.getClass().getSimpleName());
        observerList.removeListener(observer);
    }

    /**
     * Add a database that has been opened, and notify all registered
     * listeners of it. Switch the current database to this new one.
     * @param descriptor info about the database.
     */
    public void addOpenedDatabase(final DatabaseDescriptor descriptor) {
        if (descriptor == null) {
            LOGGER.warn("Cannot add null opened database");
            return;
        }
        LOGGER.debug("ODL Observer list is: " + observerList);
        if (!databaseList.contains(descriptor)) {
            LOGGER.info("Adding opened database " + descriptor.getDatabaseName());
            databaseList.add(descriptor);
            currentDatabaseIndex = databaseList.size() - 1;
            observerList.eventOccurred(new DatabaseOpenedEvent(descriptor));
        } else {
            LOGGER.info("Switching to " + descriptor.getDatabaseName());
            currentDatabaseIndex = databaseList.indexOf(descriptor);
        }
        observerList.eventOccurred(new DatabaseSwitchedEvent(descriptor));
    }

    /**
     * Remove a database that has been closed, and notify all registered
     * listeners of it. Switch to the database that's the same index in the
     * list, unless we've just closed the one at the end of the list, in
     * which case, switch to the one at the new end of list. 
     * @param descriptor info about the database.
     */
    public void removeClosedDatabase(final DatabaseDescriptor descriptor) {
        if (descriptor == null) {
            LOGGER.warn("Cannot remove null opened database");
            return;
        }
        if (!databaseList.contains(descriptor)) {
            throw new IllegalStateException(
                String.format("Could not remove closed database %s since it is not in list", descriptor.getDatabaseName()));
        }
        LOGGER.debug("Removing closed database " + descriptor.getDatabaseName() + "; before removing, current index = " + currentDatabaseIndex);
        databaseList.remove(descriptor);
        if (currentDatabaseIndex == databaseList.size()) {
            currentDatabaseIndex--;
        }
        LOGGER.debug("after removing, current index = " + currentDatabaseIndex);
        observerList.eventOccurred(new DatabaseClosedEvent(descriptor));
        if (currentDatabaseIndex == -1) {
            LOGGER.debug("Database list is now empty");
            observerList.eventOccurred(new DatabaseListEmptyEvent());
        } else {
            final DatabaseDescriptor descriptorToSwitchTo = databaseList.get(currentDatabaseIndex);
            LOGGER.debug("Database list is not empty; switching to " + descriptorToSwitchTo.getDatabaseName());
            observerList.eventOccurred(new DatabaseSwitchedEvent(descriptorToSwitchTo));
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
        for (int i = 0; i < databaseList.size(); i++) {
            final DatabaseDescriptor descriptor = databaseList.get(i);
            if (descriptor.getDatabaseName().equals(databaseName)) {
                LOGGER.info("Switching to " + databaseName);
                currentDatabaseIndex = i;
                observerList.eventOccurred(new DatabaseSwitchedEvent(descriptor));
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
        final boolean contained = databaseList.contains(databaseDescriptor);
        return contained;
    }

    /**
     * If the list is empty, send the DatabaseListEmptyEvent event
     * so update listeners that need to do something on emptiness
     * in the case where the list is not populated on startup - i.e
     * when there are no databases to load.
     */
    public void checkForEmptiness() {
        final int size = databaseList.size();
        if (size == 0) {
            LOGGER.info("Database list is empty");
            observerList.eventOccurred(new DatabaseListEmptyEvent());
        } else {
            LOGGER.info("Database list is not empty (" + size + " elements)");
        }
    }
}
