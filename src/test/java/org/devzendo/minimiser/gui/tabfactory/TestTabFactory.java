package org.devzendo.minimiser.gui.tabfactory;

import java.awt.Label;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseDescriptorFactoryUnittestHelper;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.opentablist.TabEvent;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
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
    private TabFactory tabFactory;
    private OpenTabList openTabList;
    private StubProblemReporter problemReporter;
    private DatabaseDescriptorFactoryUnittestHelper mDatabaseDescriptorFactoryHelper;
    private TabParameterFactoryUnittestHelper mTabParameterFactoryHelper;

    // Used by tests that make use of setUpStubRecordingTest
    private DatabaseDescriptor descriptor;
    private TabParameter parameter;
    private StubRecordingTab stubTab;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getTabFactoryPrerequisites() {
        mDatabaseDescriptorFactoryHelper = new DatabaseDescriptorFactoryUnittestHelper(getSpringLoader());
        mTabParameterFactoryHelper = new TabParameterFactoryUnittestHelper(getSpringLoader());
        tabFactory = getSpringLoader().getBean("tabFactory", TabFactory.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        problemReporter = getSpringLoader().getBean("problemReporter", StubProblemReporter.class);
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
        Assert.assertNull(problemReporter.getDoing());
        Assert.assertNull(problemReporter.getException());

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getUndefinedTabIdentifiersToOpen();
        tabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertNotNull(problemReporter.getDoing());
        Assert.assertTrue(problemReporter.getException() instanceof org.springframework.beans.factory.NoSuchBeanDefinitionException);
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
        final List<TabDescriptor> tabsForDatabase = tabFactory.loadTabs(databaseDescriptor, tabIdentifiers);

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
        final List<TabIdentifier> tabIdentifiersToOpen = getTabIdentifiersToOpen();
        final List<TabDescriptor> tabsForDatabase = tabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, tabsForDatabase.get(0).getTabIdentifier());
    }

    private List<TabIdentifier> getTabIdentifiersToOpen() {
        final List<TabIdentifier> toOpenTabs = new ArrayList<TabIdentifier>();
        toOpenTabs.add(SystemTabIdentifiers.OVERVIEW);
        return toOpenTabs;
    }

    private List<TabIdentifier> getUndefinedTabIdentifiersToOpen() {
        final List<TabIdentifier> toOpenTabs = new ArrayList<TabIdentifier>();
        toOpenTabs.add(SystemTabIdentifiers.CATEGORIES); // not in app context
        return toOpenTabs;
    }

    private List<TabDescriptor> setUpStubRecordingTest() {
        StubRecordingTab.clearConstructCount();

        descriptor = new DatabaseDescriptor(DATABASE);
        parameter = new TabParameter() {
            /**
             * Just something for the weekend
             */
            @SuppressWarnings("unused")
            public void irrelevant() {
            }
        };
        final List<TabIdentifier> tabIdentifiersToOpen = getTabIdentifiersToOpen();
        final List<TabDescriptor> tabsForDatabase = tabFactory.loadTabs(descriptor, tabIdentifiersToOpen);

        Assert.assertEquals(1, StubRecordingTab.getConstructCount());

        final TabDescriptor tabDescriptor = tabsForDatabase.get(0);
        final Tab tab = tabDescriptor.getTab();

        Assert.assertTrue(tab instanceof StubRecordingTab);
        stubTab = (StubRecordingTab) tab;

        return tabsForDatabase;
    }

    /**
     *
     */
    @Test
    public void descriptorAndParameterAreRetrievableFromDescriptorFactoryByTab() {
        // set up some fakes to test that the values returned from the stub are the
        // real ones, rather than null
        descriptor = new DatabaseDescriptor("irrelevant");
        parameter = new TabParameter() {
        };

        setUpStubRecordingTest();

        Assert.assertSame(descriptor, stubTab.getDatabaseDescriptor());
        Assert.assertSame(parameter, stubTab.getTabParameter());
    }

    /**
     *
     */
    @Test
    public void descriptorAndParameterAreNotRetrievableFromDescriptorFactoryAfterLoadingTab() {
        // set up some fakes to test that the values returned from the stub are
        // null, rather than real ones
        descriptor = new DatabaseDescriptor("irrelevant");
        parameter = new TabParameter() {
        };

        setUpStubRecordingTest();

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
    @SuppressWarnings("unchecked")
    @Test
    public void doesntLoadTabIfItHasAlreadyBeenLoaded() {
        final TabDescriptor overviewTabDescriptor = new TabDescriptor(SystemTabIdentifiers.OVERVIEW);
        descriptor = new DatabaseDescriptor(DATABASE);
        openTabList.addTab(descriptor, overviewTabDescriptor);

        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        openTabList.addTabEventObserver(obs);

        StubRecordingTab.clearConstructCount();

        final List<TabIdentifier> tabIdentifiersToOpen = getTabIdentifiersToOpen();
        tabFactory.loadTabs(descriptor, tabIdentifiersToOpen);

        Assert.assertEquals(0, StubRecordingTab.getConstructCount());

        EasyMock.verify(obs);
    }

    /**
     *
     */
    @Test
    public void tabComponentIsDestroyedOnCorrectThreads() {
        final List<TabDescriptor> tabsForDatabase = setUpStubRecordingTest();

        Assert.assertFalse(stubTab.isDestroyCalled());
        Assert.assertFalse(stubTab.isDestroyedOnNonEventThread());
        Assert.assertFalse(stubTab.isDisposeComponentCalled());
        Assert.assertFalse(stubTab.isDisposedOnEventThread());

        tabFactory.closeTabs(descriptor, tabsForDatabase);

        Assert.assertTrue(stubTab.isDestroyCalled());
        Assert.assertTrue(stubTab.isDestroyedOnNonEventThread());
        Assert.assertTrue(stubTab.isDisposeComponentCalled());
        Assert.assertTrue(stubTab.isDisposedOnEventThread());
    }
}
