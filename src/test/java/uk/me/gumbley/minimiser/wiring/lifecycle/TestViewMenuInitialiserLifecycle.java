package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.MainFrameTitle;
import uk.me.gumbley.minimiser.gui.StubMainFrameTitle;
import uk.me.gumbley.minimiser.gui.menu.MenuMediatorImpl;
import uk.me.gumbley.minimiser.gui.menu.StubMenu;
import uk.me.gumbley.minimiser.gui.menu.StubOpener;
import uk.me.gumbley.minimiser.gui.menu.StubOpenerAdapterFactory;
import uk.me.gumbley.minimiser.gui.menu.StubRecentFilesList;
import uk.me.gumbley.minimiser.gui.tabfactory.StubTabFactory;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;


/**
 * Tests the lifecycle that initialises the View Menu
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/menu/MenuMediatorTestCase.xml")
public final class TestViewMenuInitialiserLifecycle extends SpringLoaderUnittestCase {

    private StubMenu stubMenu;
    private OpenDatabaseList openDatabaseList;
    private OpenTabList openTabList;
    private RecentFilesList recentFilesList;
    private StubOpener stubOpener;
    private StubOpenerAdapterFactory stubOpenerAdapterFactory;
    private MainFrameTitle mainFrameTitle;
    private Prefs prefs;
    private File prefsFile;
    private TabFactory tabFactory;
    private List<String> beanNames;

    /**
     * @throws IOException on failure
     */
    @SuppressWarnings("unchecked")
    @Before
    public void getPrerequisites() throws IOException {
        stubMenu = getSpringLoader().getBean("menu", StubMenu.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        openTabList = new OpenTabList();
        recentFilesList = getSpringLoader().getBean("recentFilesList", StubRecentFilesList.class);
        stubOpener = new StubOpener();
        stubOpenerAdapterFactory = new StubOpenerAdapterFactory();
        mainFrameTitle = getSpringLoader().getBean("mainFrameTitle", StubMainFrameTitle.class);
        
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        tabFactory = new StubTabFactory(openTabList);
        
        beanNames = getSpringLoader().getBean("menuWiringList", List.class);

    }

    private void startMediator() {
        new MenuMediatorImpl(getSpringLoader(), stubMenu, openDatabaseList, openTabList, recentFilesList, stubOpener, 
            stubOpenerAdapterFactory, mainFrameTitle, prefs, tabFactory, beanNames);
    }
    
    /**
     * 
     */
    @Test
    public void initialiseViewMenuCorrectly() {
        prefs.setTabHidden("SQL");
        prefs.clearTabHidden("Categories");
        
        Assert.assertFalse(stubMenu.isTabHidden("SQL"));
        Assert.assertFalse(stubMenu.isTabHidden("Categories"));
        Assert.assertFalse(stubMenu.isViewMenuBuilt());

        startMediator();

        // Still no change
        Assert.assertFalse(stubMenu.isTabHidden("SQL"));
        Assert.assertFalse(stubMenu.isTabHidden("Categories"));
        Assert.assertFalse(stubMenu.isViewMenuBuilt());

        // Start lifecycle (manually)
        final ViewMenuInitialiserLifecycle lifecycle = new ViewMenuInitialiserLifecycle(prefs, stubMenu);
        lifecycle.startup();

        // now it should have changed
        Assert.assertTrue(stubMenu.isTabHidden("SQL"));
        Assert.assertFalse(stubMenu.isTabHidden("Categories"));
        Assert.assertTrue(stubMenu.isViewMenuBuilt());
    }
}
