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

package org.devzendo.minimiser.gui.menu;

import java.util.List;

import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProviding;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProvidingFacade;
import org.springframework.beans.factory.CannotLoadBeanClassException;


/**
 * A plugin that customises the ApplicationMenu.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeAppPlugin implements ApplicationPlugin, MenuProviding {
    private final MenuProvidingFacade mMenuProvidingFacade;
    private boolean mFailOnLoad;

    /**
     *
     */
    public MenuProvidingFacadeAppPlugin() {
        mFailOnLoad = false;
        mMenuProvidingFacade = new StubMenuProvidingFacade();
    }

    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "MenuProvidingFacadeAppPlugin";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1";
    }

    /**
     * {@inheritDoc}
     */
    public SpringLoader getSpringLoader() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "0.1";
    }

    /**
     * {@inheritDoc}
     */
    public void setSpringLoader(final SpringLoader springLoader) {
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
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
    public MenuProvidingFacade getMenuProvidingFacade() {
        if (mFailOnLoad) {
            // simulate a common fault, e.g. bean not found
            throw new CannotLoadBeanClassException(
                "Cannot find class", "badMenuProvidingFacade",
                "org.devzendo.minimiser.gui.menu.BadMenuProvidingFacade",
                new ClassNotFoundException());
        }
        return mMenuProvidingFacade;
    }

    /**
     * Set a bomb for getMenuProvidingFacade() to explode
     */
    void injectFacadeLoadFailure() {
        mFailOnLoad  = true;
    }
}
