package uk.me.gumbley.minimiser.recentlist;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A RecentFilesList implementation that persists via prefs.
 * 
 * @author matt
 *
 */
public final class DefaultRecentFilesListImpl extends AbstractRecentFilesListImpl {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultRecentFilesListImpl.class);
    private final Prefs preferences;

    /**
     * Construct a recent files list
     * @param prefs the prefs in which to store the recent files list
     */
    public DefaultRecentFilesListImpl(final Prefs prefs) {
        super();
        preferences = prefs;
        setDatabaseList(load());
    }

    protected void save() {
        LOGGER.info("saving recent files...");
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        for (DatabaseDescriptor databaseDescriptor : getDatabaseList()) {
            listAsStringPaths.add(DbPairEncapsulator.escape(databaseDescriptor.getDatabaseName(),
                databaseDescriptor.getDatabasePath()));
        }
        for (String string : listAsStringPaths) {
            LOGGER.info("  " + string);
        }
        preferences.setRecentFiles(listAsStringPaths.toArray(new String[0]));
    }
    
    protected List<DatabaseDescriptor> load() {
        LOGGER.info("loading recent files...");
        final String[] recentEscapedNamesAndPaths = preferences.getRecentFiles();
        final List<DatabaseDescriptor> descriptors = new ArrayList <DatabaseDescriptor>();
        for (String nameAndPath : recentEscapedNamesAndPaths) {
            try {
                final DbPairEncapsulator.DbPair pair = DbPairEncapsulator.unescape(nameAndPath);
                LOGGER.info("stored " + nameAndPath + " name " + pair.getName() + " path " + pair.getPath());
                descriptors.add(new DatabaseDescriptor(pair.getName(), pair.getPath()));
            } catch (final IllegalArgumentException iae) {
                // TODO should this be a warning in a dialog?
                LOGGER.warn("Could not reload entry from recent list '" + nameAndPath + "'");
            }
        }
        return descriptors;
    }
}
