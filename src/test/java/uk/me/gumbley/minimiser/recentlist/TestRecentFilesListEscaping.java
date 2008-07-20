package uk.me.gumbley.minimiser.recentlist;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.recentlist.DefaultRecentFilesListImpl.DbPair;


/**
 * Tests of the escaping mechanism used in the RecentFilesList.
 * 
 * @author matt
 *
 */
public final class TestRecentFilesListEscaping {
    /**
     * 
     */
    @Test
    public void testEscapeWhenNoQuotes() {
        Assert.assertEquals("\"one\",\"path\"", DefaultRecentFilesListImpl.escape("one", "path"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        final DbPair unescaped = DefaultRecentFilesListImpl.unescape("\"one\",\"two\"");
        Assert.assertEquals("one", unescaped.getName());
        Assert.assertEquals("two", unescaped.getPath());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnescapeFailure() {
        DefaultRecentFilesListImpl.unescape("\"f\\\"oo\",\"/tmp/\\\"quote"); // no end quote
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
        final String escaped = DefaultRecentFilesListImpl.escape(name.toString(), path.toString());
        Assert.assertEquals("\"f\\\"oo\",\"/tmp/\\\"quote\"", escaped);

        final DbPair unescaped = DefaultRecentFilesListImpl.unescape(escaped);
        Assert.assertEquals(name.toString(), unescaped.getName());
        Assert.assertEquals(path.toString(), unescaped.getPath());
    }
}
