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

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.devzendo.commonapp.util.OrderMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the ability to listen to the OpenDatabaseList via a list of listener
 * beans.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/openlist/DatabaseEventListenerTestCase.xml")
public final class TestDatabaseEventListenerManager extends SpringLoaderUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseEventListenerManager.class);

    private DatabaseEventListenerManager listenerManager;
    private OpenDatabaseList openDatabaseList;
    private OrderMonitor orderMonitor;

    /**
     *
     */
    @Before
    public void getSimpleTestPrerequisites() {
        listenerManager = getSpringLoader().getBean("listenerManager", DatabaseEventListenerManager.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);

        Assert.assertNotNull(listenerManager);
        Assert.assertNotNull(openDatabaseList);
        Assert.assertNotNull(orderMonitor);
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedUntilWired() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addOrdering.size());
    }

    /**
     *
     */
    @Test
    public void eventsArePropagated() {
        listenerManager.wire();

        final List<String> initialOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, initialOrdering.size());
        Assert.assertEquals(0, openDatabaseList.getNumberOfDatabases());

        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);
        Assert.assertEquals(1, openDatabaseList.getNumberOfDatabases());

        final List<String> addOrdering = orderMonitor.getOrdering();
        LOGGER.debug("addOrdering is '" + addOrdering + "'");
        Assert.assertEquals(4, addOrdering.size());
        Assert.assertEquals("one DatabaseOpenedEvent", addOrdering.get(0));
        Assert.assertEquals("two DatabaseOpenedEvent", addOrdering.get(1));
        Assert.assertEquals("one DatabaseSwitchedEvent", addOrdering.get(2));
        Assert.assertEquals("two DatabaseSwitchedEvent", addOrdering.get(3));

        orderMonitor.reset();

        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(two);
        Assert.assertEquals(2, openDatabaseList.getNumberOfDatabases());
        Assert.assertEquals("two", openDatabaseList.getCurrentDatabase().getDatabaseName());

        final List<String> addSwitchOrdering = orderMonitor.getOrdering();
        LOGGER.debug("addSwitchOrdering is '" + addSwitchOrdering + "'");
        Assert.assertEquals(4, addSwitchOrdering.size());
        Assert.assertEquals("one DatabaseOpenedEvent", addSwitchOrdering.get(0));
        Assert.assertEquals("two DatabaseOpenedEvent", addSwitchOrdering.get(1));
        Assert.assertEquals("one DatabaseSwitchedEvent", addSwitchOrdering.get(2));
        Assert.assertEquals("two DatabaseSwitchedEvent", addSwitchOrdering.get(3));

        orderMonitor.reset();

        openDatabaseList.switchDatabase("one");
        Assert.assertEquals("one", openDatabaseList.getCurrentDatabase().getDatabaseName());

        final List<String> switchOrdering = orderMonitor.getOrdering();
        LOGGER.debug("switchOrdering is '" + switchOrdering + "'");
        Assert.assertEquals(2, switchOrdering.size());
        Assert.assertEquals("one DatabaseSwitchedEvent", switchOrdering.get(0));
        Assert.assertEquals("two DatabaseSwitchedEvent", switchOrdering.get(1));

        orderMonitor.reset();

        openDatabaseList.removeClosedDatabase(one);
        Assert.assertEquals("two", openDatabaseList.getCurrentDatabase().getDatabaseName());

        final List<String> removeOrdering = orderMonitor.getOrdering();
        LOGGER.debug("removeOrdering is '" + removeOrdering + "'");
        Assert.assertEquals(4, removeOrdering.size());
        Assert.assertEquals("one DatabaseClosedEvent", removeOrdering.get(0));
        Assert.assertEquals("two DatabaseClosedEvent", removeOrdering.get(1));
        Assert.assertEquals("one DatabaseSwitchedEvent", removeOrdering.get(2));
        Assert.assertEquals("two DatabaseSwitchedEvent", removeOrdering.get(3));
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedAfterUnwiring() {
        listenerManager.wire();

        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(4, addOrdering.size());

        listenerManager.unwire();
        orderMonitor.reset();

        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(two);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
