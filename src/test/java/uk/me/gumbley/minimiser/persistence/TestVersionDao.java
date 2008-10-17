package uk.me.gumbley.minimiser.persistence;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.version.AppVersion;


/**
 * Tests the Version DAO
 * 
 * @author matt
 *
 */
public final class TestVersionDao extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestVersionDao.class);
    
    /**
     * Is the VERSIONS table populated correctly?
     */
    @Test
    public void checkVersionPopulation() { 
        LOGGER.info(">>> checkVersionPopulation");
        final String dbName = "checkversionpopulation";
        createDatabaseWithPluggableBehaviourBeforeDeletion(getAccessFactory(), dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                final MiniMiserDatabase openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbPassword);
                try {
                    final Version dbVersion = openedDatabase.getVersionDao().findVersion(VersionableEntity.SCHEMA_VERSION);
                    LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
                    Assert.assertNotNull(dbVersion);
                    Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
                    Assert.assertEquals(CurrentSchemaVersion.CURRENT_SCHEMA_VERSION, dbVersion.getVersion());
                    //
                    final Version appVersion = openedDatabase.getVersionDao().findVersion(VersionableEntity.APPLICATION_VERSION);
                    LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
                    Assert.assertNotNull(dbVersion);
                    Assert.assertEquals(VersionableEntity.APPLICATION_VERSION, appVersion.getEntity());
                    Assert.assertEquals(AppVersion.getVersion(), appVersion.getVersion());
                } finally {
                    openedDatabase.close();
                }
            }
            
        });
        LOGGER.info("<<< checkVersionPopulation");
    }
}
