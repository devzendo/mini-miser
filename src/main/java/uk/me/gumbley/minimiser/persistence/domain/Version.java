package uk.me.gumbley.minimiser.persistence.domain;

/**
 * Describes a versionable entity.
 * 
 * @author matt
 *
 */
public final class Version {
    private String mPluginName;
    private String mEntityName;
    private String mVersion;

    /**
     * Construct an empty Version
     */
    public Version() {
    }
    
    /**
     * @param pluginName the plugin name of this versionable entity
     */
    public void setPluginName(final String pluginName) {
        mPluginName = pluginName;
    }

    /**
     * @param entityName the entity name of this versionable entity
     */
    public void setEntity(final String entityName) {
        mEntityName = entityName;
    }

    /**
     * @param version the version description of this versionable entity
     */
    public void setVersion(final String version) {
        mVersion = version;
    }

    /**
     * Construct a new Version domain object
     * @param pluginName the plugin name
     * @param entityName the entity name
     * @param version its version
     */
    public Version(final String pluginName, final String entityName, final String version) {
        mPluginName = pluginName;
        mEntityName = entityName;
        mVersion = version;
    }
    
    /**
     * @return the name of the plugin responsible for this part of
     * the database schema 
     */
    public String getPluginName() {
        return mPluginName;
    }

    /**
     * @return an identifier for a versionable entity.
     */
    public String getEntity() {
        return mEntityName;
    }

    /**
     * @return the version string for this versionable entity
     */
    public String getVersion() {
        return mVersion;
    }
}