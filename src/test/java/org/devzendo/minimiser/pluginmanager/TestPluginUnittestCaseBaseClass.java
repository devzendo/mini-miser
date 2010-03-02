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

import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests the correct behaviour of the PluginUnittestCase,
 * by manually making the calls that JUnit would make.
 * Can't subclass it directly in here, 'cos its @Before
 * will throw before we get started, so we can't test for
 * its validation code.
 * 
 * @author matt
 *
 */
public final class TestPluginUnittestCaseBaseClass  {
    
    private static final class NoPluginsDeclaredSubclass extends PluginUnittestCase {
        
    }
    
    /**
     * no annotations are invalid
     */
    @Test(expected = AssertionError.class)
    public void noDeclaredPluginsCauseThrow() {
        final NoPluginsDeclaredSubclass subClass = new NoPluginsDeclaredSubclass();
        subClass.initialisePlugins(); // called by JUnit via @Before
    }


    @PluginUnderTest("org.devzendo.minimiser.plugin.AppPlugin")
    private static final class PluginDeclaredSubclass extends
    PluginUnittestCase {
    }

    /**
     * 
     */
    @Test
    public void declaredPluginIsLoadedCorrectly() {
        final PluginDeclaredSubclass subClass = new PluginDeclaredSubclass();
        subClass.initialisePlugins(); // called by JUnit via @Before
        final ApplicationPlugin applicationPlugin = subClass.getApplicationPlugin();
        Assert.assertNotNull(applicationPlugin);
        // further tests - i.e. getSpringLoader() don't work - we
        // need a proper test class loaded with @Before
    }
}
