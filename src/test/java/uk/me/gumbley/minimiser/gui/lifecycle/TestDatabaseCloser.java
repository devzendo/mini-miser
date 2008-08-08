package uk.me.gumbley.minimiser.gui.lifecycle;

import org.apache.log4j.Logger;
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
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.util.DatabasePairEncapsulator;


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
    private Prefs prefs;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        lifecycleManager = getSpringLoader().getBean("closeLifecycleManager", LifecycleManager.class);
        prefs = getSpringLoader().getBean("prefs", Prefs.class);
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
    public void closeFailureShouldDisplayAnError() {
        // TODO - will need to decouple problem reporting from closing, in the
        // same way I did for the Opener, via an adapter.
        // This can be reused in the opener lifecycle.
    }
    
    /**
     * 
     */
    @Test
    public void openDatabasesShouldBeStoredOnShutdown() {
        Assert.assertEquals(0, prefs.getOpenFiles().length);
        
        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));

        lifecycleManager.shutdown();
        
        Assert.assertEquals(1, prefs.getOpenFiles().length);
        final String openFile = prefs.getOpenFiles()[0];
        Assert.assertEquals(DatabasePairEncapsulator.escape("one", "/tmp/one"), openFile);
    }
}
