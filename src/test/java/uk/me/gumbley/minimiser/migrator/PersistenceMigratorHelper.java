package uk.me.gumbley.minimiser.migrator;

import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;

/**
 * Helper code for creating old, normal and future databases for
 * use in test code dealing with the Migrator.
 * 
 * @author matt
 *
 */
public final class PersistenceMigratorHelper {
    private final PersistencePluginHelper mPersistencePluginHelper;
    
    /**
     * Create the migrator helper, using the persistence plugin
     * helper.
     * @param persistencePluginHelper the persistence plugin
     * helper
     */
    public PersistenceMigratorHelper(final PersistencePluginHelper persistencePluginHelper) {
        mPersistencePluginHelper = persistencePluginHelper;
    }

    /**
     * Create a version 1.0 schema database. i.e. "old"
     * @param dbName the name of the database
     * @throws PluginException on error
     */
    public void createOldDatabase(final String dbName) throws PluginException {
        createDatabase(dbName, "old");
    }

    /**
     * Create a version 2.0 schema database. i.e. "new"
     * @param dbName the name of the database
     * @throws PluginException on error
     */
    public void createNewDatabase(final String dbName) throws PluginException {
        createDatabase(dbName, "new");
    }
    
    private void createDatabase(final String dbName, final String pluginSubName) throws PluginException {
        // need a helper separate from the main one since it needs
        // to have old plugins loaded, and the main one loads the
        // new plugins
        final PersistencePluginHelper persistencePluginHelper = new PersistencePluginHelper();
        persistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigration" + pluginSubName + "plugin.properties");
        final MiniMiserDAOFactory miniMiserDAOFactory = persistencePluginHelper.createDatabase(dbName, "").getInstanceOf(MiniMiserDAOFactory.class);
        miniMiserDAOFactory.close();
        
        // Let the main helper delete the old database 
        // - can't use this DAOFactory to close though
        mPersistencePluginHelper.addDatabaseToDelete(dbName);
    }
}
