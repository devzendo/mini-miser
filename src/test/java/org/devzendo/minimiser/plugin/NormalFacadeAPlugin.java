package org.devzendo.minimiser.plugin;

import java.util.List;

import org.devzendo.minimiser.plugin.facade.FacadeA;


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
