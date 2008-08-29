package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.opener.DatabaseOpenEvent;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.util.DatabasePairEncapsulator;


/**
 * Tests for the automatic opening of databases that were open on last shutdown,
 * at startup.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/wiring/lifecycle/LifecycleTestCase.xml")
public final class TestDatabaseOpener extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;
    private Prefs prefs;
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
        prefs = getSpringLoader().getBean("prefs", Prefs.class);
        opener = getSpringLoader().getBean("opener", Opener.class);
        
        // wire up the opener to the opendatabaselist as
        // the menumediator does
        opener.addDatabaseOpenObserver(new Observer<DatabaseOpenEvent>() {
            public void eventOccurred(final DatabaseOpenEvent observableEvent) {
                openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
            }
        });

        // store the db names so we'll repoen them on lifecycle startup
        final List<String> pairs = new ArrayList<String>();
        for (DatabaseOpenDetails detail : dbDetails) {
            final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(detail.getName());
            final String pair = DatabasePairEncapsulator.escape(detail.getName(), dbDirPlusDbName);
            pairs.add(pair);
        }
        prefs.setOpenFiles(pairs.toArray(new String[0]));
    
    }

    /**
     * 
     */
    @Test
    public void shouldOpenLastSessionsDatabasesAndNotSwitchWhenNoLastActiveDatabaseOnStartup() {
        LOGGER.info(">>> shouldOpenLastSessionsDatabasesAndNotSwitchWhenNoLastActiveDatabaseOnStartup");
        createDatabasesWithPluggableBehaviourBeforeDeletion(accessFactory, dbDetails, new RunOnCreatedDbs() {
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
        createDatabasesWithPluggableBehaviourBeforeDeletion(accessFactory, dbDetails, new RunOnCreatedDbs() {
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

    private void closeOpenDatabases() {
        final int numOpened = openDatabaseList.getNumberOfDatabases();
        for (int i = 0; i < numOpened; i++) {
            final DatabaseDescriptor openDescriptor = openDatabaseList.getOpenDatabases().get(i);
            final MiniMiserDatabase database = (MiniMiserDatabase) openDescriptor.getAttribute(AttributeIdentifier.Database);
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
}
