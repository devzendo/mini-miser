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

import java.util.ArrayList;
import java.util.List;

import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;


/**
 * A Plugin Manager for tests that returns a single App, and a
 * single normal plugin.
 *
 * @author matt
 *
 */
public final class DummyAppPluginManager implements PluginManager {
    private static final String APP_PLUGIN_NAME = "Dummy App";
    private static final String APP_PLUGIN_VERSION = "1.0.0";
    private static final String PLUGIN_NAME = "Dummy Plugin";
    private static final String PLUGIN_VERSION = "3.2.1";
    private final ObserverList<PluginEvent> mObserverList;
    private final ApplicationPlugin mApplicationPlugin;
    private final Plugin mDummyPlugin;

    /**
     * The simplest plugin manager that could possibly work
     */
    public DummyAppPluginManager() {
        mObserverList = new ObserverList<PluginEvent>();
        mApplicationPlugin = new ApplicationPlugin() {

            public List<String> getApplicationContextResourcePaths() {
                return null;
            }

            public String getName() {
                return APP_PLUGIN_NAME;
            }

            public SpringLoader getSpringLoader() {
                return null;
            }

            public String getUpdateSiteBaseURL() {
                return null;
            }

            public String getVersion() {
                return APP_PLUGIN_VERSION;
            }

            public void setSpringLoader(
                    final SpringLoader springLoader) {
            }

            public void shutdown() {
            }

            public String getDevelopersContactDetails() {
                return null;
            }

            public String getShortLicenseDetails() {
                return null;
            }

            public String getSchemaVersion() {
                return "1.0";
            }

            public String getFullLicenceDetailsResourcePath() {
                return null;
            }

            public String getAboutDetailsResourcePath() {
                return null;
            }

            public String getChangeLogResourcePath() {
                return null;
            }

            public String getIntroPanelBackgroundGraphicResourcePath() {
                return null;
            }
        };
        mDummyPlugin = new Plugin() {

            public List<String> getApplicationContextResourcePaths() {
                return null;
            }

            public String getName() {
                return PLUGIN_NAME;
            }

            public SpringLoader getSpringLoader() {
                return null;
            }

            public String getVersion() {
                return PLUGIN_VERSION;
            }

            public void setSpringLoader(final SpringLoader springLoader) {
            }

            public void shutdown() {
            }

            public String getSchemaVersion() {
                return "0.1";
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationPlugin getApplicationPlugin() {
        return mApplicationPlugin;
    }

    /**
     * {@inheritDoc}
     */
    public List<Plugin> getPlugins() {
        final ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
        pluginList.add(mDummyPlugin);
        pluginList.add(mApplicationPlugin);
        return pluginList;
    }

    /**
     * {@inheritDoc}
     */
    public void loadPlugins(final String propertiesResourcePath)
            throws PluginException {
        mObserverList.eventOccurred(
            new ApplicationPluginLoadedEvent(
                new ApplicationPluginDescriptor(true,
                    getApplicationPlugin().getName(),
                    getApplicationPlugin().getVersion(),
                    getApplicationPlugin().getSchemaVersion(),
                    getApplicationPlugin().getUpdateSiteBaseURL(),
                    getApplicationPlugin().getDevelopersContactDetails(),
                    getApplicationPlugin().getShortLicenseDetails(),
                    getApplicationPlugin().getFullLicenceDetailsResourcePath(),
                    getApplicationPlugin().getAboutDetailsResourcePath(),
                    getApplicationPlugin().getChangeLogResourcePath(),
                    getApplicationPlugin().getIntroPanelBackgroundGraphicResourcePath())));
    }

    /**
     * {@inheritDoc}
     */
    public void addPluginEventObserver(final Observer<PluginEvent> observer) {
        mObserverList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public void removePluginEventObserver(final Observer<PluginEvent> observer) {
        mObserverList.removeListener(observer);
    }

    /**
     * {@inheritDoc}
     */
    public <F> List<F> getPluginsImplementingFacade(final Class<F> facadeType) {
        return new ArrayList<F>();
    }
}
