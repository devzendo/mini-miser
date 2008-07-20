package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

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
        final String databaseName = currentDatabase.getDatabaseName();
        LOGGER.info("Closing database '" + databaseName + "'");
        try {
            // TODO perhaps DatabaseDescriptor should be polymorphic?
            if (currentDatabase instanceof MiniMiserDatabaseDescriptor) {
                final MiniMiserDatabaseDescriptor mmdd = (MiniMiserDatabaseDescriptor) currentDatabase;
                try {
                    mmdd.getDatabase().close();
                    LOGGER.info("Database '" + databaseName + "' closed");
                    return true;
                } catch (final DataAccessException dae) {
                    LOGGER.warn("Could not close database '" + databaseName + "': " + dae.getMessage(), dae);
                    // TODO pass main frame in here
                    ProblemDialog.reportProblem(null, "while closing the '" + currentDatabase.getDatabaseName() + "' database", dae);
                    return false;
                }
            } else {
                LOGGER.error("Closing a non-MiniMiserDatabaseDescriptor");
                return false;
            }
        } finally {
            databaseList.removeClosedDatabase(currentDatabase);
        }
    }
}
