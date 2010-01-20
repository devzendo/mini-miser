package org.devzendo.minimiser.util;

import java.util.List;

import org.devzendo.minimiser.util.InstanceClasses.A;
import org.devzendo.minimiser.util.InstanceClasses.B;
import org.devzendo.minimiser.util.InstanceClasses.BaseInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
    
    /**
     * 
     */
    @Test
    public void canGetAllInstancesAsList() {
        final A a = new A("foo");
        mTypeMap.addInstance(a);
        final B b = new B("bar");
        mTypeMap.addInstance(b);
        
        final List<InstancePair<BaseInterface>> list = mTypeMap.asList();
        
        Assert.assertEquals(2, list.size());
        boolean aFound = false;
        boolean bFound = false;
        for (InstancePair<BaseInterface> instancePair : list) {
            if (instancePair.getInstance().getData().equals("foo")) {
                aFound = true;
                Assert.assertSame(A.class, instancePair.getClassOfInstance());
            }
            if (instancePair.getInstance().getData().equals("bar")) {
                bFound = true;
                Assert.assertSame(B.class, instancePair.getClassOfInstance());
            }
        }
        Assert.assertTrue(aFound);
        Assert.assertTrue(bFound);
    }
}
