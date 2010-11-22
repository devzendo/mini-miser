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

package org.devzendo.minimiser.wiring.lifecycle;

import java.awt.Label;
import java.util.List;

import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.StubTab;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.util.OrderMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the TabEventListener that's managed as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/TabEventListenerLifecycleTestCase.xml")
public final class TestTabEventListenerLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private OpenTabList openTabList;
    private OrderMonitor orderMonitor;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);

        Assert.assertNotNull(lifecycleManager);
        Assert.assertNotNull(openTabList);
        Assert.assertNotNull(orderMonitor);
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedBeforeStartup() {
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addOrdering.size());
    }

    /**
     *
     */
    @Test
    public void eventsPropagatedAfterStartup() {
        lifecycleManager.startup();

        // not exhaustive - see the TabEventLifecycleManager for more.
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), one);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(1, addOrdering.size());
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedAfterShutdown() {
        lifecycleManager.startup();

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(SystemTabIdentifiers.SQL, stubTab);
        final DatabaseDescriptor dbDescriptor = new DatabaseDescriptor("db");
        openTabList.addTab(dbDescriptor, one);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(1, addOrdering.size());

        lifecycleManager.shutdown();
        orderMonitor.reset();

        final Label label2 = new Label();
        final StubTab stubTab2 = new StubTab(label2);
        final TabDescriptor two = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, stubTab2);
        openTabList.addTab(dbDescriptor, two);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
