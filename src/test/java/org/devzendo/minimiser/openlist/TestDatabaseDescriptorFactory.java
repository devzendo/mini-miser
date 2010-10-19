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

package org.devzendo.minimiser.openlist;

import java.io.IOException;

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the DatabaseDescriptor FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/openlist/DatabaseDescriptorFactoryTestCase.xml")
public final class TestDatabaseDescriptorFactory extends SpringLoaderUnittestCase {
    private DatabaseDescriptorFactoryUnittestHelper mDatabaseDescriptorFactoryHelper;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mDatabaseDescriptorFactoryHelper = new DatabaseDescriptorFactoryUnittestHelper(getSpringLoader());
    }

    /**
     * @throws IOException on failure
     *
     */
    @Test
    public void testStoreAndClearDescriptor() throws IOException {
        final DatabaseDescriptor dd = new DatabaseDescriptor("dd");
        Assert.assertNull(mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor());

        mDatabaseDescriptorFactoryHelper.getDatabaseDescriptorFactory().setDatabaseDescriptor(dd);

        final DatabaseDescriptor dd2 = mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor();
        Assert.assertNotNull(dd2);

        Assert.assertEquals(dd.getDatabaseName(), dd2.getDatabaseName());
        Assert.assertSame(dd, dd2);

        mDatabaseDescriptorFactoryHelper.getDatabaseDescriptorFactory().clearDatabaseDescriptor();
        Assert.assertNull(mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor());
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsNotASingleton() throws IOException {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("dd1");

        mDatabaseDescriptorFactoryHelper.getDatabaseDescriptorFactory().setDatabaseDescriptor(dd1);

        final DatabaseDescriptor dd2 = new DatabaseDescriptor("dd2");

        mDatabaseDescriptorFactoryHelper.getDatabaseDescriptorFactory().setDatabaseDescriptor(dd2);

        final DatabaseDescriptor ddcurrent = mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor();
        Assert.assertNotNull(ddcurrent);

        Assert.assertFalse(ddcurrent.getDatabaseName().equals(dd1.getDatabaseName()));
        Assert.assertNotSame(ddcurrent, dd1);
        Assert.assertSame(ddcurrent, dd2);
    }
}
