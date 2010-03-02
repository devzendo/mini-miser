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

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Is the POM's version and artifact name available at runtime?
 * @author matt
 *
 */
public final class TestAppDetailsPropertiesLoader extends LoggingTestCase {
    private AppDetailsPropertiesLoader mAppDetailsPropertiesLoader;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mAppDetailsPropertiesLoader = new AppDetailsPropertiesLoader();
    }
    
    /**
     * 
     */
    @Test
    public void appDetailsPropertiesContainAVersion() {
        final String version = mAppDetailsPropertiesLoader.getVersion();
        validateFilteredProperty(version);
    }

    /**
     * 
     */
    @Test
    public void appDetailsPropertiesContainAName() {
        final String name = mAppDetailsPropertiesLoader.getName();
        validateFilteredProperty(name);
    }

    private void validateFilteredProperty(final String property) {
        Assert.assertNotNull(property);
        Assert.assertTrue(property.length() > 0);
        Assert.assertFalse(property.contains("$")); // has it been filtered?
        // Eclipse will fail the test here since I'm not filtering
        // resources via m2eclipse - it slows the build down, and
        // in the version of the plugin I'm using, Just Doesn't
        // Work.
        
        // It /does/ work, with the latest version of the plugin.
    }
}
