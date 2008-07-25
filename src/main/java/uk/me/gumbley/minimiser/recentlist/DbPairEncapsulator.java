package uk.me.gumbley.minimiser.recentlist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Toolkit for encapsulating a (database name, database path) pair into a single
 * String, and unencapsulating a thusly-encapsulated String back into
 * a pair.
 * @author matt
 *
 */
public final class DbPairEncapsulator {
    /**
     * Pointless CTOR
     */
    private DbPairEncapsulator() {
        // no instances
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
     * Escape a database name and path into a single String that can be
     * stored in prefs and later unescaped.
     * @param dbName a database name
     * @param dbPath a path
     * @return the escaped name. Currently double quotes are escaped.
     */
    public static String escape(final String dbName, final String dbPath) {
        final StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append(QuoteEscaper.escapeQuotes(dbName));
        sb.append('"');
        sb.append(',');
        sb.append('"');
        sb.append(QuoteEscaper.escapeQuotes(dbPath));
        sb.append('"');
        return sb.toString();
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
            return new DbPair(QuoteEscaper.unescapeQuotes(matcher.group(1)),
                              QuoteEscaper.unescapeQuotes(matcher.group(2)));
        }
        throw new IllegalArgumentException("Could not unescape the pair [" + escapedPair + "]");
    }
}
