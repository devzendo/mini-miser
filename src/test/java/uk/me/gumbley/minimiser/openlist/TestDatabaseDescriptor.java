package uk.me.gumbley.minimiser.openlist;

import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * Tests for equality, inequality, and attribute operations.
 * 
 * @author matt
 *
 */
public final class TestDatabaseDescriptor extends LoggingTestCase {
    
    /**
     * 
     */
    @Test
    public void testEquality() {
        final DatabaseDescriptor justOne = new DatabaseDescriptor("one");
        final DatabaseDescriptor justTwo = new DatabaseDescriptor("two");
        Assert.assertFalse(justOne.equals(justTwo));
        final DatabaseDescriptor oneWithPath = new DatabaseDescriptor("one", "/tmp/one");
        Assert.assertTrue(justOne.equals(oneWithPath));
        Assert.assertTrue(oneWithPath.equals(justOne));
        Assert.assertFalse(oneWithPath.equals(justTwo));
        final DatabaseDescriptor twoWithOnesPath = new DatabaseDescriptor("two", "/tmp/one");
        Assert.assertTrue(justTwo.equals(twoWithOnesPath));
        Assert.assertFalse(twoWithOnesPath.equals(oneWithPath));
        final DatabaseDescriptor oneWithOtherPath = new DatabaseDescriptor("one", "/tmp/other");
        Assert.assertTrue(justOne.equals(oneWithOtherPath));
    }
    
    /**
     * 
     */
    @Test
    public void attributesAreNullInitially() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        Assert.assertNull(dd.getAttribute(AttributeIdentifier.Database));
    }
    
    /**
     * 
     */
    @Test
    public void pathAttributeIsEmptyInitially() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        Assert.assertEquals("", dd.getAttribute(AttributeIdentifier.Path));
        Assert.assertEquals("", dd.getDatabasePath());
    }
    
    /**
     * 
     */
    @Test
    public void attributesCanBeSet() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        dd.setAttribute(AttributeIdentifier.Path, "foo");
        Assert.assertEquals("foo", dd.getAttribute(AttributeIdentifier.Path));
    }

    /**
     * 
     */
    @Test
    public void attributesCanBeCleared() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        dd.setAttribute(AttributeIdentifier.Database, "foo");
        dd.clearAttribute(AttributeIdentifier.Database);
        Assert.assertNull(dd.getAttribute(AttributeIdentifier.Database));
    }
    
    /**
     * 
     */
    @Test
    public void pathCanBeGotFromAnAttributeAndAGetter() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one", "/tmp/foo");
        Assert.assertEquals("/tmp/foo", dd.getDatabasePath());
        Assert.assertEquals("/tmp/foo", dd.getAttribute(AttributeIdentifier.Path));
    }

    /**
     * 
     */
    @Test
    public void unsetPathIsEmptyFromAnAttributeAndAGetter() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one");
        Assert.assertEquals("", dd.getDatabasePath());
        Assert.assertEquals("", dd.getAttribute(AttributeIdentifier.Path));
    }

    /**
     * 
     */
    @Test
    public void clearedPathIsEmptyFromAnAttributeAndAGetter() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one", "/tmp/foo");
        dd.clearAttribute(AttributeIdentifier.Path);
        Assert.assertEquals("", dd.getDatabasePath());
        Assert.assertEquals("", dd.getAttribute(AttributeIdentifier.Path));
    }

    /**
     * 
     */
    @Test
    public void pathCanBeSetNullIfThatsWhatYouReallyReallyWant() {
        final DatabaseDescriptor dd = new DatabaseDescriptor("one", "/tmp/foo");
        dd.setAttribute(AttributeIdentifier.Path, null);
        Assert.assertNull(dd.getDatabasePath());
        Assert.assertNull(dd.getAttribute(AttributeIdentifier.Path));
    }
}
