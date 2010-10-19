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

import org.devzendo.commonspring.springloader.SpringLoader;


/**
 * An application plugin that verifies that getName() and
 * getVersion() are not called until Spring initialisation has
 * taken place.
 *
 * @author matt
 *
 */
public final class SpringContractPlugin implements ApplicationPlugin {
    private SpringLoader mSpringLoader;
    private boolean mGotSpringLoader = false;
    private boolean mAppContextsAdded = false;
    private boolean mFirstGetName = true;
    private boolean mGetNameCalledOK = false;
    private boolean mFirstGetVersion = true;
    private boolean mGetVersionCalledOK = false;

    /**
     * {@inheritDoc}
     */
    public String getName() {
        if (mFirstGetName) {
            mFirstGetName = false;
            mGetNameCalledOK = mGotSpringLoader && mAppContextsAdded;
        }
        return "Application";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        if (mFirstGetVersion) {
            mFirstGetVersion = false;
            mGetVersionCalledOK = mGotSpringLoader && mAppContextsAdded;
        }
        return "1.0.0";
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        final String[] contexts = new String[] {
                "org/devzendo/minimiser/pluginmanager/PluginAppContext.xml"
        };

        mAppContextsAdded = true;
        return Arrays.asList(contexts);
    }

    /**
     * {@inheritDoc}
     */
    public SpringLoader getSpringLoader() {
        return mSpringLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void setSpringLoader(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mGotSpringLoader = true;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAfter() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getBefore() {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
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
     * @return true iff getName and getVerison have been called, before the
     * app contexts have been given and spring loader set.
     */
    public boolean nameAndVersionNotCalledUntilSpringInitialised() {
        return mGetNameCalledOK && mGetVersionCalledOK;
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
