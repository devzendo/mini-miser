package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.closer.Closer;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;

/**
 * Base class for File Close / All menu action listeners.
 * 
 * @author matt
 *
 */
public abstract class AbstractFileCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractFileCloseActionListener.class);
    private final OpenDatabaseList databaseList;
    private final CursorManager cursorMan;

    /**
     * Get the cursor manager
     * @return the cursor manager
     */
    protected final CursorManager getCursorMan() {
        return cursorMan;
    }

    /**
     * Get the open database list
     * @return the open database list
     */
    protected final OpenDatabaseList getDatabaseList() {
        return databaseList;
    }

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param cursorManager the cursor manager singleton
     */
    public AbstractFileCloseActionListener(final OpenDatabaseList openDatabaseList,
            final CursorManager cursorManager) {
        super();
        this.databaseList = openDatabaseList;
        this.cursorMan = cursorManager;
    }
    
    /**
     * Close the currently selected database
     * @return true if there actually is a current database, and database closed
     * false on any problems. 
     */
    protected final boolean closeCurrentDatabase() {
        final DatabaseDescriptor currentDatabase = databaseList.getCurrentDatabase();
        if (currentDatabase == null) {
            LOGGER.warn("Closing a null database; there are " + databaseList.getNumberOfDatabases() + " open database(s)");
            return false;
        }
        try {
            return Closer.close(currentDatabase);
        } finally {
            databaseList.removeClosedDatabase(currentDatabase);
        }
    }
}
