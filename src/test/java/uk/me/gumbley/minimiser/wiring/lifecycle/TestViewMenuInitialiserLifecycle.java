package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;
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
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;


public final class TestViewMenuInitialiserLifecycle {

    private StubMenu stubMenu;
    private OpenDatabaseList openDatabaseList;
    private RecentFilesList recentFilesList;
    private StubOpener stubOpener;
    private StubOpenerAdapterFactory stubOpenerAdapterFactory;
    private MainFrameTitle mainFrameTitle;
    private Prefs prefs;
    private File prefsFile;

    /**
     * @throws IOException
     */
    @Before
    public void getPrerequisites() throws IOException {
        stubMenu = new StubMenu();
        openDatabaseList = new OpenDatabaseList();
        recentFilesList = EasyMock.createStrictMock(RecentFilesList.class);
        recentFilesList.addRecentListEventObserver(EasyMock.isA(Observer.class));
        stubOpener = new StubOpener();
        stubOpenerAdapterFactory = new StubOpenerAdapterFactory();
        mainFrameTitle = new StubMainFrameTitle();
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
    }

    private void startMediator() {
        new MenuMediatorImpl(stubMenu, openDatabaseList, recentFilesList, stubOpener, 
            stubOpenerAdapterFactory, mainFrameTitle, prefs);
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
