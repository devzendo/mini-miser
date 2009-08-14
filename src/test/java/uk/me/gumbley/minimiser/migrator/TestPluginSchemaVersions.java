package uk.me.gumbley.minimiser.migrator;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the comparison between maps of plugin name to schema
 * versions.
 * 
 * @author matt
 *
 */
public final class TestPluginSchemaVersions {

    /**
     * 
     */
    @Test
    public void toStringGivesDetails() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        psv1.addPluginSchemaVersion("plugin0", "v3.5");
        Assert.assertEquals("plugin0:v3.5", psv1.toString());
        psv1.addPluginSchemaVersion("plugin1", "7");
        final String string = psv1.toString();
        Assert.assertTrue(
            string.equals("plugin0:v3.5, plugin1:7")
            ||
            string.equals("plugin1:7, plugin0:v3.5"));
    }
    
    // if they're not comparable, equality/comparison makes no
    // sense
    
    /**
     * 
     */
    @Test
    public void dissimilarAreStrangelyEqual() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        psv1.addPluginSchemaVersion("plugin0", "3.5");
        final PluginSchemaVersions psv2 = new PluginSchemaVersions();
        psv2.addPluginSchemaVersion("plugin1", "1.0");
        Assert.assertEquals(0, psv2.compareTo(psv1));
        Assert.assertEquals(0, psv1.compareTo(psv2));
        Assert.assertEquals(psv1, psv2);
        Assert.assertEquals(psv2, psv1);
    }
    
    /**
     * 
     */
    @Test
    public void equalityOfEmptiness() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        final PluginSchemaVersions psv2 = new PluginSchemaVersions();
        Assert.assertEquals(psv1, psv2);
        Assert.assertEquals(0, psv1.compareTo(psv2));
        Assert.assertEquals(0, psv2.compareTo(psv1));
    }

    /**
     * 
     */
    @Test
    public void equalityOfPopulated() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        psv1.addPluginSchemaVersion("plugin1", "1.0");
        psv1.addPluginSchemaVersion("plugin2", "2.0");
        final PluginSchemaVersions psv2 = new PluginSchemaVersions();
        psv2.addPluginSchemaVersion("plugin1", "1.0");
        psv2.addPluginSchemaVersion("plugin2", "2.0");
        Assert.assertEquals(psv1, psv2);
        Assert.assertEquals(0, psv1.compareTo(psv2));
        Assert.assertEquals(0, psv2.compareTo(psv1));
    }

    /**
     * 
     */
    @Test
    public void inequalityOfGreatherThan() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        psv1.addPluginSchemaVersion("plugin1", "1.0");
        final PluginSchemaVersions psv2 = new PluginSchemaVersions();
        psv2.addPluginSchemaVersion("plugin1", "1.1");
        Assert.assertFalse(psv1.equals(psv2));
        Assert.assertFalse(psv2.equals(psv1));
        Assert.assertEquals(-1, psv1.compareTo(psv2));
        Assert.assertEquals(1, psv2.compareTo(psv1));
    }
    
    /**
     * 
     */
    @Test
    public void inequalityOfLessThan() {
        final PluginSchemaVersions psv1 = new PluginSchemaVersions();
        psv1.addPluginSchemaVersion("plugin1", "1.1");
        final PluginSchemaVersions psv2 = new PluginSchemaVersions();
        psv2.addPluginSchemaVersion("plugin1", "1.0");
        Assert.assertFalse(psv1.equals(psv2));
        Assert.assertFalse(psv2.equals(psv1));
        Assert.assertEquals(1, psv1.compareTo(psv2));
        Assert.assertEquals(-1, psv2.compareTo(psv1));
    }
    
    // now some tests for mixes, to make sure that the first
    // non-equal plugin doesn't short the test

    /**
     * Difficult to test as the entries are computed in map
     * retrieval order.
     */
    @Test
    public void anyDowngradedPluginsMustBeDetectedFirst() {
        final PluginSchemaVersions database = new PluginSchemaVersions();
        database.addPluginSchemaVersion("p1", "1.3");
        database.addPluginSchemaVersion("p2", "1.4");
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        plugins.addPluginSchemaVersion("p1", "4.0"); // plugin upgrade
        plugins.addPluginSchemaVersion("p2", "1.0"); // plugin downgrade 
        Assert.assertFalse(database.equals(plugins));
        Assert.assertFalse(plugins.equals(database));
        // 
        Assert.assertEquals(-1, database.compareTo(plugins));
        Assert.assertEquals(-1, plugins.compareTo(database));
    }
}
