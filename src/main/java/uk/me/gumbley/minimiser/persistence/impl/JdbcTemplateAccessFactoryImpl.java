package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.h2.constant.ErrorCode;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistenceObservableEvent;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateVersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.pluginmanager.facade.newdatabase.NewDatabaseCreation;
import uk.me.gumbley.minimiser.pluginmanager.facade.newdatabase.NewDatabaseCreationFacade;

/**
 * An AccessFactory that uses Spring's JdbcTemplate.
 * Historical note: I used to have one that used plain
 * JDBC, but it was painful compared to this one.
 *
 * @author matt
 *
 */
public final class JdbcTemplateAccessFactoryImpl implements AccessFactory {
    private static final Logger LOGGER = Logger
            .getLogger(JdbcTemplateAccessFactoryImpl.class);
    private static final String[] CREATION_DDL_STRINGS = new String[] {
        "CREATE TABLE Versions("
                + "plugin VARCHAR(40),"
                + "entity VARCHAR(40),"
                + "version VARCHAR(40)"
                + ")",
        "CREATE SEQUENCE Sequence START WITH 1 INCREMENT BY 1",
                
    };
    private static final int STATIC_CREATION_STEPS = 4;
    
    private static final Observer<PersistenceObservableEvent> IGNORING_LISTENER = new Observer<PersistenceObservableEvent>() {
        public void eventOccurred(final PersistenceObservableEvent observableEvent) {
            // do nothing
        }
    };
    private final PluginManager mPluginManager;
    
    /**
     * Construct a JDBC Template Access factory, given the
     * PluginManager so that the versions of any plugins can be
     * stored in any created / migrated databases.
     * @param pluginManager the plugin manager
     */
    public JdbcTemplateAccessFactoryImpl(final PluginManager pluginManager) {
        mPluginManager = pluginManager;
    }

