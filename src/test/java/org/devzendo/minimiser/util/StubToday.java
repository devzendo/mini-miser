package org.devzendo.minimiser.util;



/**
 * A Today for use by unit tests to inject a specific value for today.
 * @author matt
 *
 */
public final class StubToday implements Today {
    private final String fixDate;
    
    /**
     * Create a Today that will return a fixed date 
     * @param fixedDateString a fixed date
     */
    public StubToday(final String fixedDateString) {
        fixDate = fixedDateString;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getUKDateString() {
        return fixDate;
    }
}
