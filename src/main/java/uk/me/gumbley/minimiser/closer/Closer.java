package uk.me.gumbley.minimiser.closer;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;

/**
 * Toolkit for closing databases
 * 
 * @author matt
 *
 */
public final class Closer {
    private static final Logger LOGGER = Logger.getLogger(Closer.class);
    
    private Closer() {
        // no instances
    }

    /**
     * Close the database stored in a DatabaseDescriptor, report problems
     * if a problem occurs.
     * @param descriptor the database descriptor
     * @return true iff closed OK
     */
    public static boolean close(final DatabaseDescriptor descriptor) {
        final String databaseName = descriptor.getDatabaseName();
        try {
            final MiniMiserDatabase database = (MiniMiserDatabase) descriptor.getAttribute(AttributeIdentifier.Database);
            if (database == null) {
                LOGGER.warn("No database stored in database descriptor for database name '" + databaseName + "'");
                return false;
            }
            database.close();
            return true;
        } catch (final DataAccessException dae) {
            LOGGER.warn("Could not close database '" + databaseName + "': " + dae.getMessage(), dae);
            // TODO pass main frame in here
            ProblemDialog.reportProblem(null, "while closing the '" + databaseName + "' database", dae);
            return false;
        }
    }
}
