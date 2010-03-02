/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
