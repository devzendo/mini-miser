package uk.me.gumbley.minimiser.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Obtain various properties that may be set for testing
 * @author matt
 *
 */
public final class UnittestingConfig {
    private static final Logger LOGGER = Logger.getLogger(UnittestingConfig.class);
    private static final String LOAD_FAILURE = "Could not load test configuration file %s: %s";
    //
    private static final String TESTCONFIG_PROPERTIES = "testconfig.properties";
    //
    private static final String DATABASE_DIRECTORY_KEY = "database.directory";
    
    private Properties props;
    
    /**
     * Load up the test properties file. 
     */
    public UnittestingConfig() {
        props = new Properties();
        try {
            final InputStream inStream = new FileInputStream(TESTCONFIG_PROPERTIES);
            props.load(inStream);
        } catch (final FileNotFoundException e) {
            LOGGER.warn(String.format(LOAD_FAILURE, TESTCONFIG_PROPERTIES, e.getMessage()));
        } catch (final IOException e) {
            LOGGER.warn(String.format(LOAD_FAILURE, TESTCONFIG_PROPERTIES, e.getMessage()));
        } 
    }

    /**
     * @return a File describing the test database directory
     */
    public File getTestDatabaseDirectory() {
        final String dataDir = props.getProperty(DATABASE_DIRECTORY_KEY, null);
        if (dataDir != null) {
            return new File(dataDir);
        }
        return null;
    }
}
