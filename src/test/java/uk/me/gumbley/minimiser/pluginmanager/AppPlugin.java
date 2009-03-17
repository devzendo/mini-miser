package uk.me.gumbley.minimiser.pluginmanager;

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
}
