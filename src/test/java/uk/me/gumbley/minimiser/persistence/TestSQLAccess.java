package uk.me.gumbley.minimiser.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess.ResultType;


/**
 * Tests the low-level access to the database, for migration, parsing SQL, etc.
 * @author matt
 *
 */
public final class TestSQLAccess extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestSQLAccess.class);
    
    private AccessFactory accessFactory;
    
    /**
     * Grab the configured AccessFactory from the Spring App Context.
     */
    @Before
    public void getAccessFactory() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
    }
    
    /**
     * 
     */
    @Test
    public void selectParsedOK() {
        final String dbName = "selectparsedok";
        final String dbPassword = "";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, dbPassword);
        try {
            final SQLAccess sqlAccess = mmData.getSQLAccess();
            final ResultType type = sqlAccess.parse("SELECT * FROM Versions");
            Assert.assertSame(ResultType.ResultSet, type);
        } finally {
            mmData.close();
            deleteDatabaseFiles(dbName);
        }
    }
    
    /**
     * 
     */
    @Test
    public void selectReturnsData() {
        final String dbName = "selectreturnsdata";
        final String dbPassword = "";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, dbPassword);
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
                LOGGER.warn("Could not close", e1);
            }
            mmData.close();
            deleteDatabaseFiles(dbName);
        }
    }
}
