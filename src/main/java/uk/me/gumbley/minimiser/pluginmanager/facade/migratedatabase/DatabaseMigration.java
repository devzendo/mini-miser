package uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase;

/**
 * Plugins that need their databases upgrading to the latest
 * schema upon opening implement this and provide the facade.
 * <p>
 * It is highly likely that such plugins would also implement
 * NewDatabaseCreation - as they would want to create this data.
 * 
 * @author matt
 *
 */
public interface DatabaseMigration {
    /**
     * @return an instance of the facade; this can be called
     * several times during opening; perhaps this could be a
     * single instance that's returned?
     */
    DatabaseMigrationFacade getDatabaseMigrationFacade();
}
