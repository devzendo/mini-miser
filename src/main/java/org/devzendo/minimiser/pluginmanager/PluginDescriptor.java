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
 * A PluginDescriptor holds all metadata about a plugin. It has
 * no behaviour; that is retained in the plugin itself.
 * 
 * @author matt
 *
 */
public class PluginDescriptor {
    private final boolean mIsApplication;
    private final String mName;
    private final String mVersion;
    private final String mSchemaVersion;

    /**
     * Create a plugin descriptor.
     * @param isApplication true iff an application plugin
     * @param name the application name
     * @param version the application version
     * @param schemaVersion the schema version
     */
    public PluginDescriptor(final boolean isApplication,
            final String name,
            final String version,
            final String schemaVersion) {
        mIsApplication = isApplication;
        mName = name;
        mVersion = version;
        mSchemaVersion = schemaVersion;
    }

    /**
     * @return the isApplication
     */
    public final boolean isApplication() {
        return mIsApplication;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return mName;
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return mVersion;
    }

    /**
     * @return the schemaVersion
     */
    public final String getSchemaVersion() {
        return mSchemaVersion;
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
            .append(this.mIsApplication)
            .toHashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(mIsApplication ? "App" : "Plugin");
        sb.append(" name ");
        sb.append(mName);
        sb.append(" versions: code:");
        sb.append(mVersion);
        sb.append(" schema:");
        sb.append(mSchemaVersion);
        return sb.toString();
    }
}
