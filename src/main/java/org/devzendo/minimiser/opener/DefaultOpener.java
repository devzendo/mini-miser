package org.devzendo.minimiser.opener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.migrator.Migrator;
import org.devzendo.minimiser.migrator.Migrator.MigrationVersion;
import org.devzendo.minimiser.opener.OpenerAdapter.ProgressStage;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.BadPasswordException;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstancePair;
import org.devzendo.minimiser.util.InstanceSet;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * Default implementation of Opener.
 * 
 * @author matt
 *
 */
public final class DefaultOpener implements Opener {
    private static final Logger LOGGER = Logger.getLogger(DefaultOpener.class);
    
    private final AccessFactory mAccessFactory;
    private final Migrator mMigrator;
    private final PluginManager mPluginManager;
    private final ObserverList<DatabaseOpenEvent> mObserverList;

    /**
     * Construct the Opener.
     * @param accessFactory the mAccessFactory factory used for accessing
     * databases
     * @param migrator the migrator for performing migrations
     * upon open, if these are required
     * @param pluginManager the plugin manager for obtaining
     * the current application plugin
     * 
     */
    public DefaultOpener(
            final AccessFactory accessFactory,
            final Migrator migrator,
            final PluginManager pluginManager) {
        mAccessFactory = accessFactory;
        mMigrator = migrator;
        mPluginManager = pluginManager;
        mObserverList = new ObserverList<DatabaseOpenEvent>();
    }
    
    /**
     * {@inheritDoc}
     */
    
    public void addDatabaseOpenObserver(final Observer<DatabaseOpenEvent> observer) {
        mObserverList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter originalOpenerAdapter) {
        final OpenerAdapter openerAdapter = new LoggingDecoratorOpenerAdapter(originalOpenerAdapter);
        openerAdapter.startOpening();
        LOGGER.info("Opening database '" + dbName + "' from path '" + pathToDatabase + "'");
        openerAdapter.reportProgress(ProgressStage.STARTING, "Starting to open '" + dbName + "'");

        // Try at first with an empty password - if we get a BadPasswordException,
        // prompt for password and retry.
        String dbPassword = "";
        String tryingToOpenMessage = "Opening database '" + dbName + "'";
        while (true) {
            try {
                openerAdapter.reportProgress(ProgressStage.OPENING, tryingToOpenMessage);
                final InstanceSet<DAOFactory> daoFactories = mAccessFactory.openDatabase(pathToDatabase, dbPassword);
                final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
                LOGGER.info("Database opened - checking plugin details");
                
                // Did the app given in the current plugins create this database?
                if (isCreatedByOtherApplication(dbName, openerAdapter, miniMiserDAOFactory.getVersionDao())) {
                    openerAdapter.stopOpening();
                    LOGGER.warn("Detection of other application's database terminated open");
                    closeSinceOpenCannotProceed(miniMiserDAOFactory);
                    return null;
                }
                
                // If migration's needed, do it... did it go OK?
                if (!processMigrationOk(dbName, openerAdapter, daoFactories)) {
                    openerAdapter.stopOpening();
                    LOGGER.warn("Migration rejection or failure terminated open");
                    closeSinceOpenCannotProceed(miniMiserDAOFactory);
                    return null;
                }
                
                // Everything went OK.
                openerAdapter.reportProgress(ProgressStage.OPENED, "Opened '" + dbName + "' OK");
                openerAdapter.stopOpening();
                
                emitDatabaseDescriptor(dbName, pathToDatabase, daoFactories);

                return daoFactories;
                
            } catch (final BadPasswordException bad) {
                LOGGER.warn("Bad password: " + bad.getMessage());
                openerAdapter.reportProgress(ProgressStage.PASSWORD_REQUIRED, "Password required for '" + dbName + "'");
                dbPassword = openerAdapter.requestPassword();
                if (dbPassword == null || dbPassword.equals("")) {
                    LOGGER.info("Open of encrypted database cancelled");
                    openerAdapter.reportProgress(ProgressStage.PASSWORD_CANCELLED, "Open of '" + dbName + "' cancelled");
                    openerAdapter.stopOpening();
                    return null;
                }
                
                // Change the progress message, second time round...
                tryingToOpenMessage = "Trying to open database '" + dbName + "'";
            } catch (final DataAccessResourceFailureException darfe) {
                LOGGER.warn("Could not open database: " + darfe.getMessage());
                openerAdapter.reportProgress(ProgressStage.NOT_PRESENT, "Database '" + dbName + "' not found");
                openerAdapter.databaseNotFound(darfe);
                openerAdapter.stopOpening();
                return null;
                
            } catch (final DataAccessException dae) {
                LOGGER.warn("Data access exception opening database: " + dae.getMessage(), dae);
                openerAdapter.reportProgress(ProgressStage.OPEN_FAILED, "Open of '" + dbName + "' failed");
                openerAdapter.seriousProblemOccurred(dae);
                openerAdapter.stopOpening();
                return null;
            }
        }
    }

