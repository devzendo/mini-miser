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

package org.devzendo.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.DummyAppPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests for the automatic recording and closing the list of open databases
 * at shutdown.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/DatabaseOpenerAndCloserLifecycleTestCase.xml")
public final class TestDatabaseCloser extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;
    private MiniMiserPrefs prefs;
    private final DatabaseOpenDetails[] dbDetails = new DatabaseOpenDetails[] {
            new DatabaseOpenDetails("one", ""),
            new DatabaseOpenDetails("two", ""),
            new DatabaseOpenDetails("three", ""),
    };

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        lifecycleManager = getSpringLoader().getBean("closeLifecycleManager", LifecycleManager.class);
        prefs = getSpringLoader().getBean("prefs", MiniMiserPrefs.class);
    }

    /**
     *
     */
    @Test
    public void openDatabasesShouldBeClosedOnLifecycleShutdown() {
        LOGGER.info(">>> openDatabasesShouldBeClosedOnLifecycleShutdown");
        doCreateDatabasesBoilerplateWithOpenClosedTests(accessFactory, dbDetails, new RunOnCreatedDbs() {
            public void runOnCreatedDbs() {
                final MiniMiserDAOFactory[] openDatabases = new MiniMiserDAOFactory[dbDetails.length];
                LOGGER.info("... re-opening");
                try {
                    for (int i = 0; i < dbDetails.length; i++) {
                        final DatabaseOpenDetails detail = dbDetails[i];
                        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(detail.getName());
                        openDatabases[i] =
                            accessFactory.
                            openDatabase(dbDirPlusDbName, detail.getPassword()).
                            getInstanceOf(MiniMiserDAOFactory.class);
                        assertDatabaseShouldBeOpen(detail.getName());
                        Assert.assertNotNull(openDatabases[i]);
                        LOGGER.info("... we opened the database!");

                        final DatabaseDescriptor miniMiserDatabaseDescriptor =
                            new DatabaseDescriptor(detail.getName(), dbDirPlusDbName);
                        miniMiserDatabaseDescriptor.setDAOFactory(MiniMiserDAOFactory.class, openDatabases[i]);

                        Assert.assertFalse(openDatabases[i].isClosed());

                        openDatabaseList.addOpenedDatabase(miniMiserDatabaseDescriptor);
                    }

                    Assert.assertEquals(3, openDatabaseList.getNumberOfDatabases());

                    lifecycleManager.shutdown();

                    for (int i = 0; i < dbDetails.length; i++) {
                        final DatabaseOpenDetails detail = dbDetails[i];
                        Assert.assertTrue(openDatabases[i].isClosed());
                        assertDatabaseShouldBeClosed(detail.getName());
                    }
                } finally {
                    LOGGER.info("closing if necessary");
                    for (int i = 0; i < dbDetails.length; i++) {
                        if (!openDatabases[i].isClosed()) {
                            openDatabases[i].close();
                        }
                    }
                }
            }
        });
        LOGGER.info("<<< openDatabasesShouldBeClosedOnLifecycleShutdown");
    }

    /**
     *
     */
    @Test
    public void closeFailureShouldDisplayAnError() {
        // TODO: - will need to decouple problem reporting from closing, in the
        // same way I did for the Opener, via an adapter.
        // This can be reused in the opener lifecycle.
    }

    /**
     *
     */
    @Test
    public void openDatabasesShouldBeStoredOnShutdown() {
        LOGGER.info(">>> openDatabasesShouldBeStoredOnShutdown");
        Assert.assertEquals(0, prefs.getOpenFiles().length);

        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));

        lifecycleManager.shutdown();

        Assert.assertEquals(1, prefs.getOpenFiles().length);
        final String openFile = prefs.getOpenFiles()[0];
        Assert.assertEquals(DatabasePairEncapsulator.escape("one", "/tmp/one"), openFile);
        LOGGER.info("<<< openDatabasesShouldBeStoredOnShutdown");
    }

    /**
     *
     */
    @Test
    public void openDatabasesShouldBeRemovedFromTheOpenDatabaseListOnShutdown() {
        LOGGER.info(">>> openDatabasesShouldBeRemovedFromTheOpenDatabaseListOnShutdown");
        Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());
        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));
        Assert.assertEquals(1, openDatabaseList.getNumberOfDatabases());

        lifecycleManager.shutdown();

        Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());
        LOGGER.info("<<< openDatabasesShouldBeRemovedFromTheOpenDatabaseListOnShutdown");
    }

    /**
     *
     */
    @Test
    public void lastCurrentDatabaseShouldBeRecordedOnShutdown() {
        LOGGER.info(">>> lastCurrentDatabaseShouldBeRecordedOnShutdown");
        Assert.assertNull(prefs.getLastActiveFile());

        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));

        lifecycleManager.shutdown();

        Assert.assertEquals("one", prefs.getLastActiveFile());
        LOGGER.info("<<< lastCurrentDatabaseShouldBeRecordedOnShutdown");
    }

    /**
     *
     */
    @Test
    public void lastCurrentDatabaseShouldBeNullIfNothingOpenOnShutdown() {
        LOGGER.info(">>> lastCurrentDatabaseShouldBeNullIfNothingOpenOnShutdown");
        Assert.assertNull(prefs.getLastActiveFile());

        lifecycleManager.shutdown();

        Assert.assertNull(prefs.getLastActiveFile());
        LOGGER.info("<<< )lastCurrentDatabaseShouldBeNullIfNothingOpenOnShutdown");
    }

    /**
     *
     */
    @Test
    public void lastCurrentDatabaseShouldBeNullIfLastOpenDatabaseClosedBeforeShutdown() {
        LOGGER.info(">>> lastCurrentDatabaseShouldBeNullIfLastOpenDatabaseClosedBeforeShutdown");
        Assert.assertNull(prefs.getLastActiveFile());

        final DatabaseDescriptor descriptor = new DatabaseDescriptor("one", "/tmp/one");
        openDatabaseList.addOpenedDatabase(descriptor);
        Assert.assertEquals("one", openDatabaseList.getCurrentDatabase().getDatabaseName());
        openDatabaseList.removeClosedDatabase(descriptor);

        lifecycleManager.shutdown();

        Assert.assertNull(prefs.getLastActiveFile());
        LOGGER.info("<<< lastCurrentDatabaseShouldBeNullIfLastOpenDatabaseClosedBeforeShutdown");
    }
}
