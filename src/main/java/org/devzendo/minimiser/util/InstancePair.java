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
 * An InstancePair holds a class and an instance of that class.
 * The class could be an interface class. The InstancePair is
 * used to associate an object with a particular interface that it
 * implements.
 * 
 * @author matt
 *
 * @param <T> the base type that will be stored in this instance
 * pair.
 */
public final class InstancePair<T> {
    private final Class<T> mInstanceClass;
    private final T mInstance;

    /**
     * Construct an instance pair given a class and an instance
     * of it
     * @param <S> the subtype of the type T
     * @param klass a class/interface that the instance
     * implements 
     * @param instance the instance of type T
     */
    @SuppressWarnings("unchecked")
    public <S extends T> InstancePair(final Class<S> klass, final T instance) {
        if (klass == null) {
            throw new IllegalArgumentException("Class of an InstancePair cannot be null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Instance of an InstancePair cannot be null");
        }
        mInstanceClass = (Class<T>) klass;
        mInstance = instance;
    }

    /**
     * @param <S> the subtype of the type T
     * @return class associated with this instance
     */
    @SuppressWarnings("unchecked")
    public <S extends T> Class<S> getClassOfInstance() {
        return (Class<S>) mInstanceClass;
    }

    /**
     * @return the instance
     */
    public T getInstance() {
        return mInstance;
    }
}
