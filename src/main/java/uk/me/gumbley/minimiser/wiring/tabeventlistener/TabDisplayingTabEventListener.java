package uk.me.gumbley.minimiser.wiring.tabeventlistener;

import java.awt.Component;
import javax.swing.JTabbedPane;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.opentablist.TabEvent;
import uk.me.gumbley.minimiser.opentablist.TabOpenedEvent;

/**
 * A TabEvent listener that detects tab addition, and adds the tab into the
 * right place on the database descriptor's JTabbedPane.
 * 
 * @author matt
 *
 */
public final class TabDisplayingTabEventListener implements Observer<TabEvent> {
    
    private final OpenTabList openTabList;

    /**
     * Create the event listener
     * @param tabList the OpenTabList that's needed to compute the correct
     * insertion point for new tabs.
     */
    public TabDisplayingTabEventListener(final OpenTabList tabList) {
        this.openTabList = tabList;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final TabEvent observableEvent) {
        if (observableEvent instanceof TabOpenedEvent) {
            processTabOpened((TabOpenedEvent) observableEvent);
        }
    }

    private void processTabOpened(final TabOpenedEvent openedEvent) {
        final DatabaseDescriptor databaseDescriptor = openedEvent.getDatabaseDescriptor();
        if (databaseDescriptor == null) {
            throw new IllegalStateException("Null database descriptor");
        }
        final JTabbedPane tabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
        if (tabbedPane == null) {
            throw new IllegalStateException("Null JTabbedPane");
        }
        
        final TabDescriptor tabDescriptor = openedEvent.getTabDescriptor();
        if (tabDescriptor == null) {
            throw new IllegalStateException("Null tab descriptor");
        }
        final Tab tab = tabDescriptor.getTab();
        if (tab == null) {
            throw new IllegalStateException("Null tab");
        }
        final Component component = tab.getComponent();
        if (component == null) {
            throw new IllegalStateException("Null Component");
        }
        
        final int insertionPoint = openTabList.getInsertionPosition(
            databaseDescriptor.getDatabaseName(),
            tabDescriptor.getTabIdentifier());
        
        // TODO perhaps the OpenTabList should be throwing the IllegalStateException here?
        if (insertionPoint == -1) {
            throw new IllegalStateException("Cannot get insertion point for tab: database '" 
                + databaseDescriptor.getDatabaseName() + "' not added to open tab list");
        }
        
        tabbedPane.add(component, tabDescriptor.getTabIdentifier().getDisplayableName(), insertionPoint);
    }
}
