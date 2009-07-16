package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A PluginRegistry that presents the details of a dummy
 * app, for driver programs.
 * 
 * @author matt
 *
 */
public final class DummyAppPluginRegistry implements PluginRegistry {
    private final ApplicationPluginDescriptor mApplicationPluginDescriptor;
    
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
        mApplicationPluginDescriptor = new ApplicationPluginDescriptor(true, 
            name, version, "1.0", "http://localhost",
            "devs@foo.com", "GPL3", "COPYING.txt", "about.html",
            "changelog.txt");
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
        if (mApplicationPluginDescriptor != null) {
            final String appName = mApplicationPluginDescriptor.getName();
            if (!StringUtils.isBlank(appName)) {
                return appName;
            }
        }
        return UNKNOWN_APPLICATION;
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationPluginDescriptor getApplicationPluginDescriptor() {
        return mApplicationPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() {
        if (mApplicationPluginDescriptor != null) {
            final String appVersion = mApplicationPluginDescriptor.getVersion();
            if (!StringUtils.isBlank(appVersion)) {
                return appVersion;
            }
        }
        return UNKNOWN_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public List<PluginDescriptor> getPluginDescriptors() {
        final ArrayList<PluginDescriptor> list = new ArrayList<PluginDescriptor>();
        list.add(mApplicationPluginDescriptor);
        return list;
    }
}
