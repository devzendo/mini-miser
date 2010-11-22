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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.opener.DatabaseOpenEvent;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseListEmptyEvent;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.DummyAppPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests for the automatic opening of databases that were open on last shutdown,
 * at startup.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/DatabaseOpenerAndCloserLifecycleTestCase.xml")
public final class TestDatabaseOpener extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;
    private MiniMiserPrefs prefs;
    private Opener opener;
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
        lifecycleManager = getSpringLoader().getBean("openLifecycleManager", LifecycleManager.class);
        prefs = getSpringLoader().getBean("prefs", MiniMiserPrefs.class);
        opener = getSpringLoader().getBean("opener", Opener.class);

        // wire up the opener to the opendatabaselist as
        // the menumediator does
        opener.addDatabaseOpenObserver(new Observer<DatabaseOpenEvent>() {
            public void eventOccurred(final DatabaseOpenEvent observableEvent) {
                openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
            }
        });
    }

    /**
     *
     */
    @Test
    public void shouldOpenLastSessionsDatabasesAndNotSwitchWhenNoLastActiveDatabaseOnStartup() {
        LOGGER.info(">>> shouldOpenLastSessionsDatabasesAndNotSwitchWhenNoLastActiveDatabaseOnStartup");
        storeDatabaseNames();

        doCreateDatabasesBoilerplateWithOpenClosedTests(accessFactory, dbDetails, new RunOnCreatedDbs() {
            public void runOnCreatedDbs() {

                Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());

                try {
                    lifecycleManager.startup();

                    checkCorrectlyOpenedDatabases();

                    // no last active database stored, stick on three
                    Assert.assertEquals("three", openDatabaseList.getCurrentDatabase().getDatabaseName());
                } finally {
                    closeOpenDatabases();
                }
            }
        });
        LOGGER.info("<<< shouldOpenLastSessionsDatabasesAndNotSwitchWhenNoLastActiveDatabaseOnStartup");
    }

    /**
     *
     */
    @Test
    public void shouldOpenLastSessionsDatabasesAndSwitchToLastActiveDatabaseOnStartup() {
        LOGGER.info(">>> shouldOpenLastSessionsDatabasesAndSwitchToLastActiveDatabaseOnStartup");
        storeDatabaseNames();

        doCreateDatabasesBoilerplateWithOpenClosedTests(accessFactory, dbDetails, new RunOnCreatedDbs() {
            public void runOnCreatedDbs() {

                prefs.setLastActiveFile("two");

                Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());

                try {
                    lifecycleManager.startup();

                    checkCorrectlyOpenedDatabases();
                    // have we switched back to two, as it was the last current database?
                    Assert.assertEquals("two", openDatabaseList.getCurrentDatabase().getDatabaseName());
                } finally {
                    closeOpenDatabases();
                }
            }
        });
        LOGGER.info("<<< shouldOpenLastSessionsDatabasesAndSwitchToLastActiveDatabaseOnStartup");
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void noDatabasesToOpenFiresEmptyEvent() {
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(DatabaseListEmptyEvent.class));
        EasyMock.replay(obs);
        openDatabaseList.addDatabaseEventObserver(obs);

        lifecycleManager.startup();

        Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());
        EasyMock.verify(obs);
    }

    private void closeOpenDatabases() {
        final int numOpened = openDatabaseList.getNumberOfDatabases();
        for (int i = 0; i < numOpened; i++) {
            final DatabaseDescriptor openDescriptor = openDatabaseList.getOpenDatabases().get(i);
            final MiniMiserDAOFactory database = openDescriptor.getDAOFactory(MiniMiserDAOFactory.class);
            database.close();
            assertDatabaseShouldBeClosed(openDescriptor.getDatabaseName());
        }
    }

    private void checkCorrectlyOpenedDatabases() {
        Assert.assertEquals(3, openDatabaseList.getNumberOfDatabases());
        for (int i = 0; i < dbDetails.length; i++) {
            final DatabaseOpenDetails detail = dbDetails[i];

            final DatabaseDescriptor descriptor = openDatabaseList.getOpenDatabases().get(i);
            Assert.assertEquals(detail.getName(), descriptor.getDatabaseName());
            Assert.assertEquals(getAbsoluteDatabaseDirectory(detail.getName()), descriptor.getDatabasePath());
            assertDatabaseShouldBeOpen(detail.getName());
        }
    }

    private void storeDatabaseNames() {
        // store the db names so we'll repoen them on lifecycle startup
        final List<String> pairs = new ArrayList<String>();
        for (final DatabaseOpenDetails detail : dbDetails) {
            final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(detail.getName());
            final String pair = DatabasePairEncapsulator.escape(detail.getName(), dbDirPlusDbName);
            pairs.add(pair);
        }
        prefs.setOpenFiles(pairs.toArray(new String[0]));
    }
}
