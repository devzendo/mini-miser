package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.springloader.ApplicationContext;

/**
 * Persistent tests that don't need real plugins and can make do
 * with simple dummies can subclass this.
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/persistence/DummyAppPluginManagerTestCase.xml")
public class DummyAppPluginManagerPersistenceUnittestCase extends
        PersistenceUnittestCase {
}
