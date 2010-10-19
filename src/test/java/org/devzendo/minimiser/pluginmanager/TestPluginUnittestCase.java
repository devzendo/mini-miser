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

import org.devzendo.commonspring.springloader.SpringLoadedBean;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.plugin.AppPlugin;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.junit.Assert;
import org.junit.Test;


/**
 * Do PluginUnitTestCases receive the environment they expect?
 * @author matt
 *
 */
@PluginUnderTest("org.devzendo.minimiser.plugin.AppPlugin")
public final class TestPluginUnittestCase extends PluginUnittestCase {
    /**
     * 
     */
    @Test
    public void pluginEnvironmentIsValid() {
        final ApplicationPlugin applicationPlugin = getApplicationPlugin();
        Assert.assertNotNull(applicationPlugin);
        final AppPlugin appPlugin = (AppPlugin) applicationPlugin;
        final SpringLoader springLoader = appPlugin.getSpringLoader();
        Assert.assertNotNull(springLoader);
        final SpringLoadedBean springLoadedBean = springLoader.getBean("testBean", SpringLoadedBean.class);
        Assert.assertNotNull(springLoadedBean);
        Assert.assertEquals(31415, springLoadedBean.getTheAnswer());
    }
}
