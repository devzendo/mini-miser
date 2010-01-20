package org.devzendo.minimiser.persistence;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.migrator.DefaultMigrator;
import org.devzendo.minimiser.migrator.Migrator;
import org.devzendo.minimiser.opener.DatabaseOpenObserver;
import org.devzendo.minimiser.opener.DefaultOpener;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.util.InstanceSet;


/**
 * A helper that adds the high-level 'workflow' database opening
 * features of the Opener and Migrator to the low-level
 * PersistencePluginHelper.
 * 
 * @author matt
 *
 */
public final class PersistencePluginOpenerHelper {
    private static final Logger LOGGER = Logger
            .getLogger(PersistencePluginOpenerHelper.class);
    private final PersistencePluginHelper mPersistencePluginHelper;
    private final Migrator mMigrator;
    private final Opener mOpener;

    /**
     * Create the opener helper with the standard migrator
     * @param pluginHelper the main plugin helper
     * @param persistencePluginHelper the main persistence plugin helper
     */
    public PersistencePluginOpenerHelper(
            final PluginHelper pluginHelper,
            final PersistencePluginHelper persistencePluginHelper) {
        mPersistencePluginHelper = persistencePluginHelper;
        mMigrator = new DefaultMigrator(pluginHelper.getPluginManager());
        mOpener = new DefaultOpener(persistencePluginHelper.getAccessFactory(), mMigrator, pluginHelper.getPluginManager());
    }

    /**
     * Create the opener helper with a custom migrator
     * @param pluginHelper the main plugin helper
     * @param persistencePluginHelper the main persistence plugin helper
     * @param migrator a custom migrator
     */
    public PersistencePluginOpenerHelper(
            final PluginHelper pluginHelper,
            final PersistencePluginHelper persistencePluginHelper,
            final Migrator migrator) {
        mPersistencePluginHelper = persistencePluginHelper;
        mMigrator = migrator;
        mOpener = new DefaultOpener(persistencePluginHelper.getAccessFactory(), mMigrator, pluginHelper.getPluginManager());
    }

    /**
     * Open a database given its name, with
     * an OpenerAdapter to handle the opening workflow.
     * Typically called from a @Before method.
     * @param dbName the name of the database
     * @param openerAdapter the OpenerAdapter.
     * @return an InstanceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> openDatabase(final String dbName, final OpenerAdapter openerAdapter) {
        // in case the open fails, we still need to delete the database on exit
        mPersistencePluginHelper.addDatabaseToDelete(dbName);

        final String dbDirPlusDbName = mPersistencePluginHelper.getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Opening database dbName = %s, dbDirPlusDbName = %s", dbName, dbDirPlusDbName));
        final InstanceSet<DAOFactory> daoFactorySet = mOpener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
        if (daoFactorySet != null) {
            mPersistencePluginHelper.addDatabaseToDelete(dbName, daoFactorySet.getInstanceOf(MiniMiserDAOFactory.class));
        }
        return daoFactorySet;
    }

    /**
     * Attempt to open a database that does not exist, and so
     * does not need to be tidied up, given its name, with
     * an OpenerAdapter to handle the opening workflow.
     * Typically called from a @Before method.
     * @param dbName the name of the database
     * @param openerAdapter the OpenerAdapter.
     * @return an InstanceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> openNonExistantDatabase(final String dbName, final OpenerAdapter openerAdapter) {
        final String dbDirPlusDbName = mPersistencePluginHelper.getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Opening nonexistant database dbName = %s, dbDirPlusDbName = %s", dbName, dbDirPlusDbName));
        return mOpener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
    }

    /**
     * For opening databases using the opener, a DatabaseOpenObserver
     * can be attached. 
     * @param obs the observer
     */
    public void addDatabaseOpenObserver(final DatabaseOpenObserver obs) {
        mOpener.addDatabaseOpenObserver(obs);
    }
}
