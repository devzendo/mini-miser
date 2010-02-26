package org.devzendo.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.MainFrameTitle;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.pluginmanager.ApplicationPluginLoadedEvent;
import org.devzendo.minimiser.pluginmanager.PluginDescriptor;
import org.devzendo.minimiser.pluginmanager.PluginEvent;
import org.devzendo.minimiser.pluginmanager.PluginManager;


/**
 * Wire up a PluginLoaded observer such that when the app plugin is
 * loaded, the title bar is updated.
 * 
 * @author matt
 *
 */
public final class MainFrameTitleInitialisingPluginLoadedObservingLifecycle implements Lifecycle, Observer<PluginEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameTitleInitialisingPluginLoadedObservingLifecycle.class);
    private final PluginManager mPluginManager;
    private final MainFrameTitle mMainFrameTitle;
    
    /**
     * Construct and wire up.
     * @param pluginManager the plugin manager which will be
     * observed for plugin loaded events by this class
     * @param mainFrameTitle the main frame title which will be
     * poked by setting a null database name when the plugin
     * loaded event is fired. 
     */
    public MainFrameTitleInitialisingPluginLoadedObservingLifecycle(
            final PluginManager pluginManager,
            final MainFrameTitle mainFrameTitle) {
                mPluginManager = pluginManager;
                mMainFrameTitle = mainFrameTitle;
        // wire up early, not in startup
        mPluginManager.addPluginEventObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // don't do any wiring here, get in as early as poss!
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PluginEvent observableEvent) {
        if (observableEvent instanceof ApplicationPluginLoadedEvent) {
            final ApplicationPluginLoadedEvent appEvent = (ApplicationPluginLoadedEvent) observableEvent;
            final PluginDescriptor pluginDescriptor = appEvent.getPluginDescriptor();
            LOGGER.info("Setting main frame title application name to " + pluginDescriptor.getName());
            mMainFrameTitle.setApplicationName(pluginDescriptor.getName());
        }
    }
}