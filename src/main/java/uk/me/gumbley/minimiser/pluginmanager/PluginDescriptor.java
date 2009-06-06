package uk.me.gumbley.minimiser.pluginmanager;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A PluginDescriptor holds all metadata about a plugin. It has
 * no behaviour; that is retained in the plugin itself.
 * 
 * @author matt
 *
 */
public final class PluginDescriptor {
    private final boolean mIsApplication;
    private final String mName;
    private final String mVersion;
    private final String mSchemaVersion;
    private final String mUpdateURL;
    private final String mDevelopersMailAddress;

    /**
     * Create a plugin descriptor.
     * @param isApplication true iff an application plugin
     * @param name the application name
     * @param version the application version
     * @param schemaVersion the schema version
     * @param updateURL the URL of the update site
     * @param developersMailAddress the email address of the developers
     */
    public PluginDescriptor(final boolean isApplication,
            final String name,
            final String version,
            final String schemaVersion,
            final String updateURL,
            final String developersMailAddress) {
                mIsApplication = isApplication;
                mName = name;
                mVersion = version;
                mSchemaVersion = schemaVersion;
                mUpdateURL = updateURL;
                mDevelopersMailAddress = developersMailAddress;
    }

    /**
     * @return the isApplication
     */
    public boolean isApplication() {
        return mIsApplication;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * @return the schemaVersion
     */
    public String getSchemaVersion() {
        return mSchemaVersion;
    }

    /**
     * @return the update site URL
     */
    public String getUpdateURL() {
        return mUpdateURL;
    }

    /**
     * @return the developers email address
     */
    public String getDevelopersMailAddress() {
        return mDevelopersMailAddress;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PluginDescriptor)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final PluginDescriptor castObj = (PluginDescriptor) obj;
        return new EqualsBuilder()
            .append(this.mName, castObj.mName)
            .append(this.mVersion, castObj.mVersion)
            .append(this.mSchemaVersion, castObj.mSchemaVersion)
            .append(this.mUpdateURL, castObj.mUpdateURL)
            .append(this.mDevelopersMailAddress, castObj.mDevelopersMailAddress)
            .append(this.mIsApplication, castObj.mIsApplication)
            .isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // pick 2 hard-coded, odd, >0 ints as args
        return new HashCodeBuilder(1, 31)
            .append(this.mName)
            .append(this.mVersion)
            .append(this.mSchemaVersion)
            .append(this.mUpdateURL)
            .append(this.mDevelopersMailAddress)
            .append(this.mIsApplication)
            .toHashCode();
    }
}
