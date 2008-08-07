package uk.me.gumbley.minimiser.gui.lifecycle;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

/**
 * A Lifecycle that closes all open databases.
 * 
 * @author matt
 *
 */
public class DatabaseCloserLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseCloserLifecycle.class);
    private final OpenDatabaseList openDatabaseList;

    /**
     * We need to store the open database list so we know what to close on
     * shutdown.
     * @param openList the OpenDatabaseList
     */
    public DatabaseCloserLifecycle(final OpenDatabaseList openList) {
        this.openDatabaseList = openList;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        LOGGER.info("Closing open databases");
        final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
        for (DatabaseDescriptor descriptor : openDatabases) {
            // TODO perhaps DatabaseDescriptor should be polymorphic?
            final String databaseName = descriptor.getDatabaseName();
            if (descriptor instanceof MiniMiserDatabaseDescriptor) {
                final MiniMiserDatabaseDescriptor mmdd = (MiniMiserDatabaseDescriptor) descriptor;
                try {
                    LOGGER.info("Closing database '" + databaseName + "'");
                    mmdd.getDatabase().close();
                    LOGGER.info("Database '" + databaseName + "' closed");
                } catch (final DataAccessException dae) {
                    LOGGER.warn("Could not close database '" + databaseName + "': " + dae.getMessage(), dae);
                    // TODO pass main frame in here
                    ProblemDialog.reportProblem(null, "while closing the '" + databaseName + "' database", dae);
                }
            } else {
                LOGGER.error("Closing a non-MiniMiserDatabaseDescriptor");
            }
        }
        LOGGER.info("Open databases closed");
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing
    }
}
