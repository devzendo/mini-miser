package org.devzendo.minimiser.pluginmanager;

/**
 * PluginExceptions are thrown when plugins cannot be successfully
 * loaded
 * @author matt
 *
 */
@SuppressWarnings("serial")
public class PluginException extends Exception {

    /**
     * A problem has occurred trying to load the plugins
     * @param warning what the problem is
     */
    public PluginException(final String warning) {
        super(warning);
    }
}
