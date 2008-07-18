package uk.me.gumbley.minimiser.recentlist;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.recentlist.QuoteEscape;


/**
 * Tests the escaping and unescaping of double quotes.
 * 
 * @author matt
 *
 */
public class TestQuoteEscape {
    /**
     * 
     */
    @Test
    public void testEscapeQuotesWhenNoQuotes() {
        Assert.assertEquals("foo", QuoteEscape.escapeQuotes("foo"));
    }
    
    /**
     * 
     */
    @Test
    public void testEscapeQuotesWhenQuotes() {
        final StringBuilder sb = new StringBuilder();
        sb.append('f');
        sb.append('"');
        sb.append("oo");
        Assert.assertEquals("f\\\"oo", QuoteEscape.escapeQuotes(sb.toString()));
    }

    /**
     * 
     */
    @Test
    public void testEscapeQuotesWhenMultipleQuotes() {
        final StringBuilder sb = new StringBuilder();
        sb.append('f');
        sb.append('"');
        sb.append("oo");
        sb.append('"');
        sb.append("ball");
        Assert.assertEquals("f\\\"oo\\\"ball", QuoteEscape.escapeQuotes(sb.toString()));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        Assert.assertEquals("foo", QuoteEscape.unescapeQuotes("foo"));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenQuotes() {
        Assert.assertEquals("fo\"o", QuoteEscape.unescapeQuotes("fo\\\"o"));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenQuotesPrefixedByExtraSlash() {
        Assert.assertEquals("fo\\\"o", QuoteEscape.unescapeQuotes("fo\\\\\"o"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenMultipleQuotes() {
        Assert.assertEquals("fo\"ot\"ball\"", QuoteEscape.unescapeQuotes("fo\\\"ot\\\"ball\\\""));
    }
}
