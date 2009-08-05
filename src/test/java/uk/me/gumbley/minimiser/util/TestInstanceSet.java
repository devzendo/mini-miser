package uk.me.gumbley.minimiser.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.util.InstanceClasses.A;
import uk.me.gumbley.minimiser.util.InstanceClasses.B;
import uk.me.gumbley.minimiser.util.InstanceClasses.BaseInterface;

/**
 * Tests for the instance set.
 * 
 * @author matt
 *
 */
public final class TestInstanceSet {
    private InstanceSet<BaseInterface> mTypeMap;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mTypeMap = new InstanceSet<BaseInterface>();
    }
    
    /**
     * 
     */
    @Test
    public void nonExistenceReturnsNull() {
        Assert.assertNull(mTypeMap.getInstanceOf(A.class));
        Assert.assertNull(mTypeMap.getInstanceOf(BaseInterface.class));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantAddNull() {
        mTypeMap.addInstance(null);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantAddNullAsDeclaredType() {
        mTypeMap.addInstance(BaseInterface.class, null);
    }
    
    /**
     * 
     */
    @Test
    public void canStoreAndRetrieveByType() {
        final A a = new A("foo");
        mTypeMap.addInstance(a);

        final B b = new B("bar");
        mTypeMap.addInstance(b);
        
        final A retrievedA = mTypeMap.getInstanceOf(A.class);
        Assert.assertTrue(retrievedA instanceof A);
        Assert.assertEquals("foo", retrievedA.getData());
        
        final B retrievedB = mTypeMap.getInstanceOf(B.class);
        Assert.assertTrue(retrievedB instanceof B);
        Assert.assertEquals("bar", retrievedB.getData());
    }
    
    /**
     * 
     */
    @Test
    public void canOnlyStoreOneInstance() {
        final A a1 = new A("foo");
        mTypeMap.addInstance(a1);

        final A a2 = new A("bar");
        mTypeMap.addInstance(a2);

        final A retrievedA = mTypeMap.getInstanceOf(A.class);
        Assert.assertTrue(retrievedA instanceof A);
        Assert.assertEquals("bar", retrievedA.getData());
    }
    
    /**
     * 
     */
    @Test
    public void canOnlyStoreOneInstanceByDeclaredType() {
        final A a = new A("foo");
        mTypeMap.addInstance(BaseInterface.class, a);

        final B b = new B("bar");
        mTypeMap.addInstance(BaseInterface.class, b);
        
        final BaseInterface base = mTypeMap.getInstanceOf(BaseInterface.class);
        Assert.assertSame(b, base);
        Assert.assertEquals("bar", base.getData());
    }
}
