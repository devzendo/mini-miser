/**
 * 
 */
package uk.me.gumbley.minimiser.migrator;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import uk.me.gumbley.minimiser.pluginmanager.AbstractPlugin;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigration;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigrationFacade;

/**
 * Works with version 2.0 of the schema.
 *  
 * @author matt
 *
 */
public final class DatabaseMigrationNewAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, DatabaseMigration {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseMigrationNewAppPlugin.class);
    private final DatabaseMigrationFacade mDatabaseMigrationFacade;

    private boolean mMigrateCalled;
    private String mPreMigrationSchemaVersion;
    
    /**
     * An entity that this plugin creates upon migration; used
     * with SimpleJdbcTemplate, a la Version.
     * @author matt
     *
     */
    public static final class SampleObject {
        private final String mName;
        private final int mQuantity;

        /**
         * Construct the sample object bean
         * @param name the name
         * @param quantity the quantity
         */
        public SampleObject(final String name, final int quantity) {
            mName = name;
            mQuantity = quantity;
        }
        
        /**
         * @return the quantity
         */
        public int getQuantity() {
            return mQuantity;
        }

        /**
         * @return the name
         */
        public String getName() {
            return mName;
        }
    }
    
    /**
     * instantiate all the facades
     */
    public DatabaseMigrationNewAppPlugin() {
        mDatabaseMigrationFacade = new DatabaseMigrationFacade() {

            public void migrateSchema(
                    final DataSource dataSource,
                    final SimpleJdbcTemplate simpleJdbcTemplate,
                    final String version)
                    throws DataAccessException {
                LOGGER.info("Migrating from version '" + version + "'");
                mMigrateCalled = true;
                mPreMigrationSchemaVersion = version;
                
                simpleJdbcTemplate.getJdbcOperations().execute(
                    "CREATE TABLE SampleObjects("
                    + "name VARCHAR(40), "
                    + "quantity NUMBER"
                    + ")");
                final String insertSql = "INSERT INTO SampleObjects (name, quantity) values (?, ?)";
                simpleJdbcTemplate.update(insertSql, new Object[] {"First", 60});
                simpleJdbcTemplate.update(insertSql, new Object[] {"Second", 10});
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "DatabaseMigrationAppPlugin"; // must be same name between old and new
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "2.0"; // the new schema
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.1"; // note that this is stored also, and tested
        // for increase on upgrade, although it's only for info
        // - nothing's done with it, unlike the schema version
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * @return true iff the migration methods have been called
     */
    public boolean allMigrationMethodsCalled() {
        return mMigrateCalled;
    }

    /**
     * @return the database schema version the migration facade
     * was asked to migrate from
     */
    public String getPreMigrationSchemaVersion() {
        return mPreMigrationSchemaVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    public DatabaseMigrationFacade getDatabaseMigrationFacade() {
        return mDatabaseMigrationFacade;
    }
}
