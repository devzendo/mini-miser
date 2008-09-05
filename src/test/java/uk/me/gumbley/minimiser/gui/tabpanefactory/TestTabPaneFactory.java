package uk.me.gumbley.minimiser.gui.tabpanefactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.openlist.AbstractDatabaseDescriptorFactoryUnittestCase;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * Tests the operation of the TabpaneFactory
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/tabpanefactory/TabPaneFactoryTestCase.xml")
public class TestTabPaneFactory extends AbstractDatabaseDescriptorFactoryUnittestCase {

    private TabPaneFactory tabPaneFactory;

    /**
     * {@inheritDoc}
     */
    @Before
    public void getPrerequisites() {
        //tabPaneFactory = getSpringLoader().getBean("tabPaneFactory", TabPaneFactory.class);
    }
    
    // WOZERE - need a test to check binding into the TabPane manager?
    
    
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
    public void descriptorIsRetrievableFromDescriptorFactoryByViewPanel() {
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
