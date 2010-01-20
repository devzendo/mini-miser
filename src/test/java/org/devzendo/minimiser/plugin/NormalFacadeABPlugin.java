package org.devzendo.minimiser.plugin;

import java.util.List;

import org.devzendo.minimiser.plugin.facade.FacadeA;
import org.devzendo.minimiser.plugin.facade.FacadeB;


/**
 * @author matt
 *
 */
public final class NormalFacadeABPlugin extends AbstractPlugin implements FacadeA, FacadeB {
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
        return "NormalFacadeABPlugin";
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
