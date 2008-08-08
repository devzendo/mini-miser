package uk.me.gumbley.minimiser.util;

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
