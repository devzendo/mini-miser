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
        Assert.assertEquals("\"one\",\"path\"", DbPairEncapsulator.escape("one", "path"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        final DbPairEncapsulator.DbPair unescaped = DbPairEncapsulator.unescape("\"one\",\"two\"");
        Assert.assertEquals("one", unescaped.getName());
        Assert.assertEquals("two", unescaped.getPath());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnescapeFailure() {
        DbPairEncapsulator.unescape("\"f\\\"oo\",\"/tmp/\\\"quote"); // no end quote
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
        final String escaped = DbPairEncapsulator.escape(name.toString(), path.toString());
        Assert.assertEquals("\"f\\\"oo\",\"/tmp/\\\"quote\"", escaped);

        final DbPairEncapsulator.DbPair unescaped = DbPairEncapsulator.unescape(escaped);
        Assert.assertEquals(name.toString(), unescaped.getName());
        Assert.assertEquals(path.toString(), unescaped.getPath());
    }
}
