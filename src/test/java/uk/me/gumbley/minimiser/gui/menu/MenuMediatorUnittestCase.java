package uk.me.gumbley.minimiser.gui.menu;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Before;
import uk.me.gumbley.minimiser.gui.MainFrameTitle;
import uk.me.gumbley.minimiser.gui.StubMainFrameTitle;
import uk.me.gumbley.minimiser.gui.tabfactory.StubTabFactory;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Base class for menu mediator tests.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/menu/MenuMediatorTestCase.xml")
public abstract class MenuMediatorUnittestCase extends SpringLoaderUnittestCase {

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
    private static List<String> beanNames;

    /**
     * Get all necessaries
     * @throws IOException on prefs file creation failure
     */
    @SuppressWarnings("unchecked")
    @Before
    public final void getMediatorPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(prefs);
        
        stubMenu = getSpringLoader().getBean("menu", StubMenu.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        recentFilesList = getSpringLoader().getBean("recentFilesList", StubRecentFilesList.class);
        stubOpener = getSpringLoader().getBean("opener", StubOpener.class);
        stubOpenerAdapterFactory = getSpringLoader().getBean("openerAdapterFactory", StubOpenerAdapterFactory.class);
        mainFrameTitle = getSpringLoader().getBean("mainFrameTitle", StubMainFrameTitle.class);
        tabFactory = getSpringLoader().getBean("tabFactory", StubTabFactory.class);
        
        beanNames = getSpringLoader().getBean("menuWiringList", List.class);
    }

    /**
     * After your test code sets up anything necessary, start up the mediator. 
     */
    protected void startMediator() {
        getSpringLoader().getBean("menuMediator", MenuMediatorImpl.class);
    }

    /**
     * @return the menu wiring adapter bean names
     */
    protected static final List<String> getBeanNames() {
        return beanNames;
    }

    /**
     * @return the main frame title
     */
    protected final MainFrameTitle getMainFrameTitle() {
        return mainFrameTitle;
    }

    /**
     * @return the open database list
     */
    protected final OpenDatabaseList getOpenDatabaseList() {
        return openDatabaseList;
    }

    /**
     * @return the open tab list
     */
    protected final OpenTabList getOpenTabList() {
        return openTabList;
    }

    /**
     * @return the temporary prefs
     */
    protected final Prefs getPrefs() {
        return prefs;
    }

    /**
     * @return the file that holds the temporary prefs
     */
    protected final File getPrefsFile() {
        return prefsFile;
    }

    /**
     * @return the recent files list
     */
    protected final RecentFilesList getRecentFilesList() {
        return recentFilesList;
    }

    /**
     * @return the menu
     */
    protected final StubMenu getStubMenu() {
        return stubMenu;
    }

    /**
     * @return the opener
     */
    protected final StubOpener getStubOpener() {
        return stubOpener;
    }

    /**
     * @return the opener adapter factory
     */
    protected final StubOpenerAdapterFactory getStubOpenerAdapterFactory() {
        return stubOpenerAdapterFactory;
    }

    /**
     * @return the tab factory
     */
    protected final TabFactory getTabFactory() {
        return tabFactory;
    }

}
