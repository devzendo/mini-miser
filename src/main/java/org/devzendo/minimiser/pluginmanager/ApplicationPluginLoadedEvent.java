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



/**
 * The ApplicationPlugin has been loaded, and here are its details.
 * 
 * @author matt
 *
 */
public final class ApplicationPluginLoadedEvent extends PluginEvent {
    private final PluginDescriptor mPluginDescriptor;

    /**
     * @param pluginDescriptor the plugin descriptor of the application plugin
     */
    public ApplicationPluginLoadedEvent(final PluginDescriptor pluginDescriptor) {
        mPluginDescriptor = pluginDescriptor;
    }

    /**
     * @return the pluginDescriptor
     */
    public PluginDescriptor getPluginDescriptor() {
        return mPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return mPluginDescriptor.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationPluginLoadedEvent other = (ApplicationPluginLoadedEvent) obj;
        if (mPluginDescriptor == null) {
            if (other.mPluginDescriptor != null) {
                return false;
            }
        } else if (!mPluginDescriptor.equals(other.mPluginDescriptor)) {
            return false;
        }
        return true;
    }
}
