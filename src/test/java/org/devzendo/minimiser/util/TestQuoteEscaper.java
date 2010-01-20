package org.devzendo.minimiser.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the escaping and unescaping of double quotes.
 * 
 * @author matt
 *
 */
public final class TestQuoteEscaper {
    /**
     * 
     */
    @Test
    public void testEscapeQuotesWhenNoQuotes() {
        Assert.assertEquals("foo", QuoteEscaper.escapeQuotes("foo"));
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
        Assert.assertEquals("f\\\"oo", QuoteEscaper.escapeQuotes(sb.toString()));
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
        Assert.assertEquals("f\\\"oo\\\"ball", QuoteEscaper.escapeQuotes(sb.toString()));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        Assert.assertEquals("foo", QuoteEscaper.unescapeQuotes("foo"));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenQuotes() {
        Assert.assertEquals("fo\"o", QuoteEscaper.unescapeQuotes("fo\\\"o"));
    }

    /**
     * 
     */
    @Test
    public void testUnescapeWhenQuotesPrefixedByExtraSlash() {
        Assert.assertEquals("fo\\\"o", QuoteEscaper.unescapeQuotes("fo\\\\\"o"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenMultipleQuotes() {
        Assert.assertEquals("fo\"ot\"ball\"", QuoteEscaper.unescapeQuotes("fo\\\"ot\\\"ball\\\""));
    }
}
