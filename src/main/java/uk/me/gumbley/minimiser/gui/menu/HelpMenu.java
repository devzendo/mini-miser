package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;

/**
 * The Help menu. Small, has no interesting functionality that
 * couldn't be implemented directly in MenuImpl, but encapsulated
 * here to reduce the coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class HelpMenu extends AbstractMenuGroup {
    private final JMenu helpmenu;

    /**
     * Construct the help menu
     * 
     * @param wiring the menu wiring
     */
    public HelpMenu(final MenuWiring wiring) {
        super(wiring);

        helpmenu = new JMenu("Help");
        helpmenu.setMnemonic('H');
        
        createMenuItem(MenuIdentifier.HelpWelcome, "Welcome to " + AppName.getAppName(), 'W', helpmenu);
        createMenuItem(MenuIdentifier.HelpWhatsNew, "What's new in this release?", 'N', helpmenu);
        helpmenu.add(new JSeparator());
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        createMenuItem(MenuIdentifier.HelpAbout, "About " + AppName.getAppName(), 'A', helpmenu);
        helpmenu.add(new JSeparator());
        createMenuItem(MenuIdentifier.HelpCheckForUpdates, "Check for updates", 'U', helpmenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return helpmenu;
    }
}
