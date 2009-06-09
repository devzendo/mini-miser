package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;


/**
 * @author matt
 *
 */
public final class BadShutdownPlugin extends AbstractPlugin implements Plugin {
    private boolean mShutdownCalled;

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "BadShutdown";
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
         mShutdownCalled = true;
         throw new IllegalStateException("Ka-booom!");
    }

    /**
     * @return true iff shutdown has been called
     */
    public boolean hasShutdownBeenCalled() {
        return mShutdownCalled;
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
