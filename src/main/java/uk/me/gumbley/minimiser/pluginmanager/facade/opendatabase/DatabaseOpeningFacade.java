package uk.me.gumbley.minimiser.pluginmanager.facade.opendatabase;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.util.InstancePair;

/**
 * Facade provided to allow plugins to create DAOFactory
 * instances in order to provide access to their custom data.
 * 
 * @author matt
 *
 */
public interface DatabaseOpeningFacade {

    /**
     * Create a DAOFactory that provides DAOs to access the given
     * database.
     * 
     * @param jdbcTemplate the Spring JDBC Template access to the
     * database
     * @param dataSource the data source for other connection to
     * the database
     * @return your plugin's DAOFactory sub-interface by which the
     * instance will later be retrieved by your plugin, and an
     * instance of this type, encapsulated in an InstancePair
     * 
     */
    InstancePair<DAOFactory> createDAOFactory(
            SimpleJdbcTemplate jdbcTemplate,
            SingleConnectionDataSource dataSource);
}
