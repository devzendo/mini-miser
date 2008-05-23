package uk.me.gumbley.minimiser.gui.mm;

/**
 * The MiniMiser menu is controlled by an implementation of Menu.
 *  
 * @author matt
 *
 */
public interface Menu {
    /**
     * Enable or disable the close menu item.
     * @param enabled true to enable, false to disable.
     */
    void enableCloseMenu(boolean enabled);
}
