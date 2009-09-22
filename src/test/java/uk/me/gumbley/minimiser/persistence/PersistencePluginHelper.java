package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.config.UnittestingConfig;
import uk.me.gumbley.minimiser.persistence.impl.JdbcTemplateAccessFactoryImpl;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.DummyAppPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * A helper class for building tests involving plugins and
 * persistence.
 * 
 * @author matt
 *
 */
public final class PersistencePluginHelper {
    private static final Logger LOGGER = Logger
            .getLogger(PersistencePluginHelper.class);
    private final PluginRegistry mPluginRegistry;
    private final PluginManager mPluginManager;
    private final AccessFactory mAccessFactory;
    private final File mTestDatabaseDirectory;
    private final boolean mSuppressEmptinessCheck;
    private final PersistencePluginHelperTidier mTidier;

    /**
     * Create a helper that will check that the test database
     * directory is empty, and use the real plugin manager.
     */
    public PersistencePluginHelper() {
        this(false, false);
    }
    
    /**
     * Create a helper that may or may not check that the test
     * database directory is empty, but uses the real plugin
     * manager.
     * @param suppressEmptinessCheck true to suppress the empty
     * check, false to check it
     */
    public PersistencePluginHelper(final boolean suppressEmptinessCheck) {
        this(suppressEmptinessCheck, false);
    }
    
    /**
     * Create a helper that may or may not check that the test
     * database directory is empty, and may use the real or dummy
     * plugin manager
     * @param suppressEmptinessCheck true to suppress the empty
     * check, false to check it
     * @param useDummyPluginManager true to use the dummy plugin
     * manager, false to use the proper one, with the ability to
     * load specific plugins
     */
    public PersistencePluginHelper(final boolean suppressEmptinessCheck,
            final boolean useDummyPluginManager) {
        mSuppressEmptinessCheck = suppressEmptinessCheck;
        mTestDatabaseDirectory = new UnittestingConfig().getTestDatabaseDirectory();
        mPluginRegistry = new DefaultPluginRegistry();
        mPluginManager = useDummyPluginManager ?
                new DummyAppPluginManager() : 
                new DefaultPluginManager(mPluginRegistry);
        mAccessFactory = new JdbcTemplateAccessFactoryImpl(mPluginManager);
        mTidier = new PersistencePluginHelperTidier(mTestDatabaseDirectory);
    }
    
    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     * @param miniMiserDAOFactory a MiniMiserDAOFactory used to
     * close the database if open, prior to deletion
     */
    public void addDatabaseToDelete(final String dbName, final MiniMiserDAOFactory miniMiserDAOFactory) {
        mTidier.addDatabaseToDelete(dbName, miniMiserDAOFactory);
    }
    
    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     */
    public void addDatabaseToDelete(final String dbName) {
        mTidier.addDatabaseToDelete(dbName);
    }
    
    /**
     * Check for an empty test database directory
     */
    public void validateTestDatabaseDirectory() {
        if (mTestDatabaseDirectory == null
                || mTestDatabaseDirectory.getAbsolutePath().length() == 0) {
            final String err = "No database directory defined";
            LOGGER.error(err);
            throw new IllegalStateException(err);
        }
        if (!mTestDatabaseDirectory.exists() || !mTestDatabaseDirectory.isDirectory()) {
            final String err = String.format(
                            "Database dir %s does not exist or is not a directory",
                            mTestDatabaseDirectory.getAbsolutePath());
            LOGGER.error(err);
            throw new IllegalStateException(err);
        }
        if (mSuppressEmptinessCheck) {
            return;
        }
        
        checkForEmptiness(mTestDatabaseDirectory);
    }

