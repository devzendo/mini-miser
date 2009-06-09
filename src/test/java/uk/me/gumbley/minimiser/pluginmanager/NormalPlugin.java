package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

/**
 * A normal plugin for tests.
 * @author matt
 *
 */
public final class NormalPlugin extends AbstractPlugin implements Plugin {
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Normal";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0.1";
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
