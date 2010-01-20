package org.devzendo.minimiser.migrator;

import org.devzendo.minimiser.migrator.Migrator.MigrationVersion;
import org.junit.Assert;
import org.junit.Test;



/**
 * Test the static method for comparing PluginSchemaVersions
 * and returning a MigratorVersion.
 * 
 * @author matt
 *
 */
public final class TestMigratorComparePluginSchemaVersions {
    /**
     * 
     */
    @Test
    public void dissimilarCauseNoMigration() {
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        plugins.addPluginSchemaVersion("plugin0", "3.5");
        final PluginSchemaVersions database = new PluginSchemaVersions();
        database.addPluginSchemaVersion("plugin1", "1.0");
        Assert.assertEquals(
            MigrationVersion.CURRENT,
            DefaultMigrator.compareSchemaVersions(plugins, database));
    }

    /**
     * 
     */
    @Test
    public void equalityOfEmptiness() {
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        final PluginSchemaVersions database = new PluginSchemaVersions();
        Assert.assertEquals(
            MigrationVersion.CURRENT,
            DefaultMigrator.compareSchemaVersions(plugins, database));
    }

    /**
     * 
     */
    @Test
    public void equalityOfPopulated() {
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        plugins.addPluginSchemaVersion("plugin1", "1.0");
        plugins.addPluginSchemaVersion("plugin2", "2.0");
        final PluginSchemaVersions database = new PluginSchemaVersions();
        database.addPluginSchemaVersion("plugin1", "1.0");
        database.addPluginSchemaVersion("plugin2", "2.0");
        Assert.assertEquals(
            MigrationVersion.CURRENT,
            DefaultMigrator.compareSchemaVersions(plugins, database));
    }
    
    /**
     * 
     */
    @Test
    public void detectFuture() {
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        plugins.addPluginSchemaVersion("plugin1", "1.0");
        final PluginSchemaVersions database = new PluginSchemaVersions();
        database.addPluginSchemaVersion("plugin1", "1.1");
        Assert.assertEquals(
            MigrationVersion.FUTURE,
            DefaultMigrator.compareSchemaVersions(plugins, database));
    }
    
    /**
     * 
     */
    @Test
    public void detectOld() {
        final PluginSchemaVersions plugins = new PluginSchemaVersions();
        plugins.addPluginSchemaVersion("plugin1", "1.1");
        final PluginSchemaVersions database = new PluginSchemaVersions();
        database.addPluginSchemaVersion("plugin1", "1.0");
        Assert.assertEquals(
            MigrationVersion.OLD,
            DefaultMigrator.compareSchemaVersions(plugins, database));
    }
}
