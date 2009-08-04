/**
 * 
 */
package uk.me.gumbley.minimiser.persistence;

import java.util.List;

import uk.me.gumbley.minimiser.pluginmanager.AbstractPlugin;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.facade.opendatabase.DatabaseOpening;
import uk.me.gumbley.minimiser.pluginmanager.facade.opendatabase.DatabaseOpeningFacade;

/**
 * @author matt
 *
 */
public final class DatabaseOpeningAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, DatabaseOpening {
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
    public DatabaseOpeningAppPlugin() {
        mDatabaseOpeningFacade = new DatabaseOpeningFacade() {
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseOpeningFacade getDatabaseOpeningFacade() {
        return mDatabaseOpeningFacade;
    }
}
