package uk.me.gumbley.minimiser.gui.lifecycle;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.recentlist.DatabasePairEncapsulator;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * Tests for the automatic recording and closing the list of open databases
 * at shutdown.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/lifecycle/LifecycleTestCase.xml")
public class TestDatabaseCloser extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        lifecycleManager = getSpringLoader().getBean("closeLifecycleManager", LifecycleManager.class);
    }
    
    /**
     * 
     */
    @Test
    public void openDatabasesShouldBeClosedOnLifecycleShutdown() {
        LOGGER.info("** openDatabasesShouldBeClosedOnLifecycleShutdown");
        final String dbName = "closeme";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                final MiniMiserDatabase openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbPassword);
                try {
                    assertDatabaseShouldBeOpen(dbName);
                    Assert.assertNotNull(openedDatabase);
                    LOGGER.info("... we opened the database!");

                    final MiniMiserDatabaseDescriptor miniMiserDatabaseDescriptor =
                        new MiniMiserDatabaseDescriptor(dbName, dbDirPlusDbName, 
                                                        openedDatabase);
                    
                    openDatabaseList.addOpenedDatabase(miniMiserDatabaseDescriptor);
                    Assert.assertEquals(1, openDatabaseList.getNumberOfDatabases());

                    Assert.assertFalse(openedDatabase.isClosed());
                    
                    lifecycleManager.shutdown();
                    
                    Assert.assertTrue(openedDatabase.isClosed());
                    assertDatabaseShouldBeClosed(dbName);
                    
                } finally {
                    LOGGER.info("closing if necessary");
                    if (!openedDatabase.isClosed()) {
                        openedDatabase.close();
                    }
                }
            }
        });
    }
    
    /**
     * 
     */
    @Test
    public void openDatabasesShouldBeStoredOnShutdown() {
        // TODO closer lifecycle will be given the prefs
        
        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));

        final Prefs prefs = EasyMock.createStrictMock(Prefs.class);
        prefs.setRecentFiles(EasyMock.aryEq(new String[] {DatabasePairEncapsulator.escape("one", "/tmp/one")}));
        
        Assert.fail("unimplemented - need to test storage of open database list in prefs first");
    }
}
