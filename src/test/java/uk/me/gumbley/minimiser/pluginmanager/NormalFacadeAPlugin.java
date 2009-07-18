package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

/**
 * @author matt
 *
 */
public final class NormalFacadeAPlugin extends AbstractPlugin implements FacadeA {
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
        return "NormalFacadeAPlugin";
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
    public String getVersion() {
        return "3.0";
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }
}
