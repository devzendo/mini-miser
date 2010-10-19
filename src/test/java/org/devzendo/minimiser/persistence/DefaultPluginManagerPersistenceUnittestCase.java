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

package org.devzendo.minimiser.persistence;

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Persistent tests that need real plugins can subclass this
 * and define their plugins in their application context.
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/persistence/DefaultPluginManagerTestCase.xml")
public abstract class DefaultPluginManagerPersistenceUnittestCase extends
        PersistenceUnittestCase {
    /**
     * @return the plugin manager
     */
    public final PluginManager getPluginManager() {
        return getSpringLoader().getBean("pluginManager", PluginManager.class);
    }

    /**
     * @return the plugin registry
     */
    public final PluginRegistry getPluginRegistry() {
        return getSpringLoader().getBean("pluginRegistry", PluginRegistry.class);
    }
}
