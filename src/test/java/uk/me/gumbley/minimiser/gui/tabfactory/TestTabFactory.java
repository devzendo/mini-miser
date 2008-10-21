package uk.me.gumbley.minimiser.gui.tabfactory;

import java.awt.Label;
import java.util.ArrayList;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.dialog.problem.StubProblemReporter;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.AbstractDatabaseDescriptorFactoryUnittestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.opentablist.TabEvent;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * Tests the operation of the TabFactory
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/tabfactory/TabFactoryTestCase.xml")
public final class TestTabFactory extends AbstractDatabaseDescriptorFactoryUnittestCase {

    private static final String DATABASE = "database";
    private TabFactory tabFactory;
    private OpenTabList openTabList;
    private StubProblemReporter problemReporter;
    
    // Used by tests that make use of setUpStubRecordingTest
    private DatabaseDescriptor descriptor;
    private StubRecordingTab stubTab;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getTabFactoryPrerequisites() {
        tabFactory = getSpringLoader().getBean("tabFactory", TabFactory.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        problemReporter = getSpringLoader().getBean("problemReporter", StubProblemReporter.class);
    }
    
    /**
     * 
     */
    @Test
    public void descriptorIsNotRetrievableFromDescriptorFactoryBeforeLoadingViewPanels() {
        Assert.assertNull(getDatabaseDescriptor());
    }
   
    /**
     * 
     */
    @Test
    public void problemReporterFiredOnNoSuchBean() {
        Assert.assertNull(problemReporter.getDoing());
        Assert.assertNull(problemReporter.getException());

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getNonDeclaredTabIdentifiersToOpen();
        tabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertNotNull(problemReporter.getDoing());
        Assert.assertTrue(problemReporter.getException() instanceof org.springframework.beans.factory.NoSuchBeanDefinitionException);
    }
    
    /**
     * 
     */
    @Test
    public void loadNewTabIntoTabOpenList() {
        final List<TabDescriptor> initialTabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(initialTabsForDatabase);
        Assert.assertEquals(0, initialTabsForDatabase.size());
        
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getTabIdentifiersToOpen();
        final List<TabDescriptor> tabsForDatabase = tabFactory.loadTabs(databaseDescriptor, tabIdentifiersToOpen);

        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, tabsForDatabase.get(0).getTabIdentifier());
        // TODO further tests for correct tab instantiation?
    }

    private List<TabIdentifier> getTabIdentifiersToOpen() {
        final List<TabIdentifier> toOpenTabs = new ArrayList<TabIdentifier>();
        toOpenTabs.add(TabIdentifier.OVERVIEW);
        return toOpenTabs;
    }

    private List<TabIdentifier> getNonDeclaredTabIdentifiersToOpen() {
        final List<TabIdentifier> toOpenTabs = new ArrayList<TabIdentifier>();
        toOpenTabs.add(TabIdentifier.CATEGORIES);
        return toOpenTabs;
    }

    private List<TabDescriptor> setUpStubRecordingTest() {
        StubRecordingTab.clearConstructCount();
        
        descriptor = new DatabaseDescriptor(DATABASE);
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
    public void descriptorIsRetrievableFromDescriptorFactoryByTab() {
        setUpStubRecordingTest();
        
        Assert.assertSame(descriptor, stubTab.getDatabaseDescriptor());
    }
    
    /**
     * 
     */
    @Test
    public void descriptorIsNotRetrievableFromDescriptorFactoryAfterLoadingTab() {
        setUpStubRecordingTest();
        
        Assert.assertNull(getDatabaseDescriptor());
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
        final TabDescriptor overviewTabDescriptor = new TabDescriptor(TabIdentifier.OVERVIEW);
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
