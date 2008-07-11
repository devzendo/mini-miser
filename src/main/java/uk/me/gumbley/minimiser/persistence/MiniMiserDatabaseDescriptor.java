package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;

/**
 * A DatabaseDescriptor that also encapsulates an open/created MiniMiserDatabase 
 * @author matt
 *
 */
public final class MiniMiserDatabaseDescriptor extends DatabaseDescriptor {

    private final MiniMiserDatabase miniMiserDatabase;

    /**
     * Construct.
     * @param databaseName the databse name
     * @param database the database interface
     */
    public MiniMiserDatabaseDescriptor(final String databaseName, final MiniMiserDatabase database) {
        super(databaseName);
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
