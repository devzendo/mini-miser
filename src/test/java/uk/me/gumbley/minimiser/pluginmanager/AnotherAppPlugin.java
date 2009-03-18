package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

/**
 * @author matt
 *
 */
public final class AnotherAppPlugin extends AbstractPlugin implements ApplicationPlugin {
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Application";
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
}
