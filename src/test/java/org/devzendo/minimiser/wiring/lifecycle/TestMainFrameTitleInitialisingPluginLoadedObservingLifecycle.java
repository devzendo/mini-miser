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

package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.StubMainFrameTitle;
import org.devzendo.minimiser.pluginmanager.DefaultPluginManager;
import org.devzendo.minimiser.pluginmanager.DefaultPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.junit.Assert;
import org.junit.Test;



/**
 * When the app plugin is loaded, does the title bar initialise?
 *
 * @author matt
 *
 */
public final class TestMainFrameTitleInitialisingPluginLoadedObservingLifecycle {

    /**
     * @throws PluginException never
     */
    @Test
    public void loadingPluginsChangesTheTitle() throws PluginException {
        final DefaultPluginRegistry pluginRegistry = new DefaultPluginRegistry();
        final DefaultPluginManager pluginManager = new DefaultPluginManager(null, pluginRegistry);

        final StubMainFrameTitle mainFrameTitle = new StubMainFrameTitle();
        Assert.assertNull(mainFrameTitle.getApplicationName());

        final MainFrameTitleInitialisingPluginLoadedObservingLifecycle lifecycle =
            new MainFrameTitleInitialisingPluginLoadedObservingLifecycle(
                pluginManager, mainFrameTitle);
        Assert.assertNull(mainFrameTitle.getApplicationName()); // not yet, mungo!
        lifecycle.startup();

        pluginManager.loadPlugins("org/devzendo/minimiser/plugin/goodplugin.properties");

        Assert.assertEquals("Application", mainFrameTitle.getApplicationName());
    }
}
