package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * A PluginRegistry that presents the details of a dummy
 * app, for driver programs.
 * 
 * @author matt
 *
 */
public final class DummyAppPluginRegistry implements PluginRegistry {
    private final PluginDescriptor mAppPluginDescriptor;
    
    /**
     * Create the dummy registry with a fixed app
     */
    public DummyAppPluginRegistry() {
        this("Dummy App", "1.0.0");
    }
    
    /**
     * Create the dummy registry with a specific app
     * @param name the app name
     * @param version the app version
     */
    public DummyAppPluginRegistry(final String name, final String version) {
        mAppPluginDescriptor = new PluginDescriptor(true, 
            name, version, "1.0", "http://localhost",
            "devs@foo.com", "GPL3");
    }
    
    /**
     * {@inheritDoc}
     */
    public void addPluginDescriptor(final PluginDescriptor pluginDescriptor) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() {
        return mAppPluginDescriptor.getName();
    }

    /**
     * {@inheritDoc}
     */
    public PluginDescriptor getApplicationPluginDescriptor() {
        return mAppPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() {
        return mAppPluginDescriptor.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    public List<PluginDescriptor> getPluginDescriptors() {
        final ArrayList<PluginDescriptor> list = new ArrayList<PluginDescriptor>();
        list.add(mAppPluginDescriptor);
        return list;
    }
}
