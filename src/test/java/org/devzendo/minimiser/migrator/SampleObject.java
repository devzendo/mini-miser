package org.devzendo.minimiser.migrator;

/**
 * An entity that this plugin creates upon migration; used
 * with SimpleJdbcTemplate, a la Version.
 * @author matt
 *
 */
public final class SampleObject {
    private final String mName;
    private final int mQuantity;

    /**
     * Construct the sample object bean
     * @param name the name
     * @param quantity the quantity
     */
    public SampleObject(final String name, final int quantity) {
        mName = name;
        mQuantity = quantity;
    }
    
    /**
     * @return the quantity
     */
    public int getQuantity() {
        return mQuantity;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }
}