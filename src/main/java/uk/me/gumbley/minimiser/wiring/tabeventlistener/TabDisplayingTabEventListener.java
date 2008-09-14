package uk.me.gumbley.minimiser.wiring.tabeventlistener;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.opentablist.TabEvent;

/**
 * A TabEvent listener that detects tab addition, and adds the tab into the
 * right place on the database descriptor's JTabbedPane.
 * 
 * @author matt
 *
 */
public final class TabDisplayingTabEventListener implements Observer<TabEvent> {

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final TabEvent observableEvent) {
        // TODO Auto-generated method stub
        
    }
}
