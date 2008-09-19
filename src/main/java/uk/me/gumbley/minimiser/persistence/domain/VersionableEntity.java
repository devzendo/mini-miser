package uk.me.gumbley.minimiser.persistence.domain;

/**
 * Constants for the possible values of the Versions.Entity field.
 *  
 * @author matt
 *
 */
public final class VersionableEntity {
    
    /**
     * don't construct 
     */
    private VersionableEntity() {
        // no instances
    }
    
    /**
     *  A Versions row with the entity SCHEMA_VERSION defines the version of
     *  this database's schema.
     *  
     *  The set of schema versions are monotonically increasing integers
     *  starting at 1.
     */
    public static final String SCHEMA_VERSION = "schema";
    
    /**
     *  A Versions row with the entity APPLICATION_VERSION defines the version
     *  of the application that created this database.
     *  
     *  The set of application versions are of the form x.y.z[-SNAPSHOT], i.e.
     *  the version of the MiniMiser artifact in the component repository.
     */
    public static final String APPLICATION_VERSION = "application";
}
