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

import java.io.IOException;

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the TabParameter FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/tabfactory/TabParameterFactoryTestCase.xml")
public final class TestTabParameterFactory extends SpringLoaderUnittestCase {
    private TabParameterFactoryUnittestHelper mTabParameterFactoryHelper;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mTabParameterFactoryHelper = new TabParameterFactoryUnittestHelper(getSpringLoader());
    }

    /**
     * @throws IOException on failure
     *
     */
    @Test
    public void testStoreAndClearParameter() throws IOException {
        final TabParameter tabParameter = new TabParameter() {
        };
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tabParameter);

        final TabParameter tp2 = mTabParameterFactoryHelper.getTabParameter();
        Assert.assertNotNull(tp2);

        Assert.assertSame(tabParameter, tp2);

        mTabParameterFactoryHelper.getTabParameterFactory().clearTabParameter();
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsNotASingleton() throws IOException {
        final TabParameter tp1 = new TabParameter() {
        };

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tp1);

        final TabParameter tp2 = new TabParameter() {
        };

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tp2);

        final TabParameter tpcurrent = mTabParameterFactoryHelper.getTabParameter();
        Assert.assertNotNull(tpcurrent);

        Assert.assertNotSame(tpcurrent, tp1);
        Assert.assertSame(tpcurrent, tp2);
    }
}
