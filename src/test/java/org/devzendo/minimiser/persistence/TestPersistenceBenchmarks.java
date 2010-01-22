package org.devzendo.minimiser.persistence;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * TestPersistenceBenchmarks
 * 
 * @author matt
 *
 */
public final class TestPersistenceBenchmarks extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final String PLUGIN_NAME = "MyPlugin";
    private static final Logger LOGGER = Logger
            .getLogger(TestPersistenceBenchmarks.class);

    /**
     * When updating, we need to know whether a row exists so we can do an
     * insert (if it doesn't already exist) or an update (if it exists).
     * 
     * Which is fastest, doing a select count, or a select by PK?
     * 
     * create db, put some data in, and time a select by PK and select count
     */
    @Test
    @Ignore
    public void testBenchmarkSelectAndCountForPersist() {
        LOGGER.info("*** testBenchmarkSelectAndCountForPersist start");
        final String dbName = "benchmark";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDAOFactory mmData = 
            getAccessFactory().
            createDatabase(dbDirPlusDbName, "").
            getInstanceOf(MiniMiserDAOFactory.class); 
        try {
            final VersionsDao versionsDao = mmData.getVersionDao();
            // populate
            for (int i = 0; i < 1000; i++) {
                final Version v = new Version(PLUGIN_NAME, String.format("Benchmark%d", i), false, String.format("Version%d", i));
                versionsDao.persistVersion(v);
            }
            // Reload using select by entity
            final long selectEntityStart = System.currentTimeMillis();
            for (int i = 999; i >= 0; i--) {
                final String entityPK = String.format("Benchmark%d", i);
                final Version v = versionsDao.findVersion(PLUGIN_NAME, entityPK);
                Assert.assertNotNull(v);
                //Assert.assertEquals(String.format("Version%d", i), v.getVersion());
                //Assert.assertEquals(entityPK, v.getEntity());
            }
            final long selectEntityStop = System.currentTimeMillis();
            final long selectEntityElapsed = selectEntityStop - selectEntityStart;
            LOGGER.info(String.format("Select by entity took %s", StringUtils.translateTimeDuration(selectEntityElapsed)));
            // Detect existence using select count(0)
            final long countEntityStart = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                final String entityPK = String.format("Benchmark%d", i);
                final boolean exists = versionsDao.exists(PLUGIN_NAME, entityPK);
                Assert.assertTrue(exists);
            }
            final long countEntityStop = System.currentTimeMillis();
            final long countEntityElapsed = countEntityStop - countEntityStart;
            LOGGER.info(String.format("Select count(0) took %s", StringUtils.translateTimeDuration(countEntityElapsed)));
            // Several runs of this on the laptop confirms that select count(0)
            // is faster (e.g. 946 ms vs 1s 412ms).
            // If this ever changes, we'd need to revise our exists methods.
            if (countEntityElapsed < selectEntityElapsed) {
                LOGGER.info("Select count(0) is faster");
            } else {
                LOGGER.info("Select by PK is faster");
                Assert.fail("Select by PK has become faster than select count(0) - revise DAO exists methods");
            }
        } finally {
            // tidy up
            deleteWithClosureCheck(dbName, mmData);
            LOGGER.info("*** testBenchmarkSelectAndCountForPersist done");
        }
    }
    
    /**
     * Plaintext databases should be faster than encrypted ones, but it ain't always so.
     */
    @Test
    @Ignore
    public void benchmarkSpeedOfPlaintextVsEncrypted() {
        final long speedOfPlaintext = createPopulatedDatabase("plaintextbenchmark", false);
        final long speedOfEncrypted = createPopulatedDatabase("encryptedbenchmark", true);
        LOGGER.info(String.format("Speed of plaintext db: %s", StringUtils.translateTimeDuration(speedOfPlaintext)));
        LOGGER.info(String.format("Speed of encrypted db: %s", StringUtils.translateTimeDuration(speedOfEncrypted)));
        // increased number of rows in test to 10000, and on laptop, swa:
        // Speed of plaintext db: 20s 627ms
        // Speed of encrypted db: 15s 471ms
        Assert.assertTrue(speedOfPlaintext < speedOfEncrypted);
    }

    private long createPopulatedDatabase(final String dbName, final boolean encrypted) {
        try {
            final long start = System.currentTimeMillis();
            final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
            final MiniMiserDAOFactory mmData = 
                getAccessFactory().
                createDatabase(dbDirPlusDbName, encrypted ? "secret squirrel" : "").
                getInstanceOf(MiniMiserDAOFactory.class);
            try {
                final VersionsDao versionsDao = mmData.getVersionDao();
                final int rows = 100000;
                // populate
                for (int i = 0; i < rows; i++) {
                    final Version v = new Version(PLUGIN_NAME, String.format("Benchmark%d", i), false, String.format("Version%d", i));
                    versionsDao.persistVersion(v);
                }
                // Reload using select by entity
                for (int i = rows - 1; i >= 0; i--) {
                    final String entityPK = String.format("Benchmark%d", i);
                    final Version v = versionsDao.findVersion(PLUGIN_NAME, entityPK);
                    Assert.assertNotNull(v);
                    //Assert.assertEquals(String.format("Version%d", i), v.getVersion());
                    //Assert.assertEquals(entityPK, v.getEntity());
                }
            } finally {
                // tidy up
                deleteWithClosureCheck(dbName, mmData);
            }
            final long stop = System.currentTimeMillis();
            return stop - start;
        } catch (final Throwable t) {
            LOGGER.error("caught unexpected", t);
            return 0;
        }
    }
}
