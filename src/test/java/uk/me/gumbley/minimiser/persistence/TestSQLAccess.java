package uk.me.gumbley.minimiser.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess.ResultType;


/**
 * Tests the low-level access to the database, for migration, parsing SQL, etc.
 * @author matt
 *
 */
public final class TestSQLAccess extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestSQLAccess.class);
    
    /**
     * 
     */
    @Test
    public void selectParsedOK() {
        final String dbName = "selectparsedok";
        final String dbPassword = "";
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                final SQLAccess sqlAccess = mmData.getSQLAccess();
                final ResultType type = sqlAccess.parse("SELECT * FROM Versions");
                Assert.assertSame(ResultType.ResultSet, type);
            }
            
        });
    }
    
    /**
     * 
     */
    @Test
    public void selectReturnsDataViaJdbc() {
        final String dbName = "selectreturnsdataviajdbc";
        final String dbPassword = "";
        
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                Statement statement = null;
                try {
                    final SQLAccess sqlAccess = mmData.getSQLAccess();
                    
                    statement = sqlAccess.createStatement();
                    final ResultSet rs = statement.executeQuery("SELECT * FROM Versions");
                    Assert.assertTrue(rs.first());
                    Assert.assertEquals(1, rs.getRow());
                } catch (final SQLException e) {
                    LOGGER.warn("Could not execute SQL", e);
                } finally {
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (final SQLException e1) {
                        LOGGER.warn("Could not close statement", e1);
                    }
                }
            }
            
        });
    }
    
    /**
     * 
     */
    @Test
    public void selectReturnsDataViaJdbcTemplate() {
        final String dbName = "selectreturnsdataviajdbctemplate";
        final String dbPassword = "";
        
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                final SQLAccess sqlAccess = mmData.getSQLAccess();
                final SimpleJdbcTemplate template = sqlAccess.getSimpleJdbcTemplate();
                final int count = template.queryForInt(
                    "select count(*) from Versions");
                LOGGER.info("There are " + count + " entries in Versions");
                Assert.assertTrue(count == 4);
            }
            
        });
    }
}
