package uk.me.gumbley.minimiser.plugin;

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
    public String getDevelopersContactDetails() {
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
    public String getSchemaVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }
}