package uk.me.gumbley.minimiser.recentlist;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Handles CRUD operations on the Recent Files List, persists via the prefs
 * object.
 * 
 * @author matt
 *
 */
public final class DefaultRecentFilesListImpl implements RecentFilesList {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultRecentFilesListImpl.class);
    private final ObserverList<RecentListEvent> observerList;
    private final List<DatabaseDescriptor> databaseList;
    private final Prefs preferences;

    /**
     * Construct a recent files list
     * @param prefs the prefs in which to store the recent files list
     */
    public DefaultRecentFilesListImpl(final Prefs prefs) {
        preferences = prefs;
        observerList = new ObserverList<RecentListEvent>();
        databaseList = load();
    }

    /**
     * {@inheritDoc}
     */
    
    public int getNumberOfEntries() {
        return databaseList.size();
    }

    /**
     * {@inheritDoc}
     */
    
    public void add(final DatabaseDescriptor databaseDescriptor) {
        final int indexOf = databaseList.indexOf(databaseDescriptor);
        if (indexOf != -1) {
            // the one being added is already present
            if (!databaseList.get(0).equals(databaseDescriptor)) {
                // the one being added is not at the head, so put it there  
                databaseList.remove(indexOf);
                databaseList.add(0, databaseDescriptor);
                observerList.eventOccurred(new RecentListEvent());
                save();
            }
            // else it's already at the head, so don't bother saving
        } else {
            // the one being added is not present
            databaseList.add(0, databaseDescriptor);
            if (databaseList.size() > DEFAULT_CAPACITY) {
                databaseList.remove(DEFAULT_CAPACITY);
            }
            observerList.eventOccurred(new RecentListEvent());
            save();
        }
    }

    private void save() {
        LOGGER.info("saving recent files...");
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        for (DatabaseDescriptor databaseDescriptor : databaseList) {
            listAsStringPaths.add(escape(databaseDescriptor.getDatabaseName(),
                databaseDescriptor.getDatabasePath()));
        }
        for (String string : listAsStringPaths) {
            LOGGER.info("  " + string);
        }
        preferences.setRecentFiles(listAsStringPaths.toArray(new String[0]));
    }
    
    private List<DatabaseDescriptor> load() {
        LOGGER.info("loading recent files...");
        final String[] recentEscapedNamesAndPaths = preferences.getRecentFiles();
        final List<DatabaseDescriptor> descriptors = new ArrayList <DatabaseDescriptor>();
        for (String nameAndPath : recentEscapedNamesAndPaths) {
            final DbPair pair = unescape(nameAndPath);
            LOGGER.info("stored " + nameAndPath + " name " + pair.getName() + " path " + pair.getPath());
            descriptors.add(new DatabaseDescriptor(pair.getName(), pair.getPath()));
        }
        return descriptors;
    }
    
    /**
     * {@inheritDoc}
     */
    
    public int getCapacity() {
        // TODO later, possibly allow this to be configured via prefs.
        // reducing the size of the list would truncate.
        return DEFAULT_CAPACITY;
    }

    /**
     * {@inheritDoc}
     */
    
    public DatabaseDescriptor[] getRecentFiles() {
        return databaseList.toArray(new DatabaseDescriptor[0]);
    }

    /**
     * Escape a database name and path into a single String that can be
     * stored in prefs and later unescaped.
     * @param dbName a database name
     * @param dbPath a path
     * @return the escaped name. Currently double quotes are escaped.
     */
    static String escape(final String dbName, final String dbPath) {
        final StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append(QuoteEscape.escapeQuotes(dbName));
        sb.append('"');
        sb.append(',');
        sb.append('"');
        sb.append(QuoteEscape.escapeQuotes(dbPath));
        sb.append('"');
        return sb.toString();
    }


    /**
     * A bean for unescaping an escaped string into its constituent parts
     * @author matt
     *
     */
    static final class DbPair {
        private final String name;
        private final String path;

        /**
         * Construct a pair
         * @param dbName the database name
         * @param dbPath th edatabase path
         */
        public DbPair(final String dbName, final String dbPath) {
            this.name = dbName;
            this.path = dbPath;
        }

        /**
         * @return the database name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the database path
         */
        public String getPath() {
            return path;
        }
    }
    
    /**
     * Unescape an escaped "databasename,databasepath" string into its
     * constituent parts.
     * @param escapedPair string of the form "name,path"
     * @return a DbPair containing the name and path, unescaped.
     */
    public static DbPair unescape(final String escapedPair) {
        final Pattern pattern = Pattern.compile("^\"(.*)\",\"(.*)\"$");
        final Matcher matcher = pattern.matcher(escapedPair);
        if (matcher.matches()) {
            return new DbPair(QuoteEscape.unescapeQuotes(matcher.group(1)),
                              QuoteEscape.unescapeQuotes(matcher.group(2)));
        }
        throw new IllegalArgumentException("Could not unescape the pair [" + escapedPair + "]");
    }

    /**
     * {@inheritDoc}
     */
    public void addRecentListEventObserver(final Observer<RecentListEvent> observer) {
        observerList.addObserver(observer);
    }
}
