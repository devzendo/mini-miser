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

/**
 *
 */
package org.devzendo.minimiser.persistence;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.plugin.AbstractPlugin;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseCreation;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseCreationFacade;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpeningFacade;
import org.devzendo.minimiser.util.InstancePair;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * @author matt
 *
 */
public final class DatabaseCreationAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, NewDatabaseCreation, DatabaseOpening {
    private boolean mCreateDatabaseCalled = false;
    private boolean mOpenDatabaseCalled = false;
    private boolean mPopulateDatabaseCalled = false;
    private boolean mPopulatePluginPropertiesOK = false;
    private boolean mCreatePluginPropertiesOK = false;
    private final NewDatabaseCreationFacade mNewDatabaseCreationFacade;
    private final DatabaseOpeningFacade mDatabaseOpeningFacade;

    /**
     * Expected data from the dummy wizard data
     */
    public static final String WIZARDVALUE = "WIZARDVALUE";

    /**
     * Expected data from the dummy wizard data
     */
    public static final String WIZARDKEY = "WIZARDKEY";

    /**
     * instantiate all the facades
     */
    public DatabaseCreationAppPlugin() {
        mNewDatabaseCreationFacade = new NewDatabaseCreationFacade() {

            /**
             * {@inheritDoc}
             */
            public int getNumberOfDatabaseCreationSteps(
                    final Map<String, Object> wizardInput) {
                return 2;
            }

            public void createDatabase(
                    final DataSource dataSource,
                    final SimpleJdbcTemplate jdbcTemplate,
                    final Observer<PersistenceObservableEvent> observer,
                    final Map<String, Object> pluginProperties) {
                mCreateDatabaseCalled  = true;
                if (pluginProperties.containsKey(WIZARDKEY) && pluginProperties.get(WIZARDKEY).equals(WIZARDVALUE)) {
                    mCreatePluginPropertiesOK = true;
                }
                observer.eventOccurred(new PersistenceObservableEvent("Creating DatabaseCreationAppPlugin data"));
            }

            public void populateDatabase(
                    final SimpleJdbcTemplate jdbcTemplate,
                    final SingleConnectionDataSource dataSource,
                    final Observer<PersistenceObservableEvent> observer,
                    final Map<String, Object> pluginProperties) {
                mPopulateDatabaseCalled = true;
                if (pluginProperties.containsKey(WIZARDKEY) && pluginProperties.get(WIZARDKEY).equals(WIZARDVALUE)) {
                    mPopulatePluginPropertiesOK = true;
                }
                observer.eventOccurred(new PersistenceObservableEvent("Populating DatabaseCreationAppPlugin data"));
            }
        };

        mDatabaseOpeningFacade = new DatabaseOpeningFacade() {

            public InstancePair<DAOFactory> createDAOFactory(
                    final SimpleJdbcTemplate jdbcTemplate,
                    final SingleConnectionDataSource dataSource) {
                mOpenDatabaseCalled = true;
                final DatabaseOpeningDAOFactory daoFactory = new DatabaseOpeningDAOFactory();
                final InstancePair<DAOFactory> daoFactoryPair = new InstancePair<DAOFactory>(DatabaseOpeningDAOFactory.class, daoFactory);
                return daoFactoryPair;
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "DatabaseCreationAppPlugin";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    public NewDatabaseCreationFacade getNewDatabaseCreationFacade() {
        return mNewDatabaseCreationFacade;
    }

    /**
     * @return true iff the create and populate methods have been called
     */
    public boolean allCreationMethodsCalled() {
        return mCreateDatabaseCalled && mPopulateDatabaseCalled;
    }

    /**
     * @return true iff the correct plugin property info is passed into the
     * create and populate methods.
     */
    public boolean correctPluginPropertiesPassed() {
        return mCreatePluginPropertiesOK && mPopulatePluginPropertiesOK;
    }

    /**
     * @return true iff the open method has been called
     */
    public boolean allOpeningMethodsCalled() {
        return mOpenDatabaseCalled;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseOpeningFacade getDatabaseOpeningFacade() {
        return mDatabaseOpeningFacade;
    }
}
