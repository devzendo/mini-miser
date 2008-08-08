package uk.me.gumbley.minimiser.gui.lifecycle;

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
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;
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
@ApplicationContext("uk/me/gumbley/minimiser/gui/lifecycle/LifecycleTestCase.xml")
public final class TestDatabaseOpener extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;
    private Prefs prefs;
    private Opener opener;

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
    }

    /**
     * 
     */
    @Test
    public void shouldOpenRecordedDatabasesOnStartup() {
        LOGGER.info("*** shouldOpenRecordedDatabasesOnStartup start");
        final String dbName = "testopen";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                // wire up the opener to the opendatabaselist as
                // the menumediator does
                opener.addDatabaseOpenObserver(new Observer<DatabaseOpenEvent>() {
                    public void eventOccurred(final DatabaseOpenEvent observableEvent) {
                        openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
                    }
                });
                // store this so we'll repoen it on lifecycle startup
                prefs.setOpenFiles(new String[] {DatabasePairEncapsulator.escape(dbName, dbDirPlusDbName)});
                
                Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());
                
                try {
                    lifecycleManager.startup();
                    
                    Assert.assertEquals(1, openDatabaseList.getNumberOfDatabases());
                    final DatabaseDescriptor descriptor = openDatabaseList.getOpenDatabases().get(0);
                    Assert.assertEquals(dbName, descriptor.getDatabaseName());
                    Assert.assertEquals(dbDirPlusDbName, descriptor.getDatabasePath());
                    
                    assertDatabaseShouldBeOpen(dbName);
                } finally {
                    if (openDatabaseList.getNumberOfDatabases() == 1) {
                        final MiniMiserDatabaseDescriptor openDescriptor = (MiniMiserDatabaseDescriptor) openDatabaseList.getOpenDatabases().get(0);
                        openDescriptor.getDatabase().close();
                        assertDatabaseShouldBeClosed(dbName);
                    }
                }
            }
        });
    }
}
