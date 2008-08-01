package uk.me.gumbley.minimiser.recentlist;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the escaping mechanism used in the RecentFilesList.
 * 
 * @author matt
 *
 */
public final class TestDbPairEncapsulator {
    /**
     * 
     */
    @Test
    public void testEscapeWhenNoQuotes() {
        Assert.assertEquals("\"one\",\"path\"", DatabasePairEncapsulator.escape("one", "path"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        final DatabasePair unescaped = DatabasePairEncapsulator.unescape("\"one\",\"two\"");
        Assert.assertEquals("one", unescaped.getName());
        Assert.assertEquals("two", unescaped.getPath());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnescapeFailure() {
        DatabasePairEncapsulator.unescape("\"f\\\"oo\",\"/tmp/\\\"quote"); // no end quote
    }
    
    /**
     * 
     */
    @Test
    public void testRoundTripWithQuotes() {
        final StringBuilder name = new StringBuilder();
        name.append('f');
        name.append('"');
        name.append("oo");
        final StringBuilder path = new StringBuilder();
        path.append("/tmp/");
        path.append('"');
        path.append("quote");
        final String escaped = DatabasePairEncapsulator.escape(name.toString(), path.toString());
        Assert.assertEquals("\"f\\\"oo\",\"/tmp/\\\"quote\"", escaped);

        final DatabasePair unescaped = DatabasePairEncapsulator.unescape(escaped);
        Assert.assertEquals(name.toString(), unescaped.getName());
        Assert.assertEquals(path.toString(), unescaped.getPath());
    }
}
