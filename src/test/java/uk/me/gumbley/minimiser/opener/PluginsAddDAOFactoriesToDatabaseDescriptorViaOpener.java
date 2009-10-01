package uk.me.gumbley.minimiser.opener;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import uk.me.gumbley.minimiser.migrator.DefaultMigrator;
import uk.me.gumbley.minimiser.migrator.Migrator;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.DatabaseOpeningAppPlugin;
import uk.me.gumbley.minimiser.persistence.DatabaseOpeningDAOFactory;
import uk.me.gumbley.minimiser.persistence.DefaultPluginManagerPersistenceUnittestCase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.plugin.Plugin;
import uk.me.gumbley.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.util.InstanceSet;


/**
 * The Opener should add all DAOFactories (from miniMiser and the
 * plugins) to the DatabaseDescriptor.
 * TODO rewrite this to use the PersistencePluginHelper
 * @author matt
 *
 */
public final class PluginsAddDAOFactoriesToDatabaseDescriptorViaOpener extends DefaultPluginManagerPersistenceUnittestCase {
    private static final String PLUGINDBNAME = "openerplugindaofactorydb";
    private PluginManager mPluginManager;
    private DatabaseOpeningAppPlugin mDatabaseOpeningAppPlugin;
    private AccessFactory mAccessFactory;
    private String mDbDirPlusDbName;
    private Opener mOpener;
    private Migrator mMigrator;
    
    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mPluginManager = getPluginManager();
        mPluginManager.loadPlugins("uk/me/gumbley/minimiser/persistence/persistenceopeningplugin.properties");
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        mDatabaseOpeningAppPlugin = (DatabaseOpeningAppPlugin) plugins.get(0);
        Assert.assertNotNull(mDatabaseOpeningAppPlugin);
        final List<DatabaseOpening> databaseOpeningPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseOpening.class);
        Assert.assertEquals(1, databaseOpeningPlugins.size());
        mAccessFactory = getAccessFactory();
        Assert.assertNotNull(mAccessFactory);
        mDbDirPlusDbName = getAbsoluteDatabaseDirectory(PLUGINDBNAME);
        mMigrator = new DefaultMigrator(mPluginManager);
        mOpener = new DefaultOpener(mAccessFactory, mMigrator, mPluginManager);
    }
    
    private final class NullOpenerAdapter implements OpenerAdapter {
        public void databaseNotFound(
                final DataAccessResourceFailureException exception) {
            throw new IllegalStateException("Wasn't expecting the database to not be found: " + exception.getMessage());
        }

        public void reportProgress(
                final ProgressStage progressStage,
                final String description) {
        }

        public String requestPassword() {
            throw new IllegalStateException("Wasn't expecting to have a password requested");
        }

        public void migrationFailed(final DataAccessException exception) {
            throw new IllegalStateException("Wasn't expecting a migration failure: " + exception.getMessage());
        }

        public void seriousProblemOccurred(final DataAccessException exception) {
            throw new IllegalStateException("Wasn't expecting a problem: " + exception.getMessage());
        }

        public void startOpening() {
        }

        public void stopOpening() {
        }

        public boolean requestMigration() {
            throw new IllegalStateException("Wasn't expecting to have migration requested");
        }

        public void migrationNotPossible() {
        }

        public void createdByOtherApplication() {
        }

        public void noApplicationPluginAvailable() {
        }
    }
    
    /**
     * 
     */
    @Test
    public void openerAddsPluginDAOFactoryToDatabaseDescriptor() {
        final DatabaseOpenObserver openObserver = new DatabaseOpenObserver();
        mOpener.addDatabaseOpenObserver(openObserver);
        InstanceSet<DAOFactory> daoFactories = null;
        try {
            createDatabase();
            daoFactories = mOpener.openDatabase(PLUGINDBNAME, mDbDirPlusDbName, new NullOpenerAdapter());
            Assert.assertNotNull(daoFactories);
            
            // Now was the plugin's DAOFactory added?
            // Some preliminary checks to make sure the MiniMiserDAOFactory is added also...
            openObserver.assertDatabaseOpen();
            final DatabaseDescriptor databaseDescriptor = openObserver.getDatabaseOpenEvent().getDatabase();
            Assert.assertEquals(PLUGINDBNAME, databaseDescriptor.getDatabaseName());
            Assert.assertNotNull(databaseDescriptor.getDAOFactory(MiniMiserDAOFactory.class));
            
            // Well, was the plugin's DAOFactory added?
            Assert.assertNotNull(databaseDescriptor.getDAOFactory(DatabaseOpeningDAOFactory.class));
        } finally {
            try {
                if (daoFactories != null) {
                    daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
                }
            } finally {
                deleteDatabaseFiles(PLUGINDBNAME);
            }
        }
    }
    
    private void createDatabase() {
        mAccessFactory.createDatabase(mDbDirPlusDbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();        
    }
}
