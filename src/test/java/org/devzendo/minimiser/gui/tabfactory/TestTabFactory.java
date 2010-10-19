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

import java.awt.Label;
import java.util.Arrays;
import java.util.List;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.gui.tab.IntegerTabParameter;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseDescriptorFactoryUnittestHelper;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.opentablist.TabEvent;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the operation of the TabFactory
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/tabfactory/TabFactoryTestCase.xml")
public final class TestTabFactory extends SpringLoaderUnittestCase {

    private static final String DATABASE = "database";
    private TabFactory mTabFactory;
    private OpenTabList mOpenTabList;
    private StubProblemReporter mProblemReporter;
    private DatabaseDescriptorFactoryUnittestHelper mDatabaseDescriptorFactoryHelper;
    private TabParameterFactoryUnittestHelper mTabParameterFactoryHelper;

    // Used by tests that make use of setUpStubRecordingTest
    private StubRecordingTab mStubTab;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getTabFactoryPrerequisites() {
        mDatabaseDescriptorFactoryHelper = new DatabaseDescriptorFactoryUnittestHelper(getSpringLoader());
        mTabParameterFactoryHelper = new TabParameterFactoryUnittestHelper(getSpringLoader());
        mTabFactory = getSpringLoader().getBean("tabFactory", TabFactory.class);
        mOpenTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        mProblemReporter = getSpringLoader().getBean("problemReporter", StubProblemReporter.class);
    }

    /**
     *
     */
    @Test
    public void descriptorIsNotRetrievableFromDescriptorFactoryBeforeLoadingViewPanels() {
        Assert.assertNull(mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor());
    }

