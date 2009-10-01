package uk.me.gumbley.minimiser.plugin;

import java.util.List;

import uk.me.gumbley.minimiser.plugin.facade.FacadeB;

/**
 * @author matt
 *
 */
public final class NormalFacadeBPlugin extends AbstractPlugin implements FacadeB {
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
        return "NormalFacadeBPlugin";
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
        return "2.0";
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }
}
