package org.devzendo.minimiser.gui.tab;

/**
 * A TabParameter is set in a TabIdentifier, and when this tabIdentifier's Tab is loaded
 * by the TabFactory, the TabParameter is made available in a BeanFactory so that it
 * can be passed in to the Tab constructor.
 *
 * TabParameter is only a marker interface to provide some type safety, rather than just
 * using Object for tab parameters.
 * @author matt
 *
 */
public interface TabParameter {
}
