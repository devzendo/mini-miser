package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Label;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.prefs.ChangeCollectingPrefsFactory;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;


/**
 * Tests the operation of the ToolsOptionsTabFactory
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/dialog/ToolsOptionsTabFactoryTestCase.xml")
public final class TestToolsOptionsTabFactory extends SpringLoaderUnittestCase {

    private ToolsOptionsTabFactory tabFactory;
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
