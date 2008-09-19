package uk.me.gumbley.minimiser.persistence.domain;

/**
 * The current version of the schema as understood by this version
 * of the app is recorded here.
 * 
 * For details of the changes in the schema, and for diagrams of the
 * relationships, see the docs/schema directory.
 * 
 * @author matt
 *
 */
public final class CurrentSchemaVersion {
    
    /**
     * Don't construct
     */
    private CurrentSchemaVersion() {
        // no instances
    }
    /**
     * The current schema version.
     */
    public static final String CURRENT_SCHEMA_VERSION = "1";
}
