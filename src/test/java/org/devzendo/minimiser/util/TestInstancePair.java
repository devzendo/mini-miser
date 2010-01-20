package org.devzendo.minimiser.util;

import org.devzendo.minimiser.util.InstanceClasses.A;
import org.devzendo.minimiser.util.InstanceClasses.BaseInterface;
import org.junit.Assert;
import org.junit.Test;


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