    private class DatabaseSetup {
        private final String dbPassword;
        private final String dbPath;
        private String dbURL;
        private final SingleConnectionDataSource dataSource;
        private final SimpleJdbcTemplate jdbcTemplate;
        public DatabaseSetup(final String databasePath,
                final String password,
                final boolean allowCreate,
                final Observer<PersistenceObservableEvent> observer) {
            LOGGER.debug("Validating arguments");
            if (databasePath == null) {
                throw new DataAccessResourceFailureException("Null database path");
            }
            dbPath = databasePath.trim();
            if (dbPath.length() == 0) {
                throw new DataAccessResourceFailureException(String.format("Incorrect database path '%s'", databasePath));
            }
            dbPassword = (password == null) ? "" : password;
            dbURL = dbPassword.length() == 0 ?
                    String.format("jdbc:h2:%s", dbPath) :
                    String.format("jdbc:h2:%s;CIPHER=AES", dbPath);
            if (!allowCreate) {
                dbURL += ";IFEXISTS=TRUE";
            }

            observer.eventOccurred(new PersistenceObservableEvent("Preparing database connectivity"));
            LOGGER.debug("Obtaining data source bean");
            final String driverClassName = "org.h2.Driver";
            final String userName = "sa";
            final boolean suppressClose = false;
            dataSource = new SingleConnectionDataSource(driverClassName,
                dbURL, userName, dbPassword + " userpwd", suppressClose); 
            LOGGER.debug("DataSource is " + dataSource);

            // TODO Spring lossage: I'd use a SimpleJdbcTemplate here, but it
            // doesn't support getDataSource() so we can't close programmatically.
            // See http://forum.springframework.org/archive/index.php/t-9704.html
            observer.eventOccurred(new PersistenceObservableEvent("Opening database"));
            LOGGER.debug("Obtaining SimpleJdbcTemplate");
            jdbcTemplate = new SimpleJdbcTemplate(dataSource);
            observer.eventOccurred(new PersistenceObservableEvent("Database opened"));
            LOGGER.debug("Database setup done");
        }
        public String getDbPassword() {
            return dbPassword;
        }
        public String getDbPath() {
            return dbPath;
        }
        public String getDbURL() {
            return dbURL;
        }
        public SimpleJdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }
        public SingleConnectionDataSource getDataSource() {
            return dataSource;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public MiniMiserDAOFactory openDatabase(final String databasePath, final String password) {
        final DatabaseSetup dbSetup = new DatabaseSetup(databasePath, password, false, IGNORING_LISTENER);
        // Possible Spring bug: if the database isn't there, it doesn't throw
        // an (unchecked) exception. - it does detect it and logs voluminously,
        // but then doesn't pass the error on to me.
        // Looks like a 90013 (DATABASE_HOT_FOUND_1) isn't mapped by the default
        // Spring sql-error-codes.xml.
        // So, I have to check myself. (Obviating one of the reasons I chose Spring!)
        try {
            // This'll throw if the db doesn't exist.
            final boolean closed = dbSetup.getDataSource().getConnection().isClosed();
            LOGGER.debug("db is initially closed? " + closed);
        } catch (final SQLException e) {
            switch (e.getErrorCode()) {
                case ErrorCode.DATABASE_NOT_FOUND_1:
                    final String dbnfMessage = String.format("Database at %s not found", databasePath);
                    LOGGER.debug(dbnfMessage);
                    throw new DataAccessResourceFailureException(dbnfMessage);
                case ErrorCode.FILE_ENCRYPTION_ERROR_1:
                    final String feeMessage = String.format("Bad password opening database at %s", databasePath);
                    LOGGER.debug(feeMessage);
                    throw new BadPasswordException(feeMessage);
                default:
                    LOGGER.warn("SQLException from isClosed", e);
                    // Assume that anything that goes wrong here is bad...
                    throw new org.springframework.jdbc.UncategorizedSQLException(
                        String.format("Could not open database - SQL Error Code %d",
                            e.getErrorCode()), "", e);
            }
        }
        LOGGER.debug("Creating new JdbcTemplateMigratableDatabaseImpl");
        return new JdbcTemplateMiniMiserDatabaseImpl(dbSetup.getDbURL(), dbSetup.getDbPath(), dbSetup.getJdbcTemplate(), dbSetup.getDataSource());
    }

    /**
     * {@inheritDoc}
     */
    public MiniMiserDAOFactory createDatabase(final String databasePath, final String password) {
        return createDatabase(databasePath, password, IGNORING_LISTENER, null);
    }

    /**
     * {@inheritDoc}
     */
    public MiniMiserDAOFactory createDatabase(
            final String databasePath,
            final String password,
            final Observer<PersistenceObservableEvent> observer,
            final Map<String, Object> pluginProperties) {
        LOGGER.debug("Creating database with path '" + databasePath + "' and password '" + password + "'");
        // Don't forget to adjust STATIC_CREATION_STEPS if the creation steps change.
        // create the database
        final DatabaseSetup dbSetup = new DatabaseSetup(databasePath, password, true, observer);
        try {
            if (dbSetup.getDataSource().getConnection().isClosed()) {
                LOGGER.warn("DataSource/Connection reports connection closed");
                throw new DataAccessResourceFailureException(
                    "Database closed when should be open");
            }
        } catch (final SQLException e) {
            throw new DataAccessResourceFailureException(
                String.format("Database closed when should be open - SQL Error Code %d",
                e.getErrorCode()), e);
        }
        createTables(dbSetup, observer, pluginProperties);
        populateTables(dbSetup, observer, pluginProperties);
        final JdbcTemplateMiniMiserDatabaseImpl templateImpl =
            new JdbcTemplateMiniMiserDatabaseImpl(
                dbSetup.getDbURL(),
                dbSetup.getDbPath(),
                dbSetup.getJdbcTemplate(),
                dbSetup.getDataSource());
        observer.eventOccurred(new PersistenceObservableEvent("Database creation complete"));
        return templateImpl;
    }

    // TODO move this to VersionsDao?
    private void createTables(
            final DatabaseSetup dbDetails, 
            final Observer<PersistenceObservableEvent> observer, 
            final Map<String, Object> pluginProperties) {
        final SimpleJdbcTemplate jdbcTemplate = dbDetails.getJdbcTemplate();
        for (int i = 0; i < CREATION_DDL_STRINGS.length; i++) {
            observer.eventOccurred(new PersistenceObservableEvent("Creating table " + (i + 1) + " of " + CREATION_DDL_STRINGS.length));
            jdbcTemplate.getJdbcOperations().execute(CREATION_DDL_STRINGS[i]);
        }
        // Now let the plugins loose
        final List<NewDatabaseCreation> newDatabaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        for (final NewDatabaseCreation newDatabaseCreation : newDatabaseCreationPlugins) {
            final NewDatabaseCreationFacade newDatabaseCreationFacade = newDatabaseCreation.getNewDatabaseCreationFacade();
            if (newDatabaseCreationFacade != null) {
                LOGGER.debug("Plugin " + newDatabaseCreation.getClass().getName() + " creating database");
                newDatabaseCreationFacade.createDatabase(dbDetails.getJdbcTemplate(),
                    dbDetails.getDataSource(), observer, pluginProperties);
            }
        }
    }
    
    private void populateTables(
            final DatabaseSetup dbDetails,
            final Observer<PersistenceObservableEvent> observer,
            final Map<String, Object> pluginProperties) {
        // Don't forget to adjust POPULATION_STEPS when we add steps to
        // the population.
        // TODO get this from Spring when we have a factory bean that can
        // create a JdbcTemplate from a programatically created
        // DataSource.
        final VersionDao versionDao = new JdbcTemplateVersionDao(dbDetails.getJdbcTemplate());
        for (final Plugin plugin : mPluginManager.getPlugins()) {
            observer.eventOccurred(new PersistenceObservableEvent("Populating table 1 of 1 for " + plugin.getName() + " plugin"));
            final String pluginName = plugin.getName();
            final Version schemaVersion = new Version(pluginName, VersionableEntity.SCHEMA_VERSION, plugin.getSchemaVersion());
            versionDao.persistVersion(schemaVersion);
            final Version appVersion = new Version(pluginName, VersionableEntity.APPLICATION_VERSION, plugin.getVersion());
            versionDao.persistVersion(appVersion);
        }
        // Now let the plugins loose
        final List<NewDatabaseCreation> newDatabaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        for (final NewDatabaseCreation newDatabaseCreation : newDatabaseCreationPlugins) {
            final NewDatabaseCreationFacade newDatabaseCreationFacade = newDatabaseCreation.getNewDatabaseCreationFacade();
            if (newDatabaseCreationFacade != null) {
                LOGGER.debug("Plugin " + newDatabaseCreation.getClass().getName() + " populating database");
                newDatabaseCreationFacade.populateDatabase(dbDetails.getJdbcTemplate(),
                    dbDetails.getDataSource(), observer, pluginProperties);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfDatabaseCreationSteps(final Map<String, Object> pluginProperties) {
        final int staticSize = CREATION_DDL_STRINGS.length
            + mPluginManager.getPlugins().size() // each plugin contributes version info
            + STATIC_CREATION_STEPS;
        final int dynamicSize = getNumberOfDatabaseCreationStepsFromPlugins(pluginProperties);
        return staticSize + dynamicSize;
    }

    private int getNumberOfDatabaseCreationStepsFromPlugins(final Map<String, Object> pluginProperties) {
        int count = 0;
        final List<NewDatabaseCreation> newDatabaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        for (final NewDatabaseCreation newDatabaseCreation : newDatabaseCreationPlugins) {
            final NewDatabaseCreationFacade newDatabaseCreationFacade = newDatabaseCreation.getNewDatabaseCreationFacade();
            if (newDatabaseCreationFacade != null) {
                count += newDatabaseCreationFacade.getNumberOfDatabaseCreationSteps(pluginProperties);
            }
        }
        return count;
    }
}

