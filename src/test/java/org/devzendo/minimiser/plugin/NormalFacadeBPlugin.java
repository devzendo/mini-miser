package org.devzendo.minimiser.plugin;

import java.util.List;

import org.devzendo.minimiser.plugin.facade.FacadeB;


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
