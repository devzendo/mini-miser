package org.devzendo.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;

/**
 * A bean containing the Application's menu structure as a number of
 * menu items that can be added into the system-provided menus (e.g. File, Edit,
 * View, Tools,Help) and custom menus that can be added between
 * File,Edit,View ... Tools,Window,Help
 *
 * @author matt
 *
 */
public final class ApplicationMenu {
    private final List<JMenu> mCustomMenus;
    private final Map<SystemMenu, List<JComponent>> mSystemMenus;
    private final Map<SystemMenu, Set<String>> mReservedSystemMenus;
    private final Set<String> mReservedMenuNames;
    private final Set<TabIdentifier> mViewMenuTabIdentifiers;

    /**
     * The framework-provided system menus are...
     *
     */
    public enum SystemMenu {
        /**
         * The File menu, perhaps Export/Import/Backup/Restore could be added here?
         */
        File,
        /**
         * Edit menu, ApplicationMenu has support for Undo/Redo/Cut/Copy/Paste
         */
        Edit,
        /**
         * View menu for switching between system and application tabs
         */
        View,
        /**
         * Tools, and possibly on the Mac the Options item will disappear to the Apple menu
         */
        Tools,
        /**
         * The Window menu switches between different databases and isn't customisable
         */
        Window,
        /**
         * The Help menu can have application menuitems added
         */
        Help;
    }

    /**
     * Construct an empty Application Menu
     */
    public ApplicationMenu() {
        mCustomMenus = new ArrayList<JMenu>();
        mSystemMenus = new HashMap<SystemMenu, List<JComponent>>();
        mReservedMenuNames = new HashSet<String>();
        for (final SystemMenu systemMenu : SystemMenu.values()) {
            mSystemMenus.put(systemMenu, new ArrayList<JComponent>());
            mReservedMenuNames.add(systemMenu.toString());
        }
        mReservedSystemMenus = new HashMap<SystemMenu, Set<String>>();
        // TODO remove duplication of menu text
        mReservedSystemMenus.put(SystemMenu.File, new HashSet<String>(
                Arrays.asList(new String[] {
                "New...", "Open...", "Close", "Close all", "Exit"
        })));
        mReservedSystemMenus.put(SystemMenu.Edit, new HashSet<String>(
                Arrays.asList(new String[] {
                "Undo", "Redo", "Cut", "Copy", "Paste"
        })));
        mReservedSystemMenus.put(SystemMenu.Tools, new HashSet<String>(
                Arrays.asList(new String[] {
                "Options"
        })));
        mViewMenuTabIdentifiers = new TreeSet<TabIdentifier>(new Comparator<TabIdentifier>() {
            public int compare(final TabIdentifier o1, final TabIdentifier o2) {
                return o1.compareTo(o2);
            }
        });
    }

    /**
     * @return the list of custom menus
     */
    public List<JMenu> getCustomMenus() {
        synchronized (mCustomMenus) {
            return mCustomMenus;
        }
    }

    /**
     * @param customMenu a custom JMenu to add in the custom menu area
     */
    public void addCustomMenu(final JMenu customMenu) {
        synchronized (mCustomMenus) {
            if (mReservedMenuNames.contains(customMenu.getText())) {
                throw new IllegalArgumentException("Cannot add a custom menu named '"
                    + customMenu.getName() + "' as that is a reserved menu name");
            }
            mCustomMenus.add(customMenu);
        }
    }

    /**
     * @param menu a menu to add a JMenuItem to
     * @param menuComponent the JComponent to add
     */
    public void addMenuComponent(final SystemMenu menu, final JComponent menuComponent) {
        synchronized (mSystemMenus) {
            if (menu == SystemMenu.Window) {
                throw new IllegalArgumentException("Cannot add a menu item to the "
                    + SystemMenu.Window.toString() + " menu as it is a private menu");
            }
            final List<JComponent> menuList = mSystemMenus.get(menu);
            if (mReservedSystemMenus.containsKey(menu)) {
                final Set<String> reservedNames = mReservedSystemMenus.get(menu);
                if (menuComponent instanceof JMenuItem) {
                    if (reservedNames.contains(((JMenuItem) menuComponent).getText())) {
                        throw new IllegalArgumentException("Cannot add a menu item named '"
                            + ((JMenuItem) menuComponent).getText() + "' to the " + menu.toString() + " menu"
                            + " as that is a reserved menu item name");
                    }
                }
            }
            if (menuComponent instanceof JSeparator) {
                if (menuList.size() > 0
                        && menuList.get(menuList.size() - 1) instanceof JSeparator) {
                    return;
                }
            }
            menuList.add(menuComponent);
        }
    }

    /**
     * @param menu a menu to retrieve the JComponents for
     * @return the JComponents, always a list, possibly empty
     */
    public List<JComponent> getMenu(final SystemMenu menu) {
        synchronized (mSystemMenus) {
            return new ArrayList<JComponent>(mSystemMenus.get(menu));
        }
    }

    /**
     * @param tabIdentifier a TabIdentifier to add to the View menu
     */
    public void addViewMenuTabIdentifier(final TabIdentifier tabIdentifier) {
        if (tabIdentifier == null) {
            throw new IllegalArgumentException("Cannot add a null TabIdentifier to the View menu");
        }
        final Set<TabIdentifier> sysTabs = new HashSet<TabIdentifier>(Arrays.asList(SystemTabIdentifiers.values()));
        if (sysTabs.contains(tabIdentifier)) {
            throw new IllegalArgumentException("Cannot add the TabIdentifier '" + tabIdentifier.getTabName()
                + " to the View menu as it has the same name as a System TabIdentifier");
        }
        mViewMenuTabIdentifiers.add(tabIdentifier);
    }

    /**
     * @return a sorted list of TabIdentifiers that are attached to the View
     * menu, containing no duplicates.
     */
    public List<TabIdentifier> getViewMenuTabIdentifiers() {
        return Arrays.asList(mViewMenuTabIdentifiers.toArray(new TabIdentifier[0]));
    }
}
