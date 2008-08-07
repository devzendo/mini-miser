/**
 * 
 */
package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.List;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.recentlist.AbstractRecentFilesListImpl;

/**
 * A stub recent file list that accepts observers, and
 * notifies them when changes occurs to the list, in the same manner as
 * the real one, but with no backing store.
 * 
 * @author matt
 *
 */
public final class StubRecentFilesList extends AbstractRecentFilesListImpl {
    /**
     * 
     */
    StubRecentFilesList() {
        super();
    }

    /**
     * Silently add a database
     * @param databaseDescriptor the database to add
     */
    public void addDatabaseSilently(final DatabaseDescriptor databaseDescriptor) {
        addSilently(databaseDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<DatabaseDescriptor> load() {
        return new ArrayList<DatabaseDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save() {
        // do nothing
    }
}