package uk.me.gumbley.minimiser.gui.menu;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.opener.DatabaseOpenEvent;
import uk.me.gumbley.minimiser.opener.IOpener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;

/**
 * A stub opener that just issues a DatabaseOpenEvent immediately when
 * openDatabase is called.
 * 
 * @author matt
 *
 */
public class StubOpener implements IOpener {
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
    public MiniMiserDatabase openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        observerList.eventOccurred(new DatabaseOpenEvent(new DatabaseDescriptor(dbName, pathToDatabase)));
        return null;
    }
}
