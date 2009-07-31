package uk.me.gumbley.minimiser.util;

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

    private interface BaseInterface {
        // marker interface
    }
    
    private abstract class AbstractBase implements BaseInterface {
        private final String mData;

        /**
         * @return the data
         */
        public AbstractBase(final String data) {
            mData = data;
        }
        public String getData() {
            return mData;
        }
    }

    /**
     *
     */
    private final class A extends AbstractBase {
        /**
         * @param data
         */
        public A(final String data) {
            super(data);
        }
    }
    
    /**
     *
     */
    private final class B extends AbstractBase {
        /**
         * @param data
         */
        public B(final String data) {
            super(data);
        }
    }
    
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
}
