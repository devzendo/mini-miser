package org.devzendo.minimiser.plugin.facade.newdatabase;

import java.util.Map;

import javax.sql.DataSource;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.persistence.PersistenceObservableEvent;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


/**
 * Facade provided to allow databases to be populated with
 * plugin-supplied content, given the user input from
 * the File|New wizard .
 * 
 * @author matt
 *
 */
public interface NewDatabaseCreationFacade {
    /**
     * Given the user input from the File|New wizard, how many
     * steps will the plugin add to create tables and populate
     * them?
     * 
     * @param pluginProperties a map of name=value-object pairs
     * that has typically been provided by plugins via the File|New
     * wizard. Can be empty or null. Is passed to all plugins that
     * implement NewDatabaseCreation to use as input data whilst
     * creating or populating the database.
     * 
     * @return the number of creation steps this plugin will
     * perform, i.e. the number of times the observer will be
     * notified of progress
     */
    int getNumberOfDatabaseCreationSteps(Map<String, Object> pluginProperties);
    
    /**
     * Given user input from the File|New wizard, and access to the
     * database, create any resources necessary, informing the
     * user of progress via the observer as necessary.
     * @param dataSource the data source for other connection to
     * the database
     * @param jdbcTemplate the Spring JDBC Template access to the
     * database
     * @param observer an observer that should be notified of
     * creation events.
     * @param pluginProperties a map of name=value-object pairs
     * that has typically been provided by plugins via the File|New
     * wizard. Can be empty or null. Is passed to all plugins that
     * implement NewDatabaseCreation to use as input data whilst
     * creating or populating the database.
     */
    void createDatabase(DataSource dataSource,
            SimpleJdbcTemplate jdbcTemplate,
            Observer<PersistenceObservableEvent> observer,
            Map<String, Object> pluginProperties);
    
    /**
     * Given user input from the File|New wizard, and access to the
     * database, populate any resources necessary, informing the
     * user of progress via the observer as necessary.
     * 
     * @param jdbcTemplate the Spring JDBC Template access to the
     * database
     * @param dataSource the data source for other connection to
     * the database
     * @param observer an observer that should be notified of
     * creation events.
     * @param pluginProperties a map of name=value-object pairs
     * that has typically been provided by plugins via the File|New
     * wizard. Can be empty or null. Is passed to all plugins that
     * implement NewDatabaseCreation to use as input data whilst
     * creating or populating the database.
     */
    void populateDatabase(SimpleJdbcTemplate jdbcTemplate,
            SingleConnectionDataSource dataSource,
            Observer<PersistenceObservableEvent> observer,
            Map<String, Object> pluginProperties);
}
