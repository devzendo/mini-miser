package uk.me.gumbley.minimiser.gui.menu;

import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProvidingFacade;

/**
 * A MenuProvidingFacade for tests.
 *
 * @author matt
 *
 */
public final class StubMenuProvidingFacade implements MenuProvidingFacade {

    private ApplicationMenu mGlobalApplicationMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private Menu mMenu;

    /**
     * {@inheritDoc}
     */
    public void initialise(
            final ApplicationMenu globalApplicationMenu,
            final OpenDatabaseList openDatabaseList,
            final Menu menu) {
                mGlobalApplicationMenu = globalApplicationMenu;
                mOpenDatabaseList = openDatabaseList;
                mMenu = menu;

    }

    /**
     * @return the globalApplicationMenu
     */
    public ApplicationMenu getGlobalApplicationMenu() {
        return mGlobalApplicationMenu;
    }
}
