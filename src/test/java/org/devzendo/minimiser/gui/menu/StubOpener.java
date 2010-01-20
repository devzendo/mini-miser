package org.devzendo.minimiser.gui.menu;

import org.devzendo.minimiser.opener.DatabaseOpenEvent;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * A stub opener that just issues a DatabaseOpenEvent immediately when
 * openDatabase is called.
 * 
 * @author matt
 *
 */
public final class StubOpener implements Opener {
    private final ObserverList<DatabaseOpenEvent> observerList;

    /**
     * Construct the StubOpener
     */
    public StubOpener() {
        observerList = new ObserverList<DatabaseOpenEvent>();
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseOpenObserver(final Observer<DatabaseOpenEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        observerList.eventOccurred(new DatabaseOpenEvent(new DatabaseDescriptor(dbName, pathToDatabase)));
        return null;
    }
}
