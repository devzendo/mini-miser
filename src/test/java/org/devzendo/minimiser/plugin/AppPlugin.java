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

package org.devzendo.minimiser.plugin;

import java.util.Arrays;
import java.util.List;

/**
 * @author matt
 *
 */
public final class AppPlugin extends AbstractPlugin implements ApplicationPlugin {
    private boolean mShutdownCalled;

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Application";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        final String[] contexts = new String[] {
                "org/devzendo/minimiser/pluginmanager/PluginAppContext.xml"
        };

        return Arrays.asList(contexts);
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
         mShutdownCalled = true;
    }

    /**
     * @return true iff shutdown has been called
     */
    public boolean hasShutdownBeenCalled() {
        return mShutdownCalled;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return "http://localhost/";
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return "bob@aol.com";
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return "(C) 2009 J. R. Bob Dobbs";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return "LICENSE.txt";
    }

    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return "About.txt";
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return "changelog.txt";
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }
}
