/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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