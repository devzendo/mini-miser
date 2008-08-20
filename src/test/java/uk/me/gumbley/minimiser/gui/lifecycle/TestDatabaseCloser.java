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
public final class TestDatabaseCloser extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;
    private Prefs prefs;
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
        prefs = getSpringLoader().getBean("prefs", Prefs.class);
    }
    
    /**
     * 
     */
    @Test
    public void openDatabasesShouldBeClosedOnLifecycleShutdown() {
        LOGGER.info("** openDatabasesShouldBeClosedOnLifecycleShutdown");
        createDatabasesWithPluggableBehaviourBeforeDeletion(accessFactory, dbDetails, new RunOnCreatedDbs() {
            @SuppressWarnings("unchecked")
            public void runOnCreatedDbs() {
                final MiniMiserDatabase[] openDatabases = new MiniMiserDatabase[dbDetails.length];
                LOGGER.info("... re-opening");
                try {
                    for (int i = 0; i < dbDetails.length; i++) {
                        final DatabaseOpenDetails detail = dbDetails[i];
                        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(detail.getName());
                        openDatabases[i] = accessFactory.openDatabase(dbDirPlusDbName, detail.getPassword());
                        assertDatabaseShouldBeOpen(detail.getName());
                        Assert.assertNotNull(openDatabases[i]);
                        LOGGER.info("... we opened the database!");
    
                        final MiniMiserDatabaseDescriptor miniMiserDatabaseDescriptor =
                            new MiniMiserDatabaseDescriptor(detail.getName(), dbDirPlusDbName, 
                                                            openDatabases[i]);

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
    
    /**
     * 
     */
    @Test
    public void lastCurrentDatabaseShouldBeRecordedOnShutdown() {
        Assert.assertNull(prefs.getLastActiveFile());

        openDatabaseList.addOpenedDatabase(new DatabaseDescriptor("one", "/tmp/one"));

        lifecycleManager.shutdown();

        Assert.assertEquals("one", prefs.getLastActiveFile());
    }
    
    /**
     * 
     */
    @Test
    public void lastCurrentDatabaseShouldBeNullIfNothingOpenOnShutdown() {
        Assert.assertNull(prefs.getLastActiveFile());

        lifecycleManager.shutdown();

        Assert.assertNull(prefs.getLastActiveFile());
    }
    
    /**
     * 
     */
    @Test
    public void lastCurrentDatabaseShouldBeNullIfLastOpenDatabaseClosedBeforeShutdown() {
        Assert.assertNull(prefs.getLastActiveFile());

        final DatabaseDescriptor descriptor = new DatabaseDescriptor("one", "/tmp/one");
        openDatabaseList.addOpenedDatabase(descriptor);
        Assert.assertEquals("one", openDatabaseList.getCurrentDatabase().getDatabaseName());
        openDatabaseList.removeClosedDatabase(descriptor);

        lifecycleManager.shutdown();

        Assert.assertNull(prefs.getLastActiveFile());
    }
}
