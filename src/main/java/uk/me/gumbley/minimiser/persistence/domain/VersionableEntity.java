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
     * A Versions row with the entity SCHEMA_VERSION defines the
     * version of this plugin's database schema.
     *  
     * The set of schema versions are ComparableVersions, which
     * can be monotonically increasing integers starting at 1.
     * They can have sub-versions, e.g. 1.0 < 1.1 < 1.2.
     */
    public static final String SCHEMA_VERSION = "schema";
    
    /**
     * A Versions row with the entity APPLICATION_VERSION defines
     * the version of the plugin code that created this database.
     *  
     * The set of plugin versions are of the form x.y.z[-SNAPSHOT],
     * i.e. the version of the plugin artifact in the component
     * repository: ComparableVersions, e.g. 1.0 < 1.1 < 1.2.
     * 
     * TODO: rename this to PLUGIN_CODE_VERSION to reduce confusion
     * with the Version.isApplication method
     */
    public static final String APPLICATION_VERSION = "application";
}
