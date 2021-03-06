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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;



/**
 * Base class for plugin unit test cases. Allows plugins to
 * be declared via annotations, and loaded prior to @Test tests
 * execute.
 * @author matt
 *
 */
public abstract class PluginUnittestCase extends SpringLoaderUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(PluginUnittestCase.class);
    private PluginInitialiser mPluginInitialiser;
    private PluginRegistry mPluginRegistry;
    
    /**
     * Initialise the plugins declared in the @PluginUnderTest
     * annotation.
     */
    @Before
    public final void initialisePlugins() {
        mPluginRegistry = new DefaultPluginRegistry();
        mPluginInitialiser = new PluginInitialiser(getSpringLoader(), mPluginRegistry);
        final List<String> pluginList = findPluginsFromAnnotations();
        
        if (pluginList.size() == 0) {
            Assert.fail("No plugins defined via @PluginUnderTest annotation");
        }
        LOGGER.info(String.format("Initialising plugins [%s]", 
            StringUtils.join(pluginList, ", ")));
        
        try {
            final List<Plugin> loadedPlugins = loadPlugins(pluginList);
            mPluginInitialiser.initialisePlugins(loadedPlugins);
        } catch (final PluginException e) {
            Assert.fail(String.format("Could not load plugins: %s", e.getMessage()));
        }
    }
    
    /**
     * @return the application plugin, if there is one. Unit tests
     * that declare plugins via annotations are not required to
     * declare an application plugin, since you may want to test
     * a non-application plugin.
     */
    protected ApplicationPlugin getApplicationPlugin() {
        return mPluginInitialiser.getApplicationPlugin();
    }
    
    /**
     * @return all loaded plugins
     */
    protected List<Plugin> getPlugins() {
        return mPluginInitialiser.getPlugins();
    }
    
    private List<Plugin> loadPlugins(final List<String> pluginList) throws PluginException {
        final List<Plugin> loadedPlugins = new ArrayList<Plugin>(pluginList.size());
        for (String pluginClassName : pluginList) {
            // TODO: only Java, ATM
            try {
                LOGGER.info(String.format("Loading plugin from class %s", pluginClassName)); 
                final Class<?> loadedClass = Class.forName(pluginClassName);
                if (!Plugin.class.isAssignableFrom(loadedClass)) {
                    final String warning = String.format("Plugin class %s is not a Plugin", pluginClassName);
                    LOGGER.warn(warning);
                    throw new PluginException(warning);
                }
                try {
                    loadedPlugins.add((Plugin) loadedClass.newInstance());
                } catch (final InstantiationException e) {
                    final String warning = String.format("Cannot instantiate class %s: %s", pluginClassName, e.getMessage());
                    LOGGER.warn(warning);
                    throw new PluginException(warning);
                } catch (final IllegalAccessException e) {
                    final String warning = String.format("Illegal access during instantiation of class %s: %s", pluginClassName, e.getMessage());
                    LOGGER.warn(warning);
                    throw new PluginException(warning);
                }
            } catch (final ClassNotFoundException e) {
                final String warning = String.format("Cannot dynamically load class %s: %s", pluginClassName, e.getMessage());
                LOGGER.warn(warning);
                throw new PluginException(warning);
            }
        }
        return loadedPlugins;
    }

    private List<String> findPluginsFromAnnotations() {
        final List<String> pluginList = new ArrayList<String>();
        Class<? extends Object> clazz = this.getClass();
        // scan up to root of object hierarchy finding our annotation
        while (clazz != null) {
            final PluginUnderTest ac = clazz.getAnnotation(PluginUnderTest.class);
            if (ac != null) {
                pluginList.addAll(Arrays.asList(ac.value()));
            }
            clazz = clazz.getSuperclass();
        }
        return pluginList;
    }

    /**
     * After the test has run, shut down any plugins.
     */
    @After
    public final void shutdownPlugins() {
        for (Plugin plugin : mPluginInitialiser.getPlugins()) {
            plugin.shutdown();
        }
    }
}
