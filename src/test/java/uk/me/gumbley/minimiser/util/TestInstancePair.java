package uk.me.gumbley.minimiser.util;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.util.InstanceClasses.A;
import uk.me.gumbley.minimiser.util.InstanceClasses.BaseInterface;

/**
 * Tests for the InstancePair.
 * 
 * @author matt
 *
 */
public final class TestInstancePair {
    private InstancePair<BaseInterface> mInstancePair;

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void classMustBeSupplied() {
        mInstancePair = new InstancePair<BaseInterface>(null, new A("foo"));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void instanceMustBeSupplied() {
        mInstancePair = new InstancePair<BaseInterface>(A.class, null);
    }
    
    /**
     * 
     */
    @Test
    public void canRetrieveCorrectly() {
        final A a = new A("foo");
        mInstancePair = new InstancePair<BaseInterface>(A.class, a);
        Assert.assertEquals(A.class, mInstancePair.getClassOfInstance());
        Assert.assertSame(a, mInstancePair.getInstance());
    }
}
