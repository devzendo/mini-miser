package uk.me.gumbley.minimiser.pluginmanager;

import java.util.Arrays;
import java.util.List;

/**
 * @author matt
 *
 */
public final class AppPlugin extends AbstractPlugin implements ApplicationPlugin {
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
        final String[] contexts = new String[] {
                "uk/me/gumbley/minimiser/pluginmanager/PluginAppContext.xml"
        };
        
        return Arrays.asList(contexts);
    }
}
