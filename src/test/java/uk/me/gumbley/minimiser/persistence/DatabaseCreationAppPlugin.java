/**
 * 
 */
package uk.me.gumbley.minimiser.persistence;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.plugin.AbstractPlugin;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.plugin.facade.newdatabase.NewDatabaseCreation;
import uk.me.gumbley.minimiser.plugin.facade.newdatabase.NewDatabaseCreationFacade;

/**
 * @author matt
 *
 */
public final class DatabaseCreationAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, NewDatabaseCreation {
    private boolean mCreateDatabaseCalled = false;
    private boolean mPopulateDatabaseCalled = false;
    private boolean mPopulatePluginPropertiesOK = false;
    private boolean mCreatePluginPropertiesOK = false;
    private final NewDatabaseCreationFacade mNewDatabaseCreationFacade;
    
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
}
