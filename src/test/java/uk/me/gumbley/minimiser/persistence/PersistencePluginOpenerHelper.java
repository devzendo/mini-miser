package uk.me.gumbley.minimiser.persistence;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.migrator.DefaultMigrator;
import uk.me.gumbley.minimiser.migrator.Migrator;
import uk.me.gumbley.minimiser.opener.DatabaseOpenObserver;
import uk.me.gumbley.minimiser.opener.DefaultOpener;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.util.InstanceSet;

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
     * @param helper the main persistence plugin helper
     */
    public PersistencePluginOpenerHelper(final PersistencePluginHelper helper) {
        mPersistencePluginHelper = helper;
        mMigrator = new DefaultMigrator(helper.getPluginManager());
        mOpener = new DefaultOpener(helper.getAccessFactory(), mMigrator);
    }

    /**
     * Create the opener helper with a custom migrator
     * @param helper the main persistence plugin helper
     * @param migrator a custom migrator
     */
    public PersistencePluginOpenerHelper(final PersistencePluginHelper helper, final Migrator migrator) {
        mPersistencePluginHelper = helper;
        mMigrator = migrator;
        mOpener = new DefaultOpener(helper.getAccessFactory(), mMigrator);
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