    private void emitDatabaseDescriptor(
            final String dbName,
            final String pathToDatabase,
            final InstanceSet<DAOFactory> daoFactories) {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(dbName, pathToDatabase);

        // Add the MiniMiserDAOFactory and other plugins'
        // DAOFactories to the DatabaseDescriptor
        for (final InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
            databaseDescriptor.setDAOFactory(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
        }
        
        mObserverList.eventOccurred(new DatabaseOpenEvent(databaseDescriptor));
    }

    private boolean isCreatedByOtherApplication(
            final String dbName,
            final OpenerAdapter openerAdapter,
            final VersionsDao versionsDao) {
        final ApplicationPlugin applicationPlugin = mPluginManager.getApplicationPlugin();
        if (applicationPlugin == null) {
            LOGGER.warn("There is no application plugin defined - cannot check which application created this database");
            openerAdapter.reportProgress(ProgressStage.NO_APPLICATION_PLUGIN, "No application plugin available");
            openerAdapter.noApplicationPluginAvailable();
            return true;
        }
        LOGGER.debug(" the app plugin is " + applicationPlugin);
        LOGGER.debug("therer are " + mPluginManager.getPlugins().size() + " plugins");
        LOGGER.info("Checking that the '" + dbName + "' database was created by the '" + applicationPlugin.getName() + "' application");
        if (versionsDao.exists(applicationPlugin.getName(), VersionableEntity.APPLICATION_VERSION)) {
            final Version storedApplicationVersion = versionsDao.findVersion(applicationPlugin.getName(), VersionableEntity.APPLICATION_VERSION);
            if (storedApplicationVersion.isApplication()) {
                LOGGER.info("Yes: there is an application plugin version stored for '" + applicationPlugin.getName() + "'");
                return false;
            } else {
                LOGGER.warn("No: there is a plugin version stored for '" + applicationPlugin.getName() + "' but it is not an application");
                // TODO test that this should give a response
            }
        } else {
            LOGGER.warn("No: there is no version stored for the '" + applicationPlugin.getName() + "' plugin");
        }
        openerAdapter.reportProgress(ProgressStage.OTHER_APPLICATION_DATABASE, "Not created by this application");
        openerAdapter.createdByOtherApplication();
        return true;
    }

    private boolean processMigrationOk(
            final String dbName,
            final OpenerAdapter openerAdapter,
            final InstanceSet<DAOFactory> daoFactories) {
        final MigrationVersion migrationVersion = mMigrator.requiresMigration(daoFactories);
        switch (migrationVersion) {
            case OLD:
                LOGGER.info("Migration is required; prompting...");
                openerAdapter.reportProgress(ProgressStage.MIGRATION_REQUIRED, "This database needs updating");
                final boolean migrationAccepted = openerAdapter.requestMigration();
                LOGGER.info("Request for migration was " + (migrationAccepted ? "accepted" : "denied"));
                if (migrationAccepted) {
                    final TransactionTemplate transaction =
                        daoFactories.getInstanceOf(MiniMiserDAOFactory.class).
                        getSQLAccess().createTransactionTemplate();
                    try {
                        transaction.execute(new TransactionCallback() {
                            public Object doInTransaction(final TransactionStatus ts) {
                                try {
                                    openerAdapter.reportProgress(ProgressStage.MIGRATING, "Updating database");
                                    mMigrator.migrate(daoFactories);
                                    openerAdapter.reportProgress(ProgressStage.MIGRATED, "Database updated");
                                    // Template now commits transaction
                                    return null;
                                } catch (final DataAccessException dae) {
                                    LOGGER.warn("Migration failed: " + dae.getMessage(), dae);
                                    openerAdapter.reportProgress(ProgressStage.MIGRATION_FAILED, "Update failed");
                                    openerAdapter.migrationFailed(dae);
                                    throw dae;
                                    // Template now rolls back transaction
                                }
                            }
                        });
                        return true;
                    } catch (final DataAccessException dae) {
                        // The exception has already been logged.
                        return false;
                    }
                } else {
                    openerAdapter.reportProgress(ProgressStage.MIGRATION_REJECTED, "Migration of '" + dbName + "' rejected");
                    return false;
                }
            case CURRENT:
                LOGGER.info("No migration required");
                return true;
            case FUTURE:
                LOGGER.warn("Migration is not possible since this database is newer than the plugins");
                openerAdapter.reportProgress(ProgressStage.MIGRATION_NOT_POSSIBLE, "This database was created by more recent software");
                openerAdapter.migrationNotPossible();
                return false;
            default:
                throw new IllegalStateException("Migrator returned unknown MigrationVersion: " + migrationVersion);
            
        }
    }

    private void closeSinceOpenCannotProceed(
            final MiniMiserDAOFactory miniMiserDAOFactory) {
        try {
            // since we return null, so the
            // MiniMiserDAOFactory isn't passed out,
            // closure can't be directly
            // verified in a test, so we test
            // by looking for an absence of a
            // lock file
           miniMiserDAOFactory.close();
        } catch (final DataAccessException dae) {
            LOGGER.warn("Data access exception closing database after detecting open cannot proceed: " + dae.getMessage(), dae);
        }
    }
}
