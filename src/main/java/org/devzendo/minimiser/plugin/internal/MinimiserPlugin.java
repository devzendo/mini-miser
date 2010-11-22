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

package org.devzendo.minimiser.plugin.internal;

import java.util.List;

import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.minimiser.persistence.domain.CurrentSchemaVersion;
import org.devzendo.minimiser.plugin.AbstractPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.AppDetailsPropertiesLoader;


/**
 * A normal plugin that declares the version of the MiniMiser
 * framework, and the database schema, so that this is set in
 * the Versions table.
 * 
 * @author matt
 *
 */
public final class MinimiserPlugin extends AbstractPlugin implements Plugin {
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
        return getAppDetailsPropertyLoader().getName();
    }

    private AppDetailsPropertiesLoader getAppDetailsPropertyLoader() {
        final SpringLoader springLoader = getSpringLoader();
        assert springLoader != null;
        final AppDetailsPropertiesLoader appDetailsPropertiesLoader =
            springLoader.getBean(
                "appDetailsPropertiesLoader", 
                AppDetailsPropertiesLoader.class);
        assert appDetailsPropertiesLoader != null;
        return appDetailsPropertiesLoader;
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return CurrentSchemaVersion.getCurrentSchemaVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return getAppDetailsPropertyLoader().getVersion();
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }
}