    private void checkForEmptiness(final File databaseDirectory) {
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            final List<String> files = Arrays.asList(databaseDirectory.list());
            if (files == null || files.size() == 0) {
                return;
            }
            final String err = String.format(
                            "Database directory %s contains %d files not cleared up after test: %s",
                            databaseDirectory.getAbsoluteFile(),
                            files.size(), StringUtils.join(files, ", "));
            LOGGER.error(err);
            if (files.size() != 0) {
                throw new IllegalStateException(err);
            }
        }
    }

    /**
     * Obtain the full path to a named directory
     * @param dbname the name of the database directory e.g. foo
     * @return the directory, prefixed with the test database directory, e.g.
     * /home/matt/testdb/foo
     */
    public String getAbsoluteDatabaseDirectory(final String dbname) {
        final StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.slashTerminate(mTestDatabaseDirectory.getAbsolutePath()));
        sb.append(dbname);
        return sb.toString();
    }
    
    /**
     * Create a database given its name and optional password.
     * It is left open. Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @return an InstabceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> createDatabase(final String dbName, final String dbPassword) {
        final Observer<PersistenceObservableEvent> ignoringObserver = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                // do nothing
            }
        };
        return createDatabase(dbName, dbPassword, ignoringObserver);
    }
    
    /**
     * Create a database given its name and optional password.
     * It is left open. Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @param observer an observer of persistence events that
     * will be notified of the progress of the creation.
     * @return an InstabceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> createDatabase(final String dbName, final String dbPassword, final Observer<PersistenceObservableEvent> observer) {
        // in case the open fails, we still need to delete the database on exit
        mTidier.addDatabaseToDelete(dbName);

        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Creating database dbName = %s, dbDirPlusDbName = %s, dbPassword = '%s'", dbName, dbDirPlusDbName, dbPassword));
        final InstanceSet<DAOFactory> daoFactorySet = mAccessFactory.createDatabase(dbDirPlusDbName, dbPassword, observer, null);
        mTidier.addDatabaseToDelete(dbName, daoFactorySet.getInstanceOf(MiniMiserDAOFactory.class));
        return daoFactorySet;
    }
    
    /**
     * Open a database given its name and optional password.
     * Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @return an InstabceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> openDatabase(final String dbName, final String dbPassword) {
        // in case the open fails, we still need to delete the database on exit
        mTidier.addDatabaseToDelete(dbName);

        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Opening database dbName = %s, dbDirPlusDbName = %s, dbPassword = '%s'", dbName, dbDirPlusDbName, dbPassword));
        final InstanceSet<DAOFactory> daoFactorySet = mAccessFactory.openDatabase(dbDirPlusDbName, dbPassword);
        mTidier.addDatabaseToDelete(dbName, daoFactorySet.getInstanceOf(MiniMiserDAOFactory.class));
        return daoFactorySet;
    }

    /**
     * Load the plugins given a resource path to a properties file
     * that lists the plugins to be loaded. Typically called from
     * a @Before method.
     * @param propertiesResourcePath the path to the properties
     * file
     * @return a list of loaded plugins
     * @throws PluginException on plugin load or initialisation
     * problems
     */
    public List<Plugin> loadPlugins(final String propertiesResourcePath) throws PluginException {
        mPluginManager.loadPlugins(propertiesResourcePath);
        return mPluginManager.getPlugins();
    }
    
    /**
     * Obtain the application plugin.
     * @return the application plugin, if loaded
     */
    public ApplicationPlugin getApplicationPlugin() {
        return mPluginManager.getApplicationPlugin();
    }
    
    /**
     * @param <F> the type of facade-implementor to return 
     * @param facadeType the type of the facade-implementor to return
     * @return a list of plugins implementing the given type
     */
    public <F> List<F> getPluginsImplementingFacade(final Class<F> facadeType) {
        return mPluginManager.getPluginsImplementingFacade(facadeType);
    }
    
    /**
     * Typically run in an @After block, tidy up after the
     * databases have been created. 
     */
    public void tidyTestDatabasesDirectory() {
        mTidier.tidy();
    }

    /**
     * Obtain the plugin manager
     * @return the plugin manager
     */
    public PluginManager getPluginManager() {
        return mPluginManager;
    }

    /**
     * Obtain the access factory
     * @return the access factory
     */
    public AccessFactory getAccessFactory() {
        return mAccessFactory;
    }
}
