package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A DatabaseDescriptor that also encapsulates an open/created MiniMiserDatabase 
 * @author matt
 *
 */
public final class MiniMiserDatabaseDescriptor extends DatabaseDescriptor {

    private final MiniMiserDatabase miniMiserDatabase;

    /**
     * Construct.
     * @param databaseName the database name
     * @param databaseFullPath the full path to the database
     * @param database the database interface
     */
    public MiniMiserDatabaseDescriptor(final String databaseName, final String databaseFullPath, final MiniMiserDatabase database) {
        super(databaseName, databaseFullPath);
        miniMiserDatabase = database;
    }

    /**
     * Obtain the database interface
     * @return the database interface
     */
    public MiniMiserDatabase getDatabase() {
        return miniMiserDatabase;
    }
}
