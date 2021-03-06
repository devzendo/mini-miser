/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.persistence.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.BadPasswordException;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.PersistenceObservableEvent;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.dao.impl.JdbcTemplateVersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseCreation;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseCreationFacade;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpeningFacade;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstancePair;
import org.devzendo.minimiser.util.InstanceSet;
import org.h2.constant.ErrorCode;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


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
                + "isapplication BOOLEAN,"
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

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> openDatabase(final String databasePath, final String password) {
        LOGGER.info("Opening database at '" + databasePath + "'");
        final JdbcTemplateAccessFactoryDatabaseBuilder dbSetup =
            new JdbcTemplateAccessFactoryDatabaseBuilder(
                databasePath, password, false, IGNORING_LISTENER);
        // Possible Spring bug: if the database isn't there, it doesn't throw
        // an (unchecked) exception. - it does detect it and logs voluminously,
        // but then doesn't pass the error on to me.
        // Looks like a 90013 (DATABASE_NOT_FOUND_1) isn't mapped by the default
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

        final InstanceSet<DAOFactory> daoFactories = new InstanceSet<DAOFactory>();

        LOGGER.debug("Creating new JdbcTemplateMiniMiserDAOFactoryImpl");
        final JdbcTemplateMiniMiserDAOFactoryImpl miniMiserDAOFactory =
            new JdbcTemplateMiniMiserDAOFactoryImpl(
                dbSetup.getDbURL(), dbSetup.getDbPath(),
                dbSetup.getJdbcTemplate(), dbSetup.getDataSource(),
                mPluginManager);
        daoFactories.addInstance(MiniMiserDAOFactory.class, miniMiserDAOFactory);

        createDatabaseOpeningFacadeDAOFactories(dbSetup, daoFactories);

        return daoFactories;
    }

    private void createDatabaseOpeningFacadeDAOFactories(
            final JdbcTemplateAccessFactoryDatabaseBuilder dbSetup,
            final InstanceSet<DAOFactory> daoFactories) {
        // Now let the plugins create their own DAOFactory objects.
        final List<DatabaseOpening> databaseOpeningPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseOpening.class);
        for (final DatabaseOpening databaseOpeningPlugin : databaseOpeningPlugins) {
            final DatabaseOpeningFacade databaseOpeningFacade = databaseOpeningPlugin.getDatabaseOpeningFacade();
            if (databaseOpeningFacade == null) {
                LOGGER.warn(
                    "DatabaseOpening class "
                    + databaseOpeningPlugin.getClass().getName()
                    + " returned a null facade - ignoring");
            } else {
                LOGGER.debug("Plugin " + databaseOpeningPlugin.getClass().getName() + " creating DAOFactory for database");
                final InstancePair<DAOFactory> daoFactoryPair =
                    databaseOpeningFacade.createDAOFactory(
                        dbSetup.getDataSource(),
                        dbSetup.getJdbcTemplate());
                if (daoFactoryPair == null) {
                    LOGGER.warn(
                        "DatabaseOpeningFacade class "
                        + databaseOpeningFacade.getClass().getName()
                        + " returned a null DAOFactory - ignoring");
                } else {
                    daoFactories.addInstance(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> createDatabase(final String databasePath, final String password) {
        return createDatabase(databasePath, password, IGNORING_LISTENER, null);
    }

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> createDatabase(
            final String databasePath,
            final String password,
            final Observer<PersistenceObservableEvent> observer,
            final Map<String, Object> pluginProperties) {
        LOGGER.info("Creating database at '" + databasePath + "' and password '" + StringUtils.maskSensitiveText(password) + "'");
        LOGGER.debug("Observer is " + observer);
        LOGGER.debug("Plugin properties are " + pluginProperties);
        // Don't forget to adjust STATIC_CREATION_STEPS if the creation steps change.
        // create the database
        final JdbcTemplateAccessFactoryDatabaseBuilder dbSetup = new JdbcTemplateAccessFactoryDatabaseBuilder(databasePath, password, true, observer);
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
        final JdbcTemplateMiniMiserDAOFactoryImpl templateImpl =
            new JdbcTemplateMiniMiserDAOFactoryImpl(
                dbSetup.getDbURL(),
                dbSetup.getDbPath(),
                dbSetup.getJdbcTemplate(),
                dbSetup.getDataSource(),
                mPluginManager);
        if (observer != null) {
            observer.eventOccurred(new PersistenceObservableEvent("Database creation complete"));
        }
        final InstanceSet<DAOFactory> daoFactories = new InstanceSet<DAOFactory>();
        daoFactories.addInstance(MiniMiserDAOFactory.class, templateImpl);
        createDatabaseOpeningFacadeDAOFactories(dbSetup, daoFactories);
        return daoFactories;
    }

    private void createTables(
            final JdbcTemplateAccessFactoryDatabaseBuilder dbDetails,
            final Observer<PersistenceObservableEvent> observer,
            final Map<String, Object> pluginProperties) {
        final SimpleJdbcTemplate jdbcTemplate = dbDetails.getJdbcTemplate();
        for (int i = 0; i < CREATION_DDL_STRINGS.length; i++) {
            if (observer != null) {
                observer.eventOccurred(new PersistenceObservableEvent("Creating table " + (i + 1) + " of " + CREATION_DDL_STRINGS.length));
            }
            jdbcTemplate.getJdbcOperations().execute(CREATION_DDL_STRINGS[i]);
        }
        // Now let the plugins loose
        final List<NewDatabaseCreation> newDatabaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        LOGGER.debug("New Database Creation Plugins: " + newDatabaseCreationPlugins);
        for (final NewDatabaseCreation newDatabaseCreation : newDatabaseCreationPlugins) {
            final NewDatabaseCreationFacade newDatabaseCreationFacade = newDatabaseCreation.getNewDatabaseCreationFacade();
            if (newDatabaseCreationFacade != null) {
                LOGGER.debug("Plugin " + newDatabaseCreation.getClass().getName() + " creating database");
                newDatabaseCreationFacade.createDatabase(dbDetails.getDataSource(),
                    dbDetails.getJdbcTemplate(), observer, pluginProperties);
            }
        }
    }

    private void populateTables(
            final JdbcTemplateAccessFactoryDatabaseBuilder dbDetails,
            final Observer<PersistenceObservableEvent> observer,
            final Map<String, Object> pluginProperties) {
        // Don't forget to adjust POPULATION_STEPS when we add steps to
        // the population.
        final VersionsDao versionsDao = new JdbcTemplateVersionsDao(dbDetails.getJdbcTemplate());
        final ApplicationPlugin appPlugin = mPluginManager.getApplicationPlugin();
        for (final Plugin plugin : mPluginManager.getPlugins()) {
            if (observer != null) {
                observer.eventOccurred(new PersistenceObservableEvent("Populating table 1 of 1 for " + plugin.getName() + " plugin"));
            }
            final String pluginName = plugin.getName();
            final Version schemaVersion = new Version(
                pluginName,
                VersionableEntity.SCHEMA_VERSION,
                plugin == appPlugin,
                plugin.getSchemaVersion());
            versionsDao.persistVersion(schemaVersion);
            final Version appVersion = new Version(
                pluginName,
                VersionableEntity.PLUGIN_CODE_VERSION,
                plugin == appPlugin,
                plugin.getVersion());
            versionsDao.persistVersion(appVersion);
        }
        // Now let the plugins loose
        final List<NewDatabaseCreation> newDatabaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        for (final NewDatabaseCreation newDatabaseCreation : newDatabaseCreationPlugins) {
            final NewDatabaseCreationFacade newDatabaseCreationFacade = newDatabaseCreation.getNewDatabaseCreationFacade();
            if (newDatabaseCreationFacade == null) {
                LOGGER.warn(
                    "NewDatabaseCreation class "
                    + newDatabaseCreation.getClass().getName()
                    + " returned a null facade - ignoring");
            } else {
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
            if (newDatabaseCreationFacade == null) {
                LOGGER.warn(
                    "NewDatabaseCreation class "
                    + newDatabaseCreation.getClass().getName()
                    + " returned a null facade - ignoring");
            } else {
                count += newDatabaseCreationFacade.getNumberOfDatabaseCreationSteps(pluginProperties);
            }
        }
        return count;
    }
}

