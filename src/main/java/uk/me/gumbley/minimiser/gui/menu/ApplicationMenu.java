package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

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

    /**
     * Construct an empty Application Menu
     */
    public ApplicationMenu() {
        mCustomMenus = new ArrayList<JMenu>();
    }

    /**
     * @return the list of custom menus
     */
    public List<JMenu> getCustomMenus() {
        return mCustomMenus;
    }

    /**
     * @param customMenu a custom JMenu to add in the custom mnu area
     */
    public void addCustomMenu(final JMenu customMenu) {
        mCustomMenus.add(customMenu);
    }
}
