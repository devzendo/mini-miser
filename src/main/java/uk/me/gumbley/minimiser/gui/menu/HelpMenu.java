package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * The Help menu. Small, has no interesting functionality that
 * couldn't be implemented directly in MenuImpl, but encapsulated
 * here to reduce the coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class HelpMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(HelpMenu.class);
    private final JMenu mHelpmenu;
    private String mApplicationName;

    /**
     * Construct the help menu
     * 
     * @param wiring the menu wiring
     * @param pluginRegistry the plugin registry
     */
    public HelpMenu(final MenuWiring wiring, final PluginRegistry pluginRegistry) {
        super(wiring);
        mApplicationName = pluginRegistry.getApplicationName();

        mHelpmenu = new JMenu("Help");
        mHelpmenu.setMnemonic('H');
        LOGGER.debug("Creating help menu");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return mHelpmenu;
    }

    /**
     * @return the enabledness of the Help|Check for Updates menu
     * item
     */
    public boolean isHelpCheckForUpdatesEnabled() {
        return getMenuWiring().isMenuItemEnabled(MenuIdentifier.HelpCheckForUpdates);
    }
    
    /**
     * Set the state of the Help|Check for Updates menu item
     * @param newEnabled true iff enabled
     */
    public void setHelpCheckForUpdatesEnabled(final boolean newEnabled) {
        getMenuWiring().setMenuItemEnabled(MenuIdentifier.HelpCheckForUpdates, newEnabled);
    }

    /**
     * Set the application name, to be displayed when the menu is
     * rebuilt.
     * @param applicationName the application name
     */
    public synchronized void setApplicationName(final String applicationName) {
        LOGGER.debug("The help menu has been notified that the application name is " + applicationName);
        mApplicationName = applicationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void rebuildMenuGroup() {
        LOGGER.debug("Rebuilding the help menu with application name " + mApplicationName);
        mHelpmenu.removeAll();
        
        replaceMenuItem(MenuIdentifier.HelpWelcome, "Welcome to " + mApplicationName, 'W', mHelpmenu);
        createMenuItem(MenuIdentifier.HelpWhatsNew, "What's new in this release?", 'N', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        replaceMenuItem(MenuIdentifier.HelpAbout, "About " + mApplicationName, 'A', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        createMenuItem(MenuIdentifier.HelpCheckForUpdates, "Check for updates", 'U', mHelpmenu);
    }
}
