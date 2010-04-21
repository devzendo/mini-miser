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

package org.devzendo.minimiser.opentablist;

import java.awt.Label;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.util.OrderMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the ability to listen to the OpenTabList via a list of listener
 * beans.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/opentablist/TabEventListenerTestCase.xml")
public final class TestTabEventListenerManager extends SpringLoaderUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestTabEventListenerManager.class);

    private TabEventListenerManager listenerManager;
    private OpenTabList openTabList;
    private OrderMonitor orderMonitor;

    /**
     *
     */
    @Before
    public void getSimpleTestPrerequisites() {
        listenerManager = getSpringLoader().getBean("listenerManager", TabEventListenerManager.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);

        Assert.assertNotNull(listenerManager);
        Assert.assertNotNull(openTabList);
        Assert.assertNotNull(orderMonitor);
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedUntilWired() {
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);
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
        final List<TabDescriptor> initialTabsForDatabase = openTabList.getTabsForDatabase("one");
        Assert.assertNotNull(initialTabsForDatabase);
        Assert.assertEquals(0, initialTabsForDatabase.size());

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);

        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(SystemTabIdentifiers.SQL, tabsForDatabase.get(0).getTabIdentifier());
        Assert.assertSame(label, tabsForDatabase.get(0).getTab().getComponent());

        final List<String> addOrdering = orderMonitor.getOrdering();
        LOGGER.debug("addOrdering is '" + addOrdering + "'");
        Assert.assertEquals(2, addOrdering.size());
        Assert.assertEquals("one TabOpenedEvent", addOrdering.get(0));
        Assert.assertEquals("two TabOpenedEvent", addOrdering.get(1));

        // TODO: add tests for switch ordering in here
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedAfterUnwiring() {
        listenerManager.wire();

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(2, addOrdering.size());

        listenerManager.unwire();
        orderMonitor.reset();


        final Label label2 = new Label();
        final StubTab stubTab2 = new StubTab(label2);
        final TabDescriptor tab2 = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, stubTab2);
        openTabList.addTab(new DatabaseDescriptor("two"), tab2);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
