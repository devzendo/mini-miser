package uk.me.gumbley.minimiser.pluginmanager;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An ApplicationPluginDescriptor holds all metadata about the
 * application plugin. It has no behaviour; that is retained in
 * the plugin itself.
 * 
 * @author matt
 *
 */
public final class ApplicationPluginDescriptor extends PluginDescriptor {
    private final String mDevelopersContactDetails;
    private final String mUpdateURL;
    private final String mShortLicenseDetails;
    private final String mFullLicenseDetailsResourcePath;
    private final String mAboutDetailsResourcePath;

    /**
     * Create an application plugin descriptor.
     * @param isApplication true iff an application plugin
     * @param name the application name
     * @param version the application version
     * @param schemaVersion the schema version
     * @param updateURL the URL of the update site
     * @param developersContactDetails the contact details of the
     * developers
     * @param shortLicenseDetails the licensing details of this
     * plugin
     * @param fullLicenseDetailsResourcePath the resource path of
     * the license text/HTML file
     * @param aboutDetailsResourcePath the resource path of the
     * about text/HTML file
     */
    public ApplicationPluginDescriptor(final boolean isApplication,
            final String name,
            final String version,
            final String schemaVersion,
            final String updateURL,
            final String developersContactDetails,
            final String shortLicenseDetails,
            final String fullLicenseDetailsResourcePath,
            final String aboutDetailsResourcePath) {
        super(isApplication, name, version, schemaVersion);
        mUpdateURL = updateURL;
        mDevelopersContactDetails = developersContactDetails;
        mShortLicenseDetails = shortLicenseDetails;
        mFullLicenseDetailsResourcePath = fullLicenseDetailsResourcePath;
        mAboutDetailsResourcePath = aboutDetailsResourcePath;
    }

    /**
     * @return the update site URL
     */
    public String getUpdateURL() {
        return mUpdateURL;
    }

    /**
     * @return the developers contact details
     */
    public String getDevelopersContactDetails() {
        return mDevelopersContactDetails;
    }
    
    /**
     * @return the licenseDetails
     */
    public String getShortLicenseDetails() {
        return mShortLicenseDetails;
    }

    /**
     * @return the fullLicenseDetailsResourcePath
     */
    public String getFullLicenseDetailsResourcePath() {
        return mFullLicenseDetailsResourcePath;
    }

    /**
     * @return the aboutDetailsResourcePath
     */
    public String getAboutDetailsResourcePath() {
        return mAboutDetailsResourcePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ApplicationPluginDescriptor)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ApplicationPluginDescriptor castObj = (ApplicationPluginDescriptor) obj;
        return new EqualsBuilder()
            .append(this.mUpdateURL, castObj.mUpdateURL)
            .append(this.mDevelopersContactDetails, castObj.mDevelopersContactDetails)
            .append(this.mShortLicenseDetails, castObj.mShortLicenseDetails)
            .append(this.mFullLicenseDetailsResourcePath, castObj.mFullLicenseDetailsResourcePath)
            .append(this.mAboutDetailsResourcePath, castObj.mAboutDetailsResourcePath)
            .isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // pick 2 hard-coded, odd, >0 ints as args
        return new HashCodeBuilder(1, 31)
            .append(super.hashCode())
            .append(this.mUpdateURL)
            .append(this.mDevelopersContactDetails)
            .append(this.mShortLicenseDetails)
            .append(this.mFullLicenseDetailsResourcePath)
            .append(this.mAboutDetailsResourcePath)
            .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" updateURL:");
        sb.append(mUpdateURL);
        sb.append(" contact:");
        sb.append(mDevelopersContactDetails);
        sb.append(" license:");
        sb.append(mShortLicenseDetails);
        sb.append(" license resource:");
        sb.append(mFullLicenseDetailsResourcePath);
        sb.append(" about resource:");
        sb.append(mAboutDetailsResourcePath);
        return sb.toString();
    }
}
