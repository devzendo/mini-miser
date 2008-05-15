package uk.me.gumbley.minimiser.persistence;

/**
 * The base interface for database DAO factories.
 * 
 * @author matt
 *
 */
public interface Database {
    /**
     * Close the open database.
     */
    void close();
}
