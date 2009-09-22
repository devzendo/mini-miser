package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.migrator.Migrator;
import uk.me.gumbley.minimiser.migrator.Migrator.MigrationVersion;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.util.InstancePair;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Default implementation of Opener.
 * 
 * @author matt
 *
 */
public final class DefaultOpener implements Opener {
    private static final Logger LOGGER = Logger.getLogger(DefaultOpener.class);
    
    private final AccessFactory mAccess;
    private final Migrator mMigrator;
    private final ObserverList<DatabaseOpenEvent> mObserverList;

    /**
     * Construct the Opener.
     * @param accessFactory the mAccess factory used for accessing
     * databases
     * @param migrator the migrator for performing migrations
     * upon open, if these are required
     * 
     */
    public DefaultOpener(
            final AccessFactory accessFactory,
            final Migrator migrator) {
        this.mAccess = accessFactory;
        mMigrator = migrator;
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
                final InstanceSet<DAOFactory> daoFactories = mAccess.openDatabase(pathToDatabase, dbPassword);
                final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
                LOGGER.info("Opened OK");
                
                if (!processMigrationOk(dbName, openerAdapter, daoFactories)) {
                    openerAdapter.stopOpening();
                    LOGGER.warn("Migration rejection or failure terminated open");
                    closeAfterMigrationTermination(miniMiserDAOFactory);
                    return null;
                }
                
                openerAdapter.reportProgress(ProgressStage.OPENED, "Opened '" + dbName + "' OK");
                openerAdapter.stopOpening();
                
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(dbName, pathToDatabase);

                // Add the MiniMiserDAOFactory and other plugins'
                // DAOFactories to the DatabaseDescriptor
                for (final InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
                    databaseDescriptor.setDAOFactory(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
                }
                
                mObserverList.eventOccurred(new DatabaseOpenEvent(databaseDescriptor));

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

    private void closeAfterMigrationTermination(
            final MiniMiserDAOFactory miniMiserDAOFactory) {
        try {
            LOGGER.info("Closing due to migration rejection/failure");
            // since we return null, so the
            // MiniMiserDAOFactory isn't passed out,
            // closure can't be directly
            // verified in a test, so we test
            // by looking for an absence of a
            // lock file
           miniMiserDAOFactory.close();
        } catch (final DataAccessException dae) {
            LOGGER.warn("Data access exception closing database after migration rejection: " + dae.getMessage(), dae);
        }
    }
}
