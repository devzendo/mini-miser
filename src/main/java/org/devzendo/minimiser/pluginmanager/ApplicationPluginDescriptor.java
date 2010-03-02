/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.pluginmanager;

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
    private final String mChangeLogResourcePath;
    private final String mIntroPanelBackgroundGraphicResourcePath;

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
     * @param changeLogResourcePath the resource path of the
     * change log markup file
     * @param introPanelBackgroundGraphicResourcePath the
     * resource path of the intro panel background graphic
     */
    public ApplicationPluginDescriptor(final boolean isApplication,
            final String name,
            final String version,
            final String schemaVersion,
            final String updateURL,
            final String developersContactDetails,
            final String shortLicenseDetails,
            final String fullLicenseDetailsResourcePath,
            final String aboutDetailsResourcePath,
            final String changeLogResourcePath,
            final String introPanelBackgroundGraphicResourcePath) {
        super(isApplication, name, version, schemaVersion);
        mUpdateURL = updateURL;
        mDevelopersContactDetails = developersContactDetails;
        mShortLicenseDetails = shortLicenseDetails;
        mFullLicenseDetailsResourcePath = fullLicenseDetailsResourcePath;
        mAboutDetailsResourcePath = aboutDetailsResourcePath;
        mChangeLogResourcePath = changeLogResourcePath;
        mIntroPanelBackgroundGraphicResourcePath = introPanelBackgroundGraphicResourcePath;
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
     * @return the changeLogResourcePath
     */
    public String getChangeLogResourcePath() {
        return mChangeLogResourcePath;
    }

    /**
     * @return the introPanelBackgroundGraphicResourcePath
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return mIntroPanelBackgroundGraphicResourcePath;
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
            .append(this.mChangeLogResourcePath, castObj.mChangeLogResourcePath)
            .append(this.mIntroPanelBackgroundGraphicResourcePath, castObj.mIntroPanelBackgroundGraphicResourcePath)
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
            .append(this.mChangeLogResourcePath)
            .append(this.mIntroPanelBackgroundGraphicResourcePath)
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
        sb.append(" change log resource:");
        sb.append(mChangeLogResourcePath);
        sb.append(" intro panel resource:");
        sb.append(mIntroPanelBackgroundGraphicResourcePath);
        return sb.toString();
    }
}
