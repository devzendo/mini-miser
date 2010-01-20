package org.devzendo.minimiser.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;



/**
 * An InstanceSet allows rapid access to single instances of
 * specific types. Instances are retrieved by their class. The
 * InstanceSet is constrained by a base interface/type.
 * <p>
 * Note that this is <em>not</em> a java.util.Set!
 * 
 * @param <T> the base type that will be stored in this instance
 * set.
 *  
 * @author matt
 *
 */
public final class InstanceSet<T> {
    private static final Logger LOGGER = Logger.getLogger(InstanceSet.class);
    private final Map<Class<T>, T> mTypeMap;

    /**
     * Instantiate the TypeMap
     */
    public InstanceSet() {
        mTypeMap = Collections.synchronizedMap(new HashMap<Class<T>, T>());
    }
    
    /**
     * Get the instance of S that's present in the map, or null if
     * it is not present.
     * @param <S> the subtype of the map type T to retrieve
     * @param subType the subtype of the map type T to retrieve
     * @return an instance of type S, or null
     */
    @SuppressWarnings("unchecked")
    public <S extends T> S getInstanceOf(final Class<S> subType) {
        return (S) mTypeMap.get(subType);
    }

    /**
     * Add an instance of T (or some subtype of T). Replace any
     * existing instance of that type.
     * @param instance the instance of type T
     */
    @SuppressWarnings("unchecked")
    public void addInstance(final T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot add null to an InstanceSet");
        }
        LOGGER.debug("Adding instance of " + instance.getClass().getSimpleName() + " = " + instance);
        final Class<T> instanceClass = (Class<T>) instance.getClass();
        mTypeMap.put(instanceClass, instance);
    }

    /**
     * Add an instance of T (or some subtype of T), specifying
     * some interface that it implements as a declared type.
     * Replace any existing instance of that type.
     * @param <S> the subtype of the map type T to retrieve
     * @param klass a class/interface that the instance
     * implements that you want to use as the class by which to
     * later retrieve this instance. 
     * @param instance the instance of type T
     */
    @SuppressWarnings("unchecked")
    public <S extends T> void addInstance(final Class<S> klass, final T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot add null to an InstanceSet");
        }
        mTypeMap.put((Class<T>) klass, instance);
    }

    /**
     * Obtain the entries in the InstanceSet as a list.
     * @return a list of entries, each entry encapsulated in an
     * InstancePair.
     */
    public List<InstancePair<T>> asList() {
        final List<InstancePair<T>> list = new ArrayList<InstancePair<T>>();
        for (Entry<Class<T>, T> entry : mTypeMap.entrySet()) {
            list.add(new InstancePair<T>(entry.getKey(), entry.getValue()));
        }
        return list;
    }
}
