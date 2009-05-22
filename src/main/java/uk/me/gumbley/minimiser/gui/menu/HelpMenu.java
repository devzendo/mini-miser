package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;

/**
 * The Help menu. Small, has no interesting functionality that
 * couldn't be implemented directly in MenuImpl, but encapsulated
 * here to reduce the coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class HelpMenu extends AbstractMenuGroup {
    private final JMenu mHelpmenu;
    private final AppDetails mAppDetails;

    /**
     * Construct the help menu
     * 
     * @param wiring the menu wiring
     * @param appDetails the application details
     */
    public HelpMenu(final MenuWiring wiring, final AppDetails appDetails) {
        super(wiring);
        mAppDetails = appDetails;

        mHelpmenu = new JMenu("Help");
        mHelpmenu.setMnemonic('H');
        
        createMenuItem(MenuIdentifier.HelpWelcome, "Welcome to " + mAppDetails.getApplicationName(), 'W', mHelpmenu);
        createMenuItem(MenuIdentifier.HelpWhatsNew, "What's new in this release?", 'N', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        createMenuItem(MenuIdentifier.HelpAbout, "About " + mAppDetails.getApplicationName(), 'A', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        createMenuItem(MenuIdentifier.HelpCheckForUpdates, "Check for updates", 'U', mHelpmenu);
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
}
