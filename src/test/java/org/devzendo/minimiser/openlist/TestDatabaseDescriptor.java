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

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests for equality, inequality, and attribute operations.
 * 
 * @author matt
 *
 */
public final class TestDatabaseDescriptor extends LoggingTestCase {
    
    /**
     * 
     */
    @Test
    public void testEquality() {
        final DatabaseDescriptor justOne = new DatabaseDescriptor("one");
        final DatabaseDescriptor justTwo = new DatabaseDescriptor("two");
        Assert.assertFalse(justOne.equals(justTwo));
        final DatabaseDescriptor oneWithPath = new DatabaseDescriptor("one", "/tmp/one");
        Assert.assertTrue(justOne.equals(oneWithPath));
        Assert.assertTrue(oneWithPath.equals(justOne));
        Assert.assertFalse(oneWithPath.equals(justTwo));
        final DatabaseDescriptor twoWithOnesPath = new DatabaseDescriptor("two", "/tmp/one");
        Assert.assertTrue(justTwo.equals(twoWithOnesPath));
        Assert.assertFalse(twoWithOnesPath.equals(oneWithPath));
        final DatabaseDescriptor oneWithOtherPath = new DatabaseDescriptor("one", "/tmp/other");
        Assert.assertTrue(justOne.equals(oneWithOtherPath));
    }
    
    /**
     * 
     */
    @Test
    public void attributesAreNullInitially() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        Assert.assertNull(dd.getTabbedPane());
        Assert.assertNull(dd.getAttribute(AttributeIdentifier.ApplicationMenu));
    }
    
    /**
     * 
     */
    @Test
    public void pathIsEmptyInitially() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        Assert.assertEquals("", dd.getDatabasePath());
    }
    
    /**
     * 
     */
    @Test
    public void attributesCanBeSet() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        dd.setAttribute(AttributeIdentifier.ApplicationMenu, "foo");
        Assert.assertEquals("foo", dd.getAttribute(AttributeIdentifier.ApplicationMenu));
    }

    /**
     * 
     */
    @Test
    public void attributesCanBeCleared() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        dd.setAttribute(AttributeIdentifier.ApplicationMenu, "foo");
        dd.clearAttribute(AttributeIdentifier.ApplicationMenu);
        Assert.assertNull(dd.getAttribute(AttributeIdentifier.ApplicationMenu));
    }
    
    private class TestDAOFactory implements DAOFactory {
        // do nothing
    }
    
    /**
     * 
     */
    @Test
    public void daoFactoriesAreEmptyInitially() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one", "/tmp/foo");
        Assert.assertNull(dd.getDAOFactory(TestDAOFactory.class));
    }
    
    /**
     * 
     */
    @Test
    public void daoFactoriesCanBeSetAndRetrieved() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one", "/tmp/foo");
        final TestDAOFactory testDAOFactory = new TestDAOFactory();
        dd.setDAOFactory(TestDAOFactory.class, testDAOFactory);
        final TestDAOFactory factory = dd.getDAOFactory(TestDAOFactory.class);
        Assert.assertSame(testDAOFactory, factory);
    }
}
