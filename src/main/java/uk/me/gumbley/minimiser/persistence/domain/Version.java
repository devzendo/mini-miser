package uk.me.gumbley.minimiser.persistence.domain;

/**
 * Describes a versionable entity.
 * 
 * @author matt
 *
 */
public class Version {
    private String entityName;
    private String versionDesc;

    /**
     * Construct an empty Version
     */
    public Version() {
    }
    
    /**
     * @param entity the entity name (PK) of this versionable entity
     */
    public void setEntity(final String entity) {
        this.entityName = entity;
    }

    /**
     * @param version the version description of this versionable entity
     */
    public void setVersion(final String version) {
        this.versionDesc = version;
    }

    /**
     * Construct a new Version domain object
     * @param entity the entity name
     * @param version its version
     */
    public Version(final String entity, final String version) {
        this.entityName = entity;
        this.versionDesc = version;
    }
    
    /**
     * @return an identifier for a versionable entity.
     */
    public String getEntity() {
        return entityName;
    }

    /**
     * @return the version string for this versionable entity
     */
    public String getVersion() {
        return versionDesc;
    }
}