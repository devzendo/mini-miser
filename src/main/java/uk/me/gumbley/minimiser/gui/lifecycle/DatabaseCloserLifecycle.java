package uk.me.gumbley.minimiser.gui.lifecycle;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.util.DatabasePairEncapsulator;

/**
 * A Lifecycle that records all open databases in prefs, then closes them all.
 * 
 * @author matt
 *
 */
public final class DatabaseCloserLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseCloserLifecycle.class);
    private final OpenDatabaseList openDatabaseList;
    private final Prefs prefs;

    /**
     * We need to store the open database list so we know what to close on
     * shutdown.
     * @param openList the OpenDatabaseList
     * @param prefstore the prefs store
     */
    public DatabaseCloserLifecycle(final OpenDatabaseList openList, final Prefs prefstore) {
        this.openDatabaseList = openList;
        this.prefs = prefstore;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        persistOpenDatabaseList();
        closeDatabases();
    }

    private void persistOpenDatabaseList() {
        LOGGER.info("Saving list of open databases");
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
        for (int i = 0; i < openDatabases.size(); i++) {
            final DatabaseDescriptor descriptor = openDatabases.get(i);
            listAsStringPaths.add(DatabasePairEncapsulator.escape(descriptor.getDatabaseName(), descriptor.getDatabasePath()));
        }
        prefs.setOpenFiles(listAsStringPaths.toArray(new String[0]));
        LOGGER.info("List of open databases saved");
    }

    private void closeDatabases() {
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
