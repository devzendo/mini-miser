package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;

/**
 * The Tools menu. Small, has no interesting functionality that couldn't be
 * implemented directly in MenuImpl, but encapsulated here to reduce the
 * coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class ToolsMenu extends AbstractMenuGroup {
    private JMenu toolsMenu;

    /**
     * Construct the tools menu
     * 
     * @param wiring the menu wiring
     * @param state the menu state
     */
    public ToolsMenu(final MenuWiring wiring, final MenuState state) {
        super(wiring, state);

        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        
        createMenuItem(MenuIdentifier.ToolsOptions, "Options...", 'O', toolsMenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return toolsMenu;
    }
}
