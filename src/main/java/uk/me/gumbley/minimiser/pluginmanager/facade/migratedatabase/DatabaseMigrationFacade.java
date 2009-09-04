package uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase;

import org.springframework.dao.DataAccessException;

/**
 * Facade that allows plugins to upgrade their databases to the
 * latest schema upon opening.
 * 
 * @author matt
 *
 */
public interface DatabaseMigrationFacade {
    /**
     * The database schema is at a version older than that given
     * by the plugin, so migrate it to the latest version. The
     * framework will record the new version in the Versions
     * table, after migration. If the exception is thrown, the
     * entire migration will be rolled back, and the open
     * terminated.
     * @param version this plugin's current database schema
     * version
     * @throws DataAccessException on migration failure
     */
    void migrateSchema(String version) throws DataAccessException;
}
