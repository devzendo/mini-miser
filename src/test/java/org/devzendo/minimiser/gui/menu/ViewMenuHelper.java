package org.devzendo.minimiser.gui.menu;

import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.wiring.databaseeventlistener.ApplicationMenuCreatingDatabaseEventListener;

/**
 * A helper for View Menu tests. Puts together the necessary
 * listeners etc., needed for reasonable View Menu behaviour.
 *
 * @author matt
 *
 */
public final class ViewMenuHelper {
    private final OpenDatabaseList mOpenDatabaseList;
    private final OpenTabList mOpenTabList;
    private final MiniMiserPrefs mPrefs;
    private final ApplicationMenu mGlobalApplicationMenu;
    private final ApplicationMenuCombiner mApplicationMenuCombiner;
    private final MenuWiring mMenuWiring;

    /**
     * Create the helper
     * @param openDatabaseList the open database list
     * @param openTabList the open tab list
     * @param prefs the prefs
     */
    public ViewMenuHelper(final OpenDatabaseList openDatabaseList, final OpenTabList openTabList, final MiniMiserPrefs prefs) {
        mOpenDatabaseList = openDatabaseList;
        mOpenTabList = openTabList;
        mPrefs = prefs;
        mOpenDatabaseList.addDatabaseEventObserver(new ApplicationMenuCreatingDatabaseEventListener());
        mGlobalApplicationMenu = new ApplicationMenu();
        mApplicationMenuCombiner = new ApplicationMenuCombiner(
                mGlobalApplicationMenu, mOpenDatabaseList);
        mMenuWiring = new MenuWiring();

    }

    /**
     * @return the created View Menu
     */
    public ViewMenu createViewMenu() {
        return new ViewMenu(getMenuWiring(), mOpenDatabaseList, mOpenTabList,
            mPrefs, mApplicationMenuCombiner);
    }

    /**
     * Initialise the MenuProviding plugins
     * @param pluginManager the PluginManager used to obtain the MenuProviding plugins
     * @param menuFacade the MenuFacade
     * @param problemReporter the ProblemReporter to pass
     */
    public void initialiseMenuProvidingPlugins(
            final PluginManager pluginManager,
            final MenuFacade menuFacade,
            final StubProblemReporter problemReporter) {
        new MenuProvidingFacadeInitialiser(pluginManager, mOpenDatabaseList,
            menuFacade, mGlobalApplicationMenu, problemReporter).initialise();
    }

    /**
     * @return the Global Application Menu
     */
    public ApplicationMenu getGlobalApplicationMenu() {
        return mGlobalApplicationMenu;
    }

    /**
     * @return the ApplicationMenuCombiner
     */
    public ApplicationMenuCombiner getApplicationMenuCombiner() {
        return mApplicationMenuCombiner;
    }

    /**
     * @return the Menu Wiring
     */
    public MenuWiring getMenuWiring() {
        return mMenuWiring;
    }
}
