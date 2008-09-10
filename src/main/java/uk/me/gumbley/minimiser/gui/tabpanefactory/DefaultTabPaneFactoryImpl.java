package uk.me.gumbley.minimiser.gui.tabpanefactory;

import java.awt.Frame;
import java.util.List;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The default implementation of the TabPaneFactory.
 * 
 * @author matt
 *
 */
public class DefaultTabPaneFactoryImpl implements TabPaneFactory {
    
    private final SpringLoader springLoader;
    private final OpenTabList openTabList;
    private final Frame parentFrame;

    /**
     * Construct the TabPaneFactory, given the OpenTabList into which
     * newly created tabs will be added.
     * @param loader the SpringLoader
     * @param tabList the OpenTabList
     * @param frame the parent frame, for reporting dialogs against
     */
    public DefaultTabPaneFactoryImpl(final SpringLoader loader, final OpenTabList tabList, final Frame frame) {
        this.springLoader = loader;
        this.openTabList = tabList;
        this.parentFrame = frame;
    }

    /**
     * Construct the TabPaneFactory, given the OpenTabList into which
     * newly created tabs will be added. There's no parent frame; use
     * this variant in unit tests, for simplicity.
     * @param loader the SpringLoader
     * @param tabList the OpenTabList
     */
    public DefaultTabPaneFactoryImpl(final SpringLoader loader, final OpenTabList tabList) {
        this(loader, tabList, null);
    }

    /**
     * {@inheritDoc}
     */
    public void loadTabs(final DatabaseDescriptor databaseDescriptor, final List<TabIdentifier> tabIdentifiers) {
        // TODO set database descriptor in factory
        /*
         *         LOGGER.info(String.format("Loading ActionListener '%s'", menuIdentifier.toString()));
        wiring.setActionListener(menuIdentifier, loader.getBean("menuAL" + menuIdentifier.toString(), ActionListener.class));
         */
        final String databaseName = databaseDescriptor.getDatabaseName();
        for (TabIdentifier identifier : tabIdentifiers) {
            try {
                final Tab tab = springLoader.getBean("tab" + identifier.toString(), Tab.class);
                openTabList.addTab(databaseName, new TabDescriptor(identifier, tab));
            } catch (final RuntimeException re) {
                ProblemDialog.reportProblem(parentFrame, "while loading the " + identifier.getDisplayableName() + " tab", re);
            }
        }
        // TODO clear database descriptor in factory
        
    }
}
