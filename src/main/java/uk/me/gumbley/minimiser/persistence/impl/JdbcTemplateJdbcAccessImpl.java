package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;
import org.h2.constant.ErrorCode;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateVersionDao;
import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A JdbcAccess that uses Spring's JdbcTemplate.
 *
 * @author matt
 *
 */
/**
 * @author matt
 *
 */
public final class JdbcTemplateJdbcAccessImpl implements AccessFactory {
    private final SpringLoader springLoader;
    
    /**
     * Construct one, using the SpringLoader
     * @param sL the SpringLoader
     */
    public JdbcTemplateJdbcAccessImpl(final SpringLoader sL) {
        springLoader = sL;
    }

    private class DatabaseSetup {
        private String dbPassword;
        private String dbPath;
        private String dbURL;
        private SingleConnectionDataSource dataSource;
        private SimpleJdbcTemplate jdbcTemplate;
        public DatabaseSetup(final String databasePath, final String password, final boolean allowCreate) {
            if (databasePath == null) {
                throw new DataAccessResourceFailureException("Null database path");
            }
            dbPath = databasePath.trim();
            if (dbPath.length() == 0) {
                throw new DataAccessResourceFailureException(String.format("Incorrect database path '%s'", databasePath));
            }
            dbPassword = (password == null) ? "" : password;
            dbURL = dbPassword.length() == 0 ?
                    String.format("jdbc:h2:%s", dbPath) :
                    String.format("jdbc:h2:%s;CIPHER=AES");
            if (!allowCreate) {
                dbURL += ";IFEXISTS=TRUE";
            }
            // We can populate half the data source from Spring, but we need to
            // build the URL at runtime, since it depends on the database path and
            // password.
            dataSource = springLoader.getBean("dataSource", SingleConnectionDataSource.class);
            dataSource.setUrl(dbURL);
            dataSource.setPassword(dbPassword);
            // Just instantiate directly rather than bothering with Spring
            // TODO Spring lossage: I'd use a SimpleJdbcTemplate here, but it
            // doesn't support getDataSource() so we can't close programmatically.
            // See http://forum.springframework.org/archive/index.php/t-9704.html
            jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        }
        public String getDbPassword() {
            return dbPassword;
        }
        public String getDbPath() {
            return dbPath;
        }
        public String getDbURL() {
            return dbURL;
        }
        public SimpleJdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }
        public SingleConnectionDataSource getDataSource() {
            return dataSource;
        }
    }
    /**
     * {@inheritDoc}
     */
    public MigratableDatabase openMigratableDatabase(final String databasePath, final String password) {
        DatabaseSetup dbSetup = new DatabaseSetup(databasePath, password, false);
        // Possible Spring bug: if the database isn't there, it doesn't throw
        // an (unchecked) exception. - it does detect it and logs voluminously,
        // but then doesn't pass the error on to me.
        // Looks like a 90013 (DATABASE_HOT_FOUND_1) isn't mapped by the default
        // Spring sql-error-codes.xml.
        // So, I have to check myself. (Obviating one of the reasons I chose Spring!)
        try {
            dbSetup.getDataSource().getConnection().close();
        } catch (SQLException e) {
            if (e.getErrorCode() == ErrorCode.DATABASE_NOT_FOUND_1) {
                throw new DataAccessResourceFailureException(String.format("Database at %s not found", databasePath));
            }
            // Assume that anything that goes wrong here is bad...
            throw new org.springframework.jdbc.UncategorizedSQLException(
                String.format("Possible database not found - SQL Error Code %d",
                    e.getErrorCode()), "", e);
        }
        return new JdbcTemplateMigratableDatabaseImpl(dbSetup.getDbURL(), dbSetup.getDbPath(), dbSetup.getJdbcTemplate(), dbSetup.getDataSource());
    }

    /**
     * {@inheritDoc}
     */
    public MiniMiserDatabase createDatabase(final String databasePath, final String password) {
        // create the database
        DatabaseSetup dbSetup = new DatabaseSetup(databasePath, password, true);
        createTables(dbSetup);
        populateTables(dbSetup);
        return new JdbcTemplateMiniMiserDatabaseImpl(dbSetup.getDbURL(), dbSetup.getDbPath(), dbSetup.getJdbcTemplate(), dbSetup.getDataSource());
    }

    // TODO move this to VersionsDao?
    private void createTables(final DatabaseSetup dbDetails) {
        SimpleJdbcTemplate jdbcTemplate = dbDetails.getJdbcTemplate();
        
        jdbcTemplate.getJdbcOperations().execute("CREATE TABLE Versions("
            + "entity VARCHAR(40) PRIMARY KEY,"
            + "version VARCHAR(40)"
            + ")");
        
    }
    
    private void populateTables(final DatabaseSetup dbDetails) {
        // TODO get this from Spring when we have a factory bean that can
        // create a JdbcTemplate from a programmatically created
        // DataSource.
        VersionDao versionDao = new JdbcTemplateVersionDao(dbDetails.getJdbcTemplate());
        Version version = new Version(VersionableEntity.SCHEMA_VERSION, CurrentSchemaVersion.CURRENT_SCHEMA_VERSION);
        versionDao.persistVersion(version);
    }
}

