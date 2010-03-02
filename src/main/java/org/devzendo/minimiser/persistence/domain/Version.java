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

package org.devzendo.minimiser.persistence.domain;

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
    private boolean mIsApplication;

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
     * @param isApplication is this versionable entity from an
     * application or normal plugin?
     */
    public void setIsApplication(final boolean isApplication) {
        mIsApplication = isApplication;
    }

    /**
     * Construct a new Version domain object
     * @param pluginName the plugin name
     * @param entityName the entity name
     * @param isApplication true iff created by an application
     * plugin
     * @param version its version
     */
    public Version(final String pluginName, final String entityName,
            final boolean isApplication, final String version) {
        mPluginName = pluginName;
        mEntityName = entityName;
        mIsApplication = isApplication;
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

    /**
     * @return true iff this versionable entity was created by
     * the application plugin; false iff a normal plugin created
     * it
     */
    public boolean isApplication() {
        return mIsApplication;
    }
}