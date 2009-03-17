package uk.me.gumbley.minimiser.pluginmanager;

/**
 * An ApplicationPlugin extends the basic plugin to
 * provide details of the main application. There must be only
 * one ApplicationPlugin loaded by the PluginManager for a
 * given application. The framework will report a fatal error
 * if one is not present.
 * 
 * @author matt
 *
 */
public interface ApplicationPlugin extends Plugin {
}
