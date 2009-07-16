package uk.me.gumbley.minimiser.internalplugins;

import java.util.List;

import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.pluginmanager.AbstractPlugin;
import uk.me.gumbley.minimiser.pluginmanager.AppDetailsPropertiesLoader;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A normal plugin that declares the version of the MiniMiser
 * framework, and the database schema, so that this is set in
 * the Versions table.
 * 
 * @author matt
 *
 */
public final class MinimiserPlugin extends AbstractPlugin implements Plugin {
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
        return getAppDetailsPropertyLoader().getName();
    }

    private AppDetailsPropertiesLoader getAppDetailsPropertyLoader() {
        final SpringLoader springLoader = getSpringLoader();
        assert springLoader != null;
        final AppDetailsPropertiesLoader appDetailsPropertiesLoader =
            springLoader.getBean(
                "appDetailsPropertiesLoader", 
                AppDetailsPropertiesLoader.class);
        assert appDetailsPropertiesLoader != null;
        return appDetailsPropertiesLoader;
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return CurrentSchemaVersion.CURRENT_SCHEMA_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return getAppDetailsPropertyLoader().getVersion();
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }
}
