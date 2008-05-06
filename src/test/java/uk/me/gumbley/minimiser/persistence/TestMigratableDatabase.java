package uk.me.gumbley.minimiser.persistence;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Basic tests for low level (JDBC) access to databases.
 * 
 * @author matt
 *
 */
public class TestMigratableDatabase extends PersistenceTestCase {
    private JdbcAccess jdbcAccess;
    @Before
    public void getJdbcAccess() {
        jdbcAccess = getSpringLoader().getBean("jdbcAccess", JdbcAccess.class);
    }
    @Test(expected = DataAccessException.class)
    public void testNonExistantDatabaseThrowsUpOnOpen() throws BadPasswordException, DataAccessException {
        String dbDir = getAbsoluteDatabaseDirectory("nonexistant");
        Assert.assertFalse(new File(dbDir).exists());
        MigratableDatabase migData = jdbcAccess.openDatabase(dbDir, null);
        // shouldn't get here, but if we do, close
        migData.close();
    }
    
}
