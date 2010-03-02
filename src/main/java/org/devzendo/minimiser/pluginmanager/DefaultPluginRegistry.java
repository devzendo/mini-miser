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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * The default implementation of the PluginRegistry.
 * 
 * @author matt
 *
 */
public final class DefaultPluginRegistry implements PluginRegistry {
    private final Set<PluginDescriptor> mPluginDescriptors;
    private ApplicationPluginDescriptor mApplicationPluginDescriptor;

    /**
     * Construct the default plugin registry
     */
    public DefaultPluginRegistry() {
        mPluginDescriptors = new HashSet<PluginDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationPluginDescriptor getApplicationPluginDescriptor() {
        return mApplicationPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public List<PluginDescriptor> getPluginDescriptors() {
        return new ArrayList<PluginDescriptor>(mPluginDescriptors);
    }

    /**
     * {@inheritDoc}
     */
    public void addPluginDescriptor(final PluginDescriptor pluginDescriptor) {
        if (pluginDescriptor.isApplication()) {
            if (mApplicationPluginDescriptor != null && !mApplicationPluginDescriptor.equals(pluginDescriptor)) {
                throw new IllegalStateException("Cannot add multiple application plugin descriptors");
            }
            mApplicationPluginDescriptor = (ApplicationPluginDescriptor) pluginDescriptor;
        }
        mPluginDescriptors.add(pluginDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() {
        if (mApplicationPluginDescriptor != null) {
            final String appName = mApplicationPluginDescriptor.getName();
            if (!StringUtils.isBlank(appName)) {
                return appName;
            }
        }
        return UNKNOWN_APPLICATION;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() {
        if (mApplicationPluginDescriptor != null) {
            final String appVersion = mApplicationPluginDescriptor.getVersion();
            if (!StringUtils.isBlank(appVersion)) {
                return appVersion;
            }
        }
        return UNKNOWN_VERSION;
    }
}
