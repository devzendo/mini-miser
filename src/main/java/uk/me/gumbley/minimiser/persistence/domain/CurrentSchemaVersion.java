package uk.me.gumbley.minimiser.persistence.domain;

/**
 * The current version of the schema as understood by this version
 * of the app is recorded here.
 * 
 * For details of the changes in the schema, and for diagrams of the
 * relationships, see the docs/schema* directory.
 * 
 * @author matt
 *
 */
public interface CurrentSchemaVersion {
    /**
     * The current schema version.
     */
    String CURRENT_SCHEMA_VERSION = "1";
}
