package org.devzendo.minimiser.persistence.domain;

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
public abstract class CurrentSchemaVersion {
    private static String gCurrentSchemaVersion = "1";
    private CurrentSchemaVersion() {
        // no instances
    }
    
    /**
     * @return The current schema version.
     */
    public static String getCurrentSchemaVersion() {
        return gCurrentSchemaVersion;
    }
}
