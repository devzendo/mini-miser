package org.devzendo.minimiser.plugin.internal;

import java.util.List;

import org.devzendo.minimiser.persistence.domain.CurrentSchemaVersion;
import org.devzendo.minimiser.plugin.AbstractPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.AppDetailsPropertiesLoader;
import org.devzendo.minimiser.springloader.SpringLoader;


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
        return CurrentSchemaVersion.getCurrentSchemaVersion();
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
