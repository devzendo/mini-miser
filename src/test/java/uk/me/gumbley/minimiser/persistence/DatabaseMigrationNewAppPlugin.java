/**
 * 
 */
package uk.me.gumbley.minimiser.persistence;

import java.util.List;

import uk.me.gumbley.minimiser.pluginmanager.AbstractPlugin;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigration;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigrationFacade;

/**
 * @author matt
 *
 */
public final class DatabaseMigrationNewAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, DatabaseMigration {
    private final DatabaseMigrationFacade mDatabaseMigrationFacade;

    private boolean mMigrateCalled;
    
    /**
     * instantiate all the facades
     */
    public DatabaseMigrationNewAppPlugin() {
        mDatabaseMigrationFacade = new DatabaseMigrationFacade() {
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
        return "DatabaseMigrationAppPlugin"; // must be same name between old and new
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "2.0"; // the new schema
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0"; // ideally this should increase as well, but
        // migration is only concerned with schema versions 
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * @return true iff the migration methods have been called
     */
    public boolean allMigrationMethodsCalled() {
        return mMigrateCalled;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseMigrationFacade getDatabaseMigrationFacade() {
        return mDatabaseMigrationFacade;
    }
}
