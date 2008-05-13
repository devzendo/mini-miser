package uk.me.gumbley.minimiser.persistence.domain;

/**
 * Describes a versionable entity.
 * 
 * @author matt
 *
 */
public class Version {
    private final String entity;
    private final String version;

    /**
     * Construct a new Version domain object
     * @param ent the entity name
     * @param ver its version
     */
    public Version(final String ent, final String ver) {
        this.entity = ent;
        this.version = ver;
    }
    
    /**
     * @return an identifier for a versionable entity.
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @return the version string for this versionable entity
     */
    public String getVersion() {
        return version;
    }
}