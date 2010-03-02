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

/**
 * A singleton bean that is used everywhere to obtain the current
 * application name and version. Defaults to the name and version
 * of the framework until it is updated by the PluginManager
 * when the ApplicationPlugin has been loaded.
 *  
 * @author matt
 * @deprecated use PluginRegistry
 *
 */
@Deprecated
public final class AppDetails {
    private static final String UNKNOWN = "unknown";
    
    private String mApplicationName = UNKNOWN;
    private String mApplicationVersion = UNKNOWN;
    
    /**
     * @param applicationName the applicationName to set
     */
    public synchronized void setApplicationName(final String applicationName) {
        if (applicationName == null || applicationName.length() == 0) {
            throw new IllegalArgumentException("Cannot set a null or empty application name");
        }
        mApplicationName = applicationName;
    }

    /**
     * @param applicationVersion the applicationVersion to set
     */
    public synchronized void setApplicationVersion(final String applicationVersion) {
        if (applicationVersion == null || applicationVersion.length() == 0) {
            throw new IllegalArgumentException("Cannot set a null or empty application version");
        }
        mApplicationVersion = applicationVersion;
    }

    /**
     * @return the application name, or "UNKNOWN" if not set.
     */
    public synchronized String getApplicationName() {
        return mApplicationName;
    }

    /**
     * @return the application version, or "UNKNOWN" if not set.
     */
    public synchronized String getApplicationVersion() {
        return mApplicationVersion;
    }
    
    /**
     * @return true iff the application version is set to
     * something
     */
    public synchronized boolean isApplicationVersionSet() {
        return !mApplicationVersion.equals(UNKNOWN);
    }
}
