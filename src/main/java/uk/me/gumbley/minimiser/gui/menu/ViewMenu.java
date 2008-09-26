package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;

/**
 * The View menu is rebuildable on initialisation and when prefs' hidden tabs
 * change, so that tabs that are hidden are removed, and that those that have
 * been enabled are shown.
 * 
 * @author matt
 *
 */
public final class ViewMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(ViewMenu.class);
    private JMenu viewMenu;

    /**
     * Construct the view menu
     * 
     * @param wiring the menu wiring
     * @param state the menus state
     * @param menu the main menu
     */
    public ViewMenu(final MenuWiring wiring, final MenuState state, final MenuImpl menu) {
        super(wiring, state, menu);
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        viewMenu.removeAll();
        if (getMenuState().getNumberOfDatabases() == 0) {
            viewMenu.setEnabled(false);
            LOGGER.debug("view menu is empty");
            return;
        }

        for (final TabIdentifier tabId : TabIdentifier.values()) {
            final boolean viewMenuItemHidden = getMenuState().isViewMenuItemHidden(tabId.toString());
            //final boolean tabPresent = isTabPresent(tabId);
            LOGGER.debug("View menu item " + tabId + " hidden:" + viewMenuItemHidden);
            if (!tabId.isTabPermanent() && !viewMenuItemHidden) {
                final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(tabId.getDisplayableName());
                menuItem.setMnemonic(tabId.getMnemonic());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.currentThread().setName("ViewOpener:" + tabId.getDisplayableName());
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info("Opening view '" + tabId.getDisplayableName() + "'");
                                    //openRecentSubmenuChoiceObservers.eventOccurred(
                                    //new DatabaseNameAndPathChoice(recentDbName, recentDbPath));        
                                } catch (final Throwable t) {
                                    LOGGER.error("View opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                                } finally {
                                    LOGGER.debug("Open view complete");
                                }
                            }
                        }).start();
                    }
                });
                viewMenu.add(menuItem);
            }
        }
        viewMenu.setEnabled(true);
    }
//    
//    private boolean isTabPresent(final TabIdentifier tabId) {
//        return false;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return viewMenu;
    }
}