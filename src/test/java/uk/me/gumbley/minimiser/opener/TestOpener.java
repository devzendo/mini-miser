package uk.me.gumbley.minimiser.opener;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;


/**
 * Tests the Opener.
 * @author matt
 *
 */
public final class TestOpener extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestOpener.class);
    
    private AccessFactory accessFactory;
    private Opener opener;
    
    /**
     * Grab the configured AccessFactory from the Spring App Context, and pass
     * it to a new Opener.
     * TODO not in a unit test!
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        opener = new Opener(accessFactory);
    }

    
    /**
     * 
     */
    @Test
    public void progressNotificationsOnPlainOpen() {
        final String dbName = "progress";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final Set<OpenerAdapter.ProgressStage> progressReceived = new HashSet<OpenerAdapter.ProgressStage>();

                final OpenerAdapter openerAdapter = new OpenerAdapter() {

                    public void reportProgress(final ProgressStage progressStage, final String description) {
                        LOGGER.info("Reached stage '" + progressStage + "' text '" + description + "'");
                        progressReceived.add(progressStage);
                    }
                };
                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final MiniMiserDatabase database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    Assert.assertTrue(progressReceived.contains(OpenerAdapter.ProgressStage.STARTING));
                    Assert.assertTrue(progressReceived.contains(OpenerAdapter.ProgressStage.OPENING));
                    Assert.assertNotNull(database);

                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            }
        });
    }
}
