/**
 * 
 */
package org.devzendo.minimiser.persistence;

import java.util.List;

import org.devzendo.minimiser.plugin.AbstractPlugin;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpeningFacade;
import org.devzendo.minimiser.util.InstancePair;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * @author matt
 *
 */
public final class DatabaseOpeningAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, DatabaseOpening {
    private final DatabaseOpeningFacade mDatabaseOpeningFacade;

    private boolean mCreateDAOFactoryCalled;
    
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
    public DatabaseOpeningAppPlugin() {
        mDatabaseOpeningFacade = new DatabaseOpeningFacade() {

            public InstancePair<DAOFactory> createDAOFactory(
                    final SimpleJdbcTemplate jdbcTemplate,
                    final SingleConnectionDataSource dataSource) {
                mCreateDAOFactoryCalled = true;
                final DatabaseOpeningDAOFactory daoFactory = new DatabaseOpeningDAOFactory();
                return new InstancePair<DAOFactory>(DatabaseOpeningDAOFactory.class, daoFactory);
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
        return "DatabaseOpeningAppPlugin";
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
     * @return true iff the open methods have been called
     */
    public boolean allOpeningMethodsCalled() {
        return mCreateDAOFactoryCalled;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseOpeningFacade getDatabaseOpeningFacade() {
        return mDatabaseOpeningFacade;
    }
}
