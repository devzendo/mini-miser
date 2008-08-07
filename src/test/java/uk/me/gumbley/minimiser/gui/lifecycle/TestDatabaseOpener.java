package uk.me.gumbley.minimiser.gui.lifecycle;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;


/**
 * Tests for the automatic opening of databases that were open on last shutdown,
 * at startup.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/lifecycle/LifecycleTestCase.xml")
public class TestDatabaseOpener extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseCloser.class);
    private AccessFactory accessFactory;
    private OpenDatabaseList openDatabaseList;
    private LifecycleManager lifecycleManager;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        lifecycleManager = getSpringLoader().getBean("openLifecycleManager", LifecycleManager.class);
    }

    @Test
    public void shouldOpenRecordedDatabaseOnStartup() {
        
    }
    

}
