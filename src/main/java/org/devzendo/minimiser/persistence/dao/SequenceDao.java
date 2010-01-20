package org.devzendo.minimiser.persistence.dao;

/**
 * Describes the database-wide sequence provider.
 * 
 * This bean/sequence is present in all versions of the schema.
 * 
 * @author matt
 *
 */
public interface SequenceDao {
    /**
     * Obtain the next identifier in the sequence.
     * @return the next identifier.
     */
    long getNextSequence();
}
