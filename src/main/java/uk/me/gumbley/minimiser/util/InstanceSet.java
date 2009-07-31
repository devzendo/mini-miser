package uk.me.gumbley.minimiser.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



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
        final Class<T> instanceClass = (Class<T>) instance.getClass();
        mTypeMap.put(instanceClass, instance);
    }
}
