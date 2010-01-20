package org.devzendo.minimiser.plugin.facade.migratedatabase;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

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
     * 
     * @param dataSource the DataSource, for low-level access to
     * the database
     * @param simpleJdbcTemplate the Spring SimpleJdbcTemplate, for
     * easier access to the database atop JDBC
     * @param currentSchemaVersion this plugin's current database
     * schema version
     * @throws DataAccessException on migration failure
     */
    void migrateSchema(
            DataSource dataSource,
            SimpleJdbcTemplate simpleJdbcTemplate,
            String currentSchemaVersion) throws DataAccessException;
}
