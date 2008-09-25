package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import org.apache.log4j.Logger;

/**
 * The Window menu rebuilds when databases are added, removed, or switched to.
 * It contains a list of all the open databases, allowing the user to switch
 * between them.
 * 
 * @author matt
 *
 */
public final class WindowMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(WindowMenu.class);
    private JMenu windowMenu;

    /**
     * Construct the Window Menu
     * 
     * @param wiring the menu wiring
     * @param state the menu state
     * @param menu the main menu
     */
    public WindowMenu(final MenuWiring wiring, final MenuState state, final MenuImpl menu) {
        super(wiring, state, menu);
        windowMenu = new JMenu("Window");
        windowMenu.setMnemonic('W');
        // no need to call buildWindowMenu() as it's not populated initially
        windowMenu.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        LOGGER.debug("building window menu");
        windowMenu.removeAll();
        final int numberOfDatabases = getMainMenu().getNumberOfDatabases();
        if (numberOfDatabases == 0) {
            windowMenu.setEnabled(false);
            LOGGER.debug("window menu is empty");
            return;
        }
        LOGGER.debug("building window list");
        for (int i = 0; i < numberOfDatabases; i++) {
            final String database = getMainMenu().getDatabase(i);
            LOGGER.debug("adding " + database + " to window list");
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(database, i == getMainMenu().getCurrentDatabaseIndex());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    getMainMenu().dispatchToWindowMenuChoiceObservers(new DatabaseNameChoice(database));
                }
            });
            windowMenu.add(menuItem);
        }
        windowMenu.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return windowMenu;
    }
}
