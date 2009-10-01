/**
 * 
 */
package uk.me.gumbley.minimiser.migrator;

import java.util.List;

import uk.me.gumbley.minimiser.plugin.AbstractPlugin;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;

/**
 * An application other than the usual one.
 *  
 * @author matt
 *
 */
public final class DatabaseMigrationOtherAppPlugin extends AbstractPlugin implements
        ApplicationPlugin {

    /**
     * 
     */
    public DatabaseMigrationOtherAppPlugin() {
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
        return "MysteriousOtherApp"; // different to the others in these tests
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
        return "1.1"; // note that this is stored also, and tested
        // for increase on upgrade, although it's only for info
        // - nothing's done with it, unlike the schema version
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }
}
