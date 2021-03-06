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

package org.devzendo.minimiser.gui.tabfactory;

import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.junit.Assert;

/**
 * Provides access to the configured TabParameter FactoryBean and its
 * stored TabParameter.
 *
 * @author matt
 *
 */
public final class TabParameterFactoryUnittestHelper {

    private final SpringLoader mSpringLoader;
    private final TabParameterFactory mTabParameterFactory;

    /**
     * @param springLoader the SpringLoader
     */
    public TabParameterFactoryUnittestHelper(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mTabParameterFactory = getTabParameterFactory();
        Assert.assertNotNull(mTabParameterFactory);
    }

    /**
     * Get the tab parameter that's stored in the TabParameterFactory
     * @return the currently stored tab parameter.
     */
    public TabParameter getTabParameter() {
        return mSpringLoader.getBean("tabParameter", TabParameter.class);
    }

    /**
     * Get the TabParameterFactory
     * @return the tab parameter factory
     */
    public TabParameterFactory getTabParameterFactory() {
        return mSpringLoader.getBean("&tabParameter", TabParameterFactory.class);
    }
}
