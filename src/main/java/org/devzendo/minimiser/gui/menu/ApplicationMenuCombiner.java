package org.devzendo.minimiser.gui.menu;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;

import org.devzendo.minimiser.gui.menu.ApplicationMenu.SystemMenu;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * Combines the global ApplicationMenu and the current database's ApplicationMenu.
 *
 * @author matt
 *
 */
public final class ApplicationMenuCombiner {
    private static final ApplicationMenu EMPTY_APPLICATION_MENU = new ApplicationMenu();
    private final ApplicationMenu mGlobalApplicationMenu;
    private final OpenDatabaseList mOpenDatabaseList;

    /**
     * @param globalApplicationMenu the Global ApplicationMenu used in the combining of
     * all database ApplicationMenus
     * @param openDatabaseList the OpenDatabaseList from where the current database's
     * ApplicationMenu will be retrieved
     */
    public ApplicationMenuCombiner(
            final ApplicationMenu globalApplicationMenu,
            final OpenDatabaseList openDatabaseList) {
        mGlobalApplicationMenu = globalApplicationMenu;
        mOpenDatabaseList = openDatabaseList;
    }

    /**
     * Combine the global ApplicationMenu and the current database's ApplicationMenu
     * (if there is a current database, and if it has provided an ApplicationMenu) into
     * a single ApplicationMenu, with the items from the global menu taking priority
     * over those from the database menu.
     * @return the combined ApplicationMenu
     */
    public ApplicationMenu combineMenus() {
        final ApplicationMenu combinedMenu = new ApplicationMenu();
        combineCustomMenus(combinedMenu);
        for (final ApplicationMenu.SystemMenu systemMenu : ApplicationMenu.SystemMenu.values()) {
            combineSystemMenus(combinedMenu, systemMenu);
        }
        return combinedMenu;
    }

    private void combineSystemMenus(
            final ApplicationMenu combinedMenu,
            final SystemMenu systemMenu) {
        addSystemMenuToCombinedMenu(combinedMenu, systemMenu, mGlobalApplicationMenu.getMenu(systemMenu));
        addSystemMenuToCombinedMenu(combinedMenu, systemMenu, getDatabaseApplicationMenu().getMenu(systemMenu));
    }

    private void addSystemMenuToCombinedMenu(
            final ApplicationMenu combinedMenu,
            final SystemMenu systemMenu,
            final List<JComponent> systemMenuComponents) {
        for (final JComponent systemMenuComponent : systemMenuComponents) {
            combinedMenu.addMenuComponent(systemMenu, systemMenuComponent);
        }
    }

    private void combineCustomMenus(final ApplicationMenu combinedMenu) {
        for (final JMenu globalJMenu : mGlobalApplicationMenu.getCustomMenus()) {
            combinedMenu.addCustomMenu(globalJMenu);
        }
        for (final JMenu databaseJMenu : getDatabaseApplicationMenu().getCustomMenus()) {
            combinedMenu.addCustomMenu(databaseJMenu);
        }
    }

    private ApplicationMenu getDatabaseApplicationMenu() {
        final DatabaseDescriptor descriptor = mOpenDatabaseList.getCurrentDatabase();
        if (descriptor != null) {
            final ApplicationMenu databaseApplicationMenu = (ApplicationMenu) descriptor.getAttribute(AttributeIdentifier.ApplicationMenu);
            if (databaseApplicationMenu != null) {
                return databaseApplicationMenu;
            }
        }
        return EMPTY_APPLICATION_MENU;
    }
}
