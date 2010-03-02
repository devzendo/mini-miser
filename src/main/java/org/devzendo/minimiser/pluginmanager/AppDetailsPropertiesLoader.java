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
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Loads the framework's version and artifact name from the
 * framework.properties file that's filtered with the
 * project.version and project.artifactid Maven property.
 *
 * @author matt
 *
 */
public final class AppDetailsPropertiesLoader {
    private static final String PROJECT_VERSION = "project.version";
    private static final String PROJECT_NAME = "project.name";
    private static final String FRAMEWORK_PROPERTIES = "org/devzendo/minimiser/pluginmanager/framework.properties";

    private static final Logger LOGGER = Logger
            .getLogger(AppDetailsPropertiesLoader.class);

    private final String mVersion;
    private final String mName;

    /**
     *
     */
    public AppDetailsPropertiesLoader() {
        LOGGER.debug("Loading version from " + FRAMEWORK_PROPERTIES);
        String version = null;
        String name = null;
        try {
            final InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(
                FRAMEWORK_PROPERTIES);
            if (inputStream == null) {
                LOGGER.warn("There is no " + FRAMEWORK_PROPERTIES + " resource");
                return;
            }
            final Properties props = new Properties();
            try {
                props.load(inputStream);
                version = props.getProperty(PROJECT_VERSION);
                if (version == null) {
                    LOGGER.warn("No " + PROJECT_VERSION + " is defined in " + FRAMEWORK_PROPERTIES);
                } else {
                    LOGGER.info("Framework version " + version);
                }
                name = props.getProperty(PROJECT_NAME);
                if (name == null) {
                    LOGGER.warn("No " + PROJECT_NAME + " is defined in " + FRAMEWORK_PROPERTIES);
                } else {
                    LOGGER.info("Framework name " + name);
                }
            } catch (final IOException e) {
                LOGGER.warn("Could not load filtered properties from " + FRAMEWORK_PROPERTIES
                    + ": " + e.getMessage());
            }
        } finally {
            mVersion = version;
            mName = name;
        }
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }
}
