package uk.me.gumbley.minimiser.plugin;

import java.util.List;

import uk.me.gumbley.minimiser.plugin.facade.FacadeA;

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
