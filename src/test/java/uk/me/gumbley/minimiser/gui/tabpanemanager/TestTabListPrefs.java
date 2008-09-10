package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;


/**
 * Test the higher-level tab list prefs storage and logic.
 * 
 * @author matt
 *
 */
public final class TestTabListPrefs extends LoggingTestCase {
    private Prefs prefs;
    private File prefsFile;
    
    private TabListPrefs tabListPrefs;

    /**
     * @throws IOException on prefs creation failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());

        tabListPrefs = new TabListPrefs(prefs);
    }
    
    /**
     * 
     */
    @After
    public void ditchPrefs() {
        prefsFile.deleteOnExit();
    }
    

    /**
     * 
     */
    @Test
    public void allPermanentTabsInOpenListAndInRightOrder() {
        final List<TabIdentifier> openTabs = tabListPrefs.getOpenTabs("none");
        Assert.assertNotNull(openTabs);
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        Assert.assertEquals(permanentTabIdentifiers, openTabs);
    }
}
