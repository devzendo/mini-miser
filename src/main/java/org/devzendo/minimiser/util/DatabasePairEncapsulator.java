/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Toolkit for encapsulating a (database name, database path) pair into a single
 * String, and unencapsulating a thusly-encapsulated String back into
 * a pair.
 * @author matt
 *
 */
public final class DatabasePairEncapsulator {
    /**
     * Pointless CTOR
     */
    private DatabasePairEncapsulator() {
        // no instances
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
    public static DatabasePair unescape(final String escapedPair) {
        final Pattern pattern = Pattern.compile("^\"(.*)\",\"(.*)\"$");
        final Matcher matcher = pattern.matcher(escapedPair);
        if (matcher.matches()) {
            return new DatabasePair(QuoteEscaper.unescapeQuotes(matcher.group(1)),
                              QuoteEscaper.unescapeQuotes(matcher.group(2)));
        }
        throw new IllegalArgumentException("Could not unescape the pair [" + escapedPair + "]");
    }
}
