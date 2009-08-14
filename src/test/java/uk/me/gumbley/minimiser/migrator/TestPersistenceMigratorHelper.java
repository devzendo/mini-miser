package uk.me.gumbley.minimiser.migrator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.util.InstanceSet;


/**
 * Can the helper create old and new databases?
 * 
 * @author matt
 *
 */
public final class TestPersistenceMigratorHelper {
    private static final String MIGRATIONDB = "persistencemigratorhelper";
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        
        mPersistenceMigratorHelper = new PersistenceMigratorHelper(mPersistencePluginHelper);
    }

    /**
     * 
     */
    @After
    public void removeTestDatabases() {
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void oldDatabaseCanBeCreated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);
        
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationoldplugin.properties");
        // run
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        // test
        try {
            final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            final VersionDao versionDao = miniMiserDAOFactory.getVersionDao();
            final ApplicationPlugin appPlugin = mPersistencePluginHelper.getApplicationPlugin();
            final Version version = versionDao.findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            Assert.assertEquals("1.0", version.getVersion());
        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void newDatabaseCanBeCreated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createNewDatabase(MIGRATIONDB);
        
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
        // run
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        // test
        try {
            final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            final VersionDao versionDao = miniMiserDAOFactory.getVersionDao();
            final ApplicationPlugin appPlugin = mPersistencePluginHelper.getApplicationPlugin();
            final Version version = versionDao.findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            Assert.assertEquals("2.0", version.getVersion());
        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }
}
