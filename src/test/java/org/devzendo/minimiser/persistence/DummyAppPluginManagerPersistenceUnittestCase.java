package org.devzendo.minimiser.persistence;

import org.devzendo.minimiser.springloader.ApplicationContext;

/**
 * Persistent tests that don't need real plugins and can make do
 * with simple dummies can subclass this.
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/persistence/DummyAppPluginManagerTestCase.xml")
public class DummyAppPluginManagerPersistenceUnittestCase extends
        PersistenceUnittestCase {
}
