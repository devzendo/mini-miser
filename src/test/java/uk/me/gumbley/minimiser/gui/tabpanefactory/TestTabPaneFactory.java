package uk.me.gumbley.minimiser.gui.tabpanefactory;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.AbstractDatabaseDescriptorFactoryUnittestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * Tests the operation of the TabpaneFactory
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/tabpanefactory/TabPaneFactoryTestCase.xml")
public class TestTabPaneFactory extends AbstractDatabaseDescriptorFactoryUnittestCase {

    private static final String DATABASE = "database";
    private TabPaneFactory tabPaneFactory;
    private OpenTabList openTabList;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getPrerequisites() {
        tabPaneFactory = getSpringLoader().getBean("tabPaneFactory", TabPaneFactory.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
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
    public void loadNewTabIntoTabOpenList() {
        Assert.assertNull(openTabList.getTabsForDatabase(DATABASE));
        
        final DatabaseDescriptor descriptor = new DatabaseDescriptor(DATABASE);
        final List<TabIdentifier> tabIdentifiersToOpen = getTabIdentifiersToOpen();
        tabPaneFactory.loadTabs(descriptor, tabIdentifiersToOpen);
        
        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(DATABASE);
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, tabsForDatabase.get(0).getTabIdentifier());
        // TODO further tests for correct tab instantiation?
        // TODO change TabDescriptor to take a Tab as its second
        // arg, not a Component - the Component has moved into the Component
    }

    private List<TabIdentifier> getTabIdentifiersToOpen() {
        final List<TabIdentifier> toOpenTabs = new ArrayList<TabIdentifier>();
        toOpenTabs.add(TabIdentifier.OVERVIEW);
        return toOpenTabs;
    }
    
    /**
     * 
     */
    @Test
    public void descriptorIsRetrievableFromDescriptorFactoryByViewPanel() {
        Assert.fail("unimplemented");
    }

    /**
     * 
     */
    @Test
    public void doesntLoadTabIfItHasAlreadyBeenLoaded() {
        Assert.fail("unimplemented");
    }

    /**
     * 
     */
    @Test
    public void descriptorIsNotRetrievableFromDescriptorFactoryAfterLoadingViewPanels() {
        Assert.fail("unimplemented");
        
    }
    
    
    /**
     * 
     */
    @Test
    public void viewPanelIsConstructedOnNonEDT() {
        Assert.fail("unimplemented");
        
    }
    
    /**
     * 
     */
    @Test
    public void viewPanelGUIIsConstructedOnEDT() {
        Assert.fail("unimplemented");
        
    }
}
