package org.devzendo.minimiser.util;

/**
 * Supporting classes for tests of InstancePair and InstanceSet.
 * 
 * @author matt
 *
 */
public final class InstanceClasses {

    /**
     *
     */
    public interface BaseInterface {
        /**
         * @return the data
         */
        String getData();
    }
    
    /**
     * 
     */
    public abstract static class AbstractBase implements BaseInterface {
        private final String mData;
    
        /**
         * @param data the data
         */
        public AbstractBase(final String data) {
            mData = data;
        }
        /**
         * {@inheritDoc}
         */
        public final String getData() {
            return mData;
        }
    }
    
    /**
     *
     */
    public static final class A extends AbstractBase {
        /**
         * @param data the data
         */
        public A(final String data) {
            super(data);
        }
    }
    
    /**
     *
     */
    public static final class B extends AbstractBase {
        /**
         * @param data the data
         */
        public B(final String data) {
            super(data);
        }
    }
}