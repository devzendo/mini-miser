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

import org.junit.Assert;
import org.junit.Test;


/**
 * Do we have resources available once under the classpath?
 * @author matt
 *
 */
public final class TestMavenEclipseClasspathCorrect {
    /**
     * @throws IOException never
     */
    @Test
    public void shouldOnlySeeResourcesOnceInEnumeration() throws IOException {
    final Enumeration<URL> urls =
        Thread.currentThread().getContextClassLoader().
        getResources("org/devzendo/minimiser/plugin/goodplugin.properties");
        int urlsize = 0;
        while (urls.hasMoreElements()) {
            urls.nextElement();
            urlsize++;
        }
        Assert.assertEquals("src/main/resources is available via " + urlsize
            + " classpath mounts - this will cause plugin loading failure",
            1, urlsize);
    }
}
