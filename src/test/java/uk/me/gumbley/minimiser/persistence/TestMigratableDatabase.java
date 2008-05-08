package uk.me.gumbley.minimiser.persistence;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;


/**
 * Basic tests for low level (JDBC) access to databases.
 * 
 * @author matt
 *
 */
public class TestMigratableDatabase extends PersistenceTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestMigratableDatabase.class);
    private JdbcAccess jdbcAccess;
    @Before
    public void getJdbcAccess() {
        jdbcAccess = getSpringLoader().getBean("jdbcAccess", JdbcAccess.class);
    }
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNonExistantDatabaseThrowsUpOnOpen() throws BadPasswordException, DataAccessException {
        LOGGER.info("*** testNonExistantDatabaseThrowsUpOnOpen");
        String dbDir = getAbsoluteDatabaseDirectory("nonexistant");
        Assert.assertFalse(new File(dbDir).exists());
//        try {
            LOGGER.info("*** testNonExistantDatabaseThrowsUpOnOpen just before");
            MigratableDatabase migData = jdbcAccess.openDatabase(dbDir, null);
            LOGGER.info("*** testNonExistantDatabaseThrowsUpOnOpen just after");
            // shouldn't get here, but if we do, close
            migData.close();
//        } catch (Throwable re) { // DataAccessResourceFailureException
//            LOGGER.info(String.format("Could not call openDatabase: Caught a %s: %s", re.getClass().getName(), re.getMessage()));
//        }
    }
    
    /**
     * How can we get a DataSource to feed to a Spring JdbcTemplate? 
     */
    /*@Test
    public void testGetJdbcTemplate() {
        
    }*/
}
