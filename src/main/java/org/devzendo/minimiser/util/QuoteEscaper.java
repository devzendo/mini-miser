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

/**
 * Toolkit for escaping and unescaping double quotes in Strings.
 * 
 * @author matt
 *
 */
public final class QuoteEscaper {
    /**
     * No instances
     */
    private QuoteEscaper() {
        
    }

    /**
     * Escape the quotes in a string
     * @param str the input string
     * @return the string with qu"ote replaced by qu\"ote
     */
    static String escapeQuotes(final String str) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            if (ch == '"') {
                sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Unescape an escaped string
     * @param str a string with escaped qu\"outes
     * @return the string with unexcaped qu"otes
     */
    static String unescapeQuotes(final String str) {
        return str.replaceAll("\\\\\"", "\"");
    }
}
