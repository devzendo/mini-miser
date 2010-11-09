/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.menu;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.MainFrameTitle;
import org.devzendo.minimiser.gui.StubMainFrameTitle;
import org.devzendo.minimiser.gui.tabfactory.StubTabFactory;
import org.devzendo.minimiser.gui.tabfactory.TabFactory;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.recentlist.RecentFilesList;
import org.junit.Before;


/**
 * Base class for menu mediator tests.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/menu/MenuMediatorTestCase.xml")
public abstract class MenuMediatorUnittestCase extends SpringLoaderUnittestCase {

    private StubMenu stubMenu;
    private OpenDatabaseList openDatabaseList;
    private OpenTabList openTabList;
    private RecentFilesList recentFilesList;
    private StubOpener stubOpener;
    private StubOpenerAdapterFactory stubOpenerAdapterFactory;
    private MainFrameTitle mainFrameTitle;
    private MiniMiserPrefs prefs;
    private File prefsFile;
    private TabFactory tabFactory;
    private PluginManager pluginManager;
    private PluginRegistry pluginRegistry;
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
        pluginManager = getSpringLoader().getBean("pluginManager", PluginManager.class);
        pluginRegistry = getSpringLoader().getBean("pluginRegistry", PluginRegistry.class);

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
    protected final MiniMiserPrefs getPrefs() {
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

    /**
     * @return the pluginManager
     */
    protected final PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * @return the pluginRegistry
     */
    protected final PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }
}
