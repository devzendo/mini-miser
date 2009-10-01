package uk.me.gumbley.minimiser.migrator;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Helper code for creating old, normal and future databases for
 * use in test code dealing with the Migrator.
 * 
 * @author matt
 *
 */
public final class PersistenceMigratorHelper {
    private final PersistencePluginHelper mPersistencePluginHelper;
    
    /**
     * Create the migrator helper, using the persistence plugin
     * helper.
     * @param persistencePluginHelper the persistence plugin
     * helper
     */
    public PersistenceMigratorHelper(final PersistencePluginHelper persistencePluginHelper) {
        mPersistencePluginHelper = persistencePluginHelper;
    }

    /**
     * Create a version 1.0 schema database. i.e. "old"
     * @param dbName the name of the database
     * @throws PluginException on error
     */
    public void createOldDatabase(final String dbName) throws PluginException {
        createDatabase(dbName, "old");
    }

    /**
     * Create a version 2.0 schema database. i.e. "new"
     * @param dbName the name of the database
     * @throws PluginException on error
     */
    public void createNewDatabase(final String dbName) throws PluginException {
        createDatabase(dbName, "new");
    }
    

    /**
     * Create a schema for some other application than the usual one.
     * @param dbName the name of the database
     * @throws PluginException on error
     */
    public void createOtherApplicationDatabase(final String dbName) throws PluginException {
        createDatabase(dbName, "otherapp");
    }

    private void createDatabase(final String dbName, final String pluginSubName) throws PluginException {
        // need a helper separate from the main one since it needs
        // to have old plugins loaded, and the main one loads the
        // new plugins
        final PersistencePluginHelper persistencePluginHelper = new PersistencePluginHelper();
        persistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigration" + pluginSubName + "plugin.properties");
        final MiniMiserDAOFactory miniMiserDAOFactory = persistencePluginHelper.createDatabase(dbName, "").getInstanceOf(MiniMiserDAOFactory.class);
        miniMiserDAOFactory.close();
        
        // Let the main helper delete the old database 
        // - can't use this DAOFactory to close though
        mPersistencePluginHelper.addDatabaseToDelete(dbName);
    }
    
    /**
     * Check that the Versions table has been updated correctly
     * @param openDatabase the database connection
     */
    public void checkForUpgradedVersions(final InstanceSet<DAOFactory> openDatabase) {
        final MiniMiserDAOFactory miniMiserDaoFactory = openDatabase.getInstanceOf(MiniMiserDAOFactory.class);
        final VersionDao versionDao = miniMiserDaoFactory.getVersionDao();
        final ApplicationPlugin applicationPlugin = mPersistencePluginHelper.getApplicationPlugin();
        
        final Version applicationSchemaVersion = versionDao.findVersion(applicationPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
        Assert.assertEquals(applicationPlugin.getSchemaVersion(), applicationSchemaVersion.getVersion());

        final Version applicationVersion = versionDao.findVersion(applicationPlugin.getName(), VersionableEntity.APPLICATION_VERSION);
        Assert.assertEquals(applicationPlugin.getVersion(), applicationVersion.getVersion());
    }

    /**
     * Check that the Versions have not been updated
     * @param openDatabase the database connection
     */
    public void checkForNoUpgradedVersions(final InstanceSet<DAOFactory> openDatabase) {
        final MiniMiserDAOFactory miniMiserDaoFactory = openDatabase.getInstanceOf(MiniMiserDAOFactory.class);
        final VersionDao versionDao = miniMiserDaoFactory.getVersionDao();
        final ApplicationPlugin applicationPlugin = mPersistencePluginHelper.getApplicationPlugin();

        final Version applicationSchemaVersion = versionDao.findVersion(applicationPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
        Assert.assertEquals("1.0", applicationSchemaVersion.getVersion());

        final Version applicationVersion = versionDao.findVersion(applicationPlugin.getName(), VersionableEntity.APPLICATION_VERSION);
        Assert.assertEquals("1.0", applicationVersion.getVersion());
    }

    /**
     * Find one of the new SampleObjects created during migration
     * @param jdbcTemplate the template for database connection
     * @param name the name of the SampleObject to retrieve
     * @return the SampleObject
     * @throws DataAccessException on error
     */
    public SampleObject findSampleObject(
            final SimpleJdbcTemplate jdbcTemplate,
            final String name) throws DataAccessException {
        final String sql = "select name, quantity from SampleObjects where name = ?";
        ParameterizedRowMapper<SampleObject> mapper =
            new ParameterizedRowMapper<SampleObject>() {
            
            // notice the return type with respect to Java 5 covariant return types
            public SampleObject mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                return new SampleObject(rs.getString("name"), rs.getInt("quantity"));
            }
        };
        return jdbcTemplate.queryForObject(sql, mapper, name);
    }
    
   /**
    * {@inheritDoc}
    */
   public boolean sampleObjectExists(final SimpleJdbcTemplate jdbcTemplate,
           final String name) throws DataAccessException {
       final int count = jdbcTemplate.queryForInt(
           "select count(0) from SampleObjects where name = ?",
           new Object[]{name});
       return count == 1;
   }
   
    /**
     * Check that the data created during migration actually
     * exists.
     * @param openDatabase the database connection
     */
    public void checkForUpgradedData(final InstanceSet<DAOFactory> openDatabase) {
        final SimpleJdbcTemplate simpleJdbcTemplate = openDatabase.
            getInstanceOf(MiniMiserDAOFactory.class).
            getSQLAccess().getSimpleJdbcTemplate();
    
        final SampleObject firstSampleObject = findSampleObject(simpleJdbcTemplate, "First");
        Assert.assertNotNull(firstSampleObject);
        Assert.assertEquals("First", firstSampleObject.getName());
        Assert.assertEquals(60, firstSampleObject.getQuantity());
        
        final SampleObject secondSampleObject = findSampleObject(simpleJdbcTemplate, "Second");
        Assert.assertNotNull(secondSampleObject);
        Assert.assertEquals("Second", secondSampleObject.getName());
        Assert.assertEquals(10, secondSampleObject.getQuantity());
    }

    /**
     * Check that there is no sample object data
     * @param openDatabase the open database
     */
    public void checkForNoUpgradedData(final InstanceSet<DAOFactory> openDatabase) {
        final SimpleJdbcTemplate simpleJdbcTemplate = openDatabase.
            getInstanceOf(MiniMiserDAOFactory.class).
            getSQLAccess().getSimpleJdbcTemplate();
        
        Assert.assertFalse(sampleObjectExists(simpleJdbcTemplate, "First"));

        Assert.assertFalse(sampleObjectExists(simpleJdbcTemplate, "Second"));
    }
}