    /**
     *
     */
    @Test
    public void parameterIsNotRetrievableFromParameterFactoryBeforeLoadingViewPanels() {
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());
    }

    /**
     *
     */
    @Test
    public void problemReporterFiredOnNoSuchBean() {
        Assert.assertNull(mProblemReporter.getDoing());
        Assert.assertNull(mProblemReporter.getException());

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getUndefinedTabIdentifiersAsList();
        mTabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertNotNull(mProblemReporter.getDoing());
        Assert.assertTrue(mProblemReporter.getException() instanceof org.springframework.beans.factory.NoSuchBeanDefinitionException);
    }

    /**
     *
     */
    @Test
    public void loadTabBeanGivenItsNameInTabIdentifier() {
        final List<TabIdentifier> tabIdentifiers =
            Arrays.asList(new TabIdentifier("id", "irrelevant display name", false, 'i', "myNamedTabBean", null)
            );
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabDescriptor> tabsForDatabase = mTabFactory.loadTabs(databaseDescriptor, tabIdentifiers);

        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals("myNamedTabBean", tabsForDatabase.get(0).getTabIdentifier().getTabBeanName());
        Assert.assertTrue(tabsForDatabase.get(0).getTab() instanceof StubRecordingTab);
    }

    /**
     *
     */
    @Test
    public void loadNewTabReturnsCorrectTabDescriptors() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getDefinedTabIdentifierAsList();
        final List<TabDescriptor> tabsForDatabase = mTabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, tabsForDatabase.get(0).getTabIdentifier());
    }

    private List<TabIdentifier> getParameterisedTabIdentifierAsList() {
        final TabIdentifier tabIdentifier =
            new TabIdentifier("test", "Irrelevant display name",
                false, 'I', "myNamedTabBean",
                new IntegerTabParameter(42));
        return Arrays.asList(tabIdentifier);
    }

    private List<TabIdentifier> getDefinedTabIdentifierAsList() {
        return Arrays.asList(SystemTabIdentifiers.OVERVIEW);
    }

    private List<TabIdentifier> getUndefinedTabIdentifiersAsList() {
        return Arrays.asList(SystemTabIdentifiers.CATEGORIES); // not in app context
    }

    private List<TabDescriptor> setUpStubRecordingTest(final DatabaseDescriptor descriptor, final List<TabIdentifier> tabIdentifiersToOpen) {
        StubRecordingTab.clearConstructCount();

        final List<TabDescriptor> tabsForDatabase = mTabFactory.loadTabs(descriptor, tabIdentifiersToOpen);

        Assert.assertEquals(1, StubRecordingTab.getConstructCount());

        final TabDescriptor tabDescriptor = tabsForDatabase.get(0);
        final Tab tab = tabDescriptor.getTab();

        Assert.assertTrue(tab instanceof StubRecordingTab);
        mStubTab = (StubRecordingTab) tab;

        return tabsForDatabase;
    }

    /**
     *
     */
    @Test
    public void descriptorAndParameterAreRetrievableFromFactoriesByTab() {
        setUpStubRecordingTest(new DatabaseDescriptor(DATABASE), getParameterisedTabIdentifierAsList());

        Assert.assertEquals(DATABASE, mStubTab.getDatabaseDescriptor().getDatabaseName());
        final IntegerTabParameter retrievedParameter = (IntegerTabParameter) mStubTab.getTabParameter();
        Assert.assertEquals(new Integer(42), retrievedParameter.getValue());
    }

    /**
     *
     */
    @Test
    public void individualTabIdentifierParametersAreRetrievableFromFactoriesByCorrectTab() {
        final TabIdentifier tabIdentifierOne = new TabIdentifier("one", "one", false, '1', "myNamedTabBean",
            new IntegerTabParameter(101));
        final TabIdentifier tabIdentifierTwo = new TabIdentifier("two", "two", false, '2', "myNamedTabBean",
            new IntegerTabParameter(102));

        final List<TabDescriptor> tabsForDatabase =
            mTabFactory.loadTabs(
                new DatabaseDescriptor(DATABASE),
                Arrays.asList(tabIdentifierOne, tabIdentifierTwo));

        final TabDescriptor tabDescriptorOne = tabsForDatabase.get(0);
        final TabDescriptor tabDescriptorTwo = tabsForDatabase.get(1);
        Assert.assertSame(tabIdentifierOne, tabDescriptorOne.getTabIdentifier());
        Assert.assertEquals(new Integer(101),
            ((IntegerTabParameter) ((StubRecordingTab) tabDescriptorOne.getTab()).
                    getTabParameter()).getValue());
        Assert.assertSame(tabIdentifierTwo, tabDescriptorTwo.getTabIdentifier());
        Assert.assertEquals(new Integer(102),
            ((IntegerTabParameter) ((StubRecordingTab) tabDescriptorTwo.getTab()).
                    getTabParameter()).getValue());
    }

    /**
     *
     */
    @Test
    public void descriptorAndParameterAreNotRetrievableFromFactoriesAfterLoadingTab() {
        // stash rubbish into the factories to ensure that they get cleared down
        mDatabaseDescriptorFactoryHelper.getDatabaseDescriptorFactory().setDatabaseDescriptor(new DatabaseDescriptor("IRRELEVANT"));
        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(new TabParameter() {
        });

        setUpStubRecordingTest(new DatabaseDescriptor(DATABASE), getParameterisedTabIdentifierAsList());

        Assert.assertNull(mDatabaseDescriptorFactoryHelper.getDatabaseDescriptor());
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());
    }

    /**
     * tab constructor is not called on a Swing Event Thread, perhaps a
     * little artificial, since I'm not testing whether this is the case
     * in Real Code. However, there's an assertion in there, and I leave
     * these on.
     */
    @Test
    public void tabIsConstructedOnNonEDT() {
        setUpStubRecordingTest(new DatabaseDescriptor(DATABASE), getDefinedTabIdentifierAsList());

        Assert.assertFalse(mStubTab.isConstructedOnEventThread());
    }

    /**
     *
     */
    @Test
    public void tabComponentIsInitialisedOnEDT() {
        setUpStubRecordingTest(new DatabaseDescriptor(DATABASE), getDefinedTabIdentifierAsList());

        Assert.assertTrue(mStubTab.isInitComponentCalled());
        Assert.assertTrue(mStubTab.isInitComponentsCalledOnEventThread());

        Assert.assertTrue(mStubTab.getComponent() instanceof Label);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void doesntLoadTabIfItHasAlreadyBeenLoaded() {
        final TabDescriptor overviewTabDescriptor = new TabDescriptor(SystemTabIdentifiers.OVERVIEW);
        final DatabaseDescriptor descriptor = new DatabaseDescriptor(DATABASE);
        mOpenTabList.addTab(descriptor, overviewTabDescriptor);

        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        mOpenTabList.addTabEventObserver(obs);

        StubRecordingTab.clearConstructCount();

        final List<TabIdentifier> tabIdentifiersToOpen = getDefinedTabIdentifierAsList();
        mTabFactory.loadTabs(descriptor, tabIdentifiersToOpen);

        Assert.assertEquals(0, StubRecordingTab.getConstructCount());

        EasyMock.verify(obs);
    }

    /**
     *
     */
    @Test
    public void tabComponentIsDestroyedOnCorrectThreads() {
        final DatabaseDescriptor descriptor = new DatabaseDescriptor(DATABASE);
        final List<TabDescriptor> tabsForDatabase = setUpStubRecordingTest(descriptor, getDefinedTabIdentifierAsList());

        Assert.assertFalse(mStubTab.isDestroyCalled());
        Assert.assertFalse(mStubTab.isDestroyedOnNonEventThread());
        Assert.assertFalse(mStubTab.isDisposeComponentCalled());
        Assert.assertFalse(mStubTab.isDisposedOnEventThread());

        mTabFactory.closeTabs(descriptor, tabsForDatabase);

        Assert.assertTrue(mStubTab.isDestroyCalled());
        Assert.assertTrue(mStubTab.isDestroyedOnNonEventThread());
        Assert.assertTrue(mStubTab.isDisposeComponentCalled());
        Assert.assertTrue(mStubTab.isDisposedOnEventThread());
    }
}
