package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

/**
 * @author matt
 *
 */
public final class AnotherAppPlugin extends AbstractPlugin implements ApplicationPlugin {
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Application";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0.0";
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
    public void shutdown() {
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
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return null;
    }
}
