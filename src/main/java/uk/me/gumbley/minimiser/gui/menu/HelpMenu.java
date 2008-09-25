package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;

/**
 * The Help menu. Small, has no interesting functionality that couldn't be
 * implemented directly in MenuImpl, but encapsulated here to reduce the
 * coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class HelpMenu extends AbstractMenuGroup {
    private JMenu helpmenu;

    /**
     * Construct the help menu
     * 
     * @param wiring the menu wiring
     * @param state the menu state
     * @param menu the main menu
     */
    public HelpMenu(final MenuWiring wiring, final MenuState state, final MenuImpl menu) {
        super(wiring, state, menu);

        helpmenu = new JMenu("Help");
        helpmenu.setMnemonic('H');
        
        //createMenuItem(MenuIdentifier.HelpWelcome, "Welcome", 'W', menu);
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        menu.createMenuItem(MenuIdentifier.HelpAbout, "About " + AppName.getAppName(), 'A', helpmenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return helpmenu;
    }
}
