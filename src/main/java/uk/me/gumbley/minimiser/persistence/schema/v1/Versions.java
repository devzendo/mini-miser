package uk.me.gumbley.minimiser.persistence.schema.v1;

import java.io.Serializable;

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
public final class Versions implements Serializable {
    private static final long serialVersionUID = 6426905119929326605L;

    /**
     *  A Versions row with the entity SCHEMA_VERSION defines the version of
     *  this database's schema.
     *  
     *  The set of schema versions are monotonically increasing integers
     *  starting at 1.
     */
    public static final transient String SCHEMA_VERSION = "schema";

    /**
     *  A Versions row with the entity APPLICATION_VERSION defines the version
     *  of the application that created this database.
     *  
     *  The set of application versions are of the form x.y.z[-SNAPSHOT], i.e.
     *  the version of the MiniMiser artifact in the component repository.
     */
    public static final transient String APPLICATION_VERSION = "application";
    
    private String entity;
    private String version;
    
    /**
     * Empty ctor.
     */
    public Versions() {
        
    }
    
    /**
     * @return an identifier for a versionable entity.
     */
    public String getEntity() {
        return entity;
    }
    
    /**
     * @param entityString the identifier of the versionable entity. See the
     * static final Strings in this class for the possible values - these should
     * never change.
     */
    public void setEntity(final String entityString) {
        this.entity = entityString;
    }
    
    /**
     * @return the version string for this versionable entity
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * @param versionString the version of this versionable entity
     */
    public void setVersion(final String versionString) {
        this.version = versionString;
    }
}
