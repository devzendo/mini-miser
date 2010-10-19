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

import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.commonspring.springloader.SpringLoaderFactory;
import org.devzendo.minimiser.MiniMiserApplicationContexts;

/**
 * A factory for PluginHelpers, allows creation of correctly-
 * initialised helpers, using dummy, or real PluginManager, with
 * or without a correctly-initialised SpringLoader.
 * 
 * @author matt
 *
 */
public final class PluginHelperFactory {

    /**
     * Create a PluginHelper using the DummyPluginManager
     * @return the PluginHelper
     */
    public static PluginHelper createPluginHelperWithDummyPluginManager() {
        final PluginManager pluginManager = new DummyAppPluginManager();
        return new PluginHelper(pluginManager);
    }

    /**
     * Create a PluginHelper using the DefaultPluginManager and no
     * SpringLoader.
     * @return the PluginHelper
     */
    public static PluginHelper createPluginHelper() {
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();
        final PluginManager pluginManager = new DefaultPluginManager(null, pluginRegistry);
        return new PluginHelper(pluginManager);
    }

    /**
     * Create a PluginHelper using the DefaultPluginManager and a
     * SpringLoader pre-loaded with the standard framework
     * application contexts.
     * @return the PluginHelper
     */
    public static PluginHelper createMiniMiserPluginHelper() {
        final SpringLoader springLoader = SpringLoaderFactory.initialise(MiniMiserApplicationContexts.getApplicationContexts());
        final PluginManager pluginManager = springLoader.getBean("pluginManager", PluginManager.class);
        return new PluginHelper(pluginManager);
    }
}
