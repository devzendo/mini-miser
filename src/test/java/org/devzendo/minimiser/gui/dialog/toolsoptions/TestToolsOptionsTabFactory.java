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

package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.awt.Label;
import java.util.List;

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.prefs.ChangeCollectingPrefsFactory;
import org.devzendo.minimiser.prefs.Prefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the operation of the ToolsOptionsTabFactory
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/dialog/toolsoptions/ToolsOptionsTabFactoryTestCase.xml")
public final class TestToolsOptionsTabFactory extends SpringLoaderUnittestCase {

    private ToolsOptionsTabFactory tabFactory;
    @SuppressWarnings("unused")
    private StubProblemReporter problemReporter;

    // Used by tests that make use of setUpStubRecordingTest
    private StubRecordingToolsOptionsTab stubTab;
    private Prefs stubPrefs;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getTabFactoryPrerequisites() {
        tabFactory = getSpringLoader().getBean("toolsOptionsTabFactory", ToolsOptionsTabFactory.class);
        problemReporter = getSpringLoader().getBean("problemReporter", StubProblemReporter.class);
        stubPrefs = new StubTabHidingPrefs();
    }


    /**
     * Get the ChangeCollectingPrefsFactory
     * @return the change-collecting prefs factory
     */
    @SuppressWarnings("unused")
    private ChangeCollectingPrefsFactory getChangeCollectingPrefsFactory() {
        return getSpringLoader().getBean("&changeCollectingPrefs", ChangeCollectingPrefsFactory.class);
    }

    /**
     * Get the change-collecting prefs
     * @return the change-collecting prefs
     */
    private Prefs getChangeCollectingPrefs() {
        return getSpringLoader().getBean("changeCollectingPrefs", Prefs.class);
    }

    private List<ToolsOptionsTab> setUpStubRecordingTest() {
        StubRecordingToolsOptionsTab.clearConstructCount();

        final List<ToolsOptionsTab> tabList = tabFactory.loadTabs(stubPrefs);

        Assert.assertEquals(1, StubRecordingToolsOptionsTab.getConstructCount());

        Assert.assertNotNull(tabList);
        Assert.assertEquals(1, tabList.size());
        Assert.assertTrue(tabList.get(0) instanceof StubRecordingToolsOptionsTab);
        stubTab = (StubRecordingToolsOptionsTab) tabList.get(0);

        return tabList;
    }

    /**
     *
     */
    @Test
    public void prefsIsNotRetrievableFromPrefsFactoryBeforeLoadingPanels() {
        Assert.assertNull(getChangeCollectingPrefs());
    }

    /**
     *
     */
    @Test
    public void prefsIsRetrievableFromPrefsFactoryByTab() {
        setUpStubRecordingTest();

        Assert.assertSame(stubPrefs, stubTab.getPrefs());
    }

    /**
     *
     */
    @Test
    public void prefsIsNotRetrievableFromPrefsFactoryAfterLoadingTab() {
        setUpStubRecordingTest();

        Assert.assertNull(getChangeCollectingPrefs());
    }

    /**
     * tab constructor is not called on a Swing Event Thread, perhaps a
     * little artificial, since I'm not testing whether this is the case
     * in Real Code. However, there's an assertion in there, and I leave
     * these on.
     */
    @Test
    public void tabIsConstructedOnNonEDT() {
        setUpStubRecordingTest();

        Assert.assertFalse(stubTab.isConstructedOnEventThread());
    }

    /**
     *
     */
    @Test
    public void tabComponentIsInitialisedOnEDT() {
        setUpStubRecordingTest();

        Assert.assertTrue(stubTab.isInitComponentCalled());
        Assert.assertTrue(stubTab.isInitComponentsCalledOnEventThread());

        Assert.assertTrue(stubTab.getComponent() instanceof Label);
    }

    /**
     *
     */
    @Test
    public void tabComponentIsDestroyedOnCorrectThreads() {
        final List<ToolsOptionsTab> tabs = setUpStubRecordingTest();

        Assert.assertFalse(stubTab.isDestroyCalled());
        Assert.assertFalse(stubTab.isDestroyedOnNonEventThread());
        Assert.assertFalse(stubTab.isDisposeComponentCalled());
        Assert.assertFalse(stubTab.isDisposedOnEventThread());

        tabFactory.closeTabs(tabs);

        Assert.assertTrue(stubTab.isDestroyCalled());
        Assert.assertTrue(stubTab.isDestroyedOnNonEventThread());
        Assert.assertTrue(stubTab.isDisposeComponentCalled());
        Assert.assertTrue(stubTab.isDisposedOnEventThread());
    }
}
