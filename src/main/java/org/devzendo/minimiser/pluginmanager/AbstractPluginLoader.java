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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * Implements functionality common to PluginLoaders.
 * @author matt
 *
 */
public abstract class AbstractPluginLoader implements PluginLoader {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractPluginLoader.class);

    /**
     * Given a resource path, return all URLs pointing to this on
     * the current classpath
     * @param resourcePath the resource path
     * @return an Enumeration<URL> of instances
     * @throws IOException on classpath scanning failure
     */
    protected Enumeration<URL> getPluginDescriptorURLs(final String resourcePath) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResources(resourcePath);
    }

    /**
     * {@inheritDoc}
     */
    public final void displayPluginDescriptorResources(final String propertiesResourcePath) {
        LOGGER.debug("List of plugin descriptor resources: " + propertiesResourcePath);
        try {
            final Enumeration<URL> resources =
                getPluginDescriptorURLs(propertiesResourcePath);
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                LOGGER.debug("Plugin descriptor resource is " + resource);
            }
            LOGGER.debug("End of plugin descriptor list");
        } catch (final IOException e) {
            LOGGER.warn("Could not obtain list of plugin descriptor resources for " + propertiesResourcePath, e);
        }
    }
}
