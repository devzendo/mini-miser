package org.devzendo.minimiser.pluginmanager;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * Something has happened with the PluginManager, this is the
 * notification you receive, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class PluginEvent implements ObservableEvent {
    /**
     * Create a PluginEvent
     */
    public PluginEvent() {
    }
}
