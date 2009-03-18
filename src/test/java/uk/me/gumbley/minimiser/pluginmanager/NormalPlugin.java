package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

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
}
