package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.prefs.Prefs;

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
    private final OpenDatabaseList openDatabaseList;
    private final Prefs prefs;
    private ObserverList<ViewMenuChoice> viewMenuChoiceObservers;

    /**
     * Construct the view menu
     * 
     * @param wiring the menu wiring
     * @param databaseList the Open Database List
     * @param preferences the Preferences
     */
    public ViewMenu(final MenuWiring wiring,
            final OpenDatabaseList databaseList,
            final Prefs preferences) {
        super(wiring);
        this.openDatabaseList = databaseList;
        this.prefs = preferences;
        viewMenuChoiceObservers = new ObserverList<ViewMenuChoice>();
        
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        viewMenu.removeAll();
        if (openDatabaseList.getNumberOfDatabases() == 0) {
            viewMenu.setEnabled(false);
            LOGGER.debug("view menu is empty");
            return;
        }
        
//        final HashSet<TabDescriptor> tabDescriptorSet =
//            new HashSet<TabDescriptor>(
//                    openTabList.getTabsForDatabase(
//                        openDatabaseList.getCurrentDatabase().getDatabaseName()));
        
        final DatabaseDescriptor currentDatabase = openDatabaseList.getCurrentDatabase();
        
        for (final TabIdentifier tabId : TabIdentifier.values()) {
            final boolean viewMenuItemHidden = prefs.isTabHidden(tabId.toString());
            LOGGER.debug("View menu item " + tabId + " hidden:" + viewMenuItemHidden);
            if (!tabId.isTabPermanent() && !viewMenuItemHidden) {
                final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(tabId.getDisplayableName());
                menuItem.setMnemonic(tabId.getMnemonic());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        final boolean opened = menuItem.isSelected();
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.currentThread().setName("ViewOpener:" + tabId.getDisplayableName());
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info((opened ? "Opening" : "Closing") + " view '" + tabId.getDisplayableName() + "'");
                                    viewMenuChoiceObservers.eventOccurred(new ViewMenuChoice(currentDatabase, tabId, opened));
                                } catch (final Throwable t) {
                                    LOGGER.error("View opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                                } finally {
                                    LOGGER.debug("View menu choice complete");
                                }
                            }
                        }).start();
                    }
                });
                //menuItem.setSelected(tabDescriptorSet.contains(new TabDescriptor(tabId)));
                viewMenu.add(menuItem);
            }
        }
        viewMenu.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return viewMenu;
    }
    
    /**
     * Add a recent menu observer
     * @param observer the observer
     */
    public void addViewObserver(final Observer<ViewMenuChoice> observer) {
        viewMenuChoiceObservers.addObserver(observer);
    }
}
