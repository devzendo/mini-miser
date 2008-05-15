package uk.me.gumbley.minimiser.persistence;

/**
 * A factory for the database schema's DAO objects, for migration up to the
 * latest version of the schema, rather than for normal application use.
 *  
 * @author matt
 *
 */
public interface MigratableDatabase extends Database {
}
