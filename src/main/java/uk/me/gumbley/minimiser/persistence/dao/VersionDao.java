package uk.me.gumbley.minimiser.persistence.dao;

import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.persistence.domain.Version;

/**
 * Describes all versionable entities in the application, and their version
 * number strings.
 * 
 * For example, the database schema has a version, as does the application.
 * 
 * This bean/table is present in all versions of the schema, and is used to
 * determine which version of the application created the opened database, and
 * which version of the schema is present, for migration.
 * 
 * @author matt
 *
 */
public interface VersionDao {
    /**
     * Find a Version for a given entity
     * @param entity the name of the entity whose version should be found 
     * @return a Version for a versionable entity.
     */
    Version findVersion(String entity) throws DataAccessException;

    /**
     * Store a specific Version
     *  
     * @param version the version to store
     */
    void persistVersion(Version version) throws DataAccessException;
}