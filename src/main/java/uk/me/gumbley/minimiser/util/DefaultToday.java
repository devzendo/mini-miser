package uk.me.gumbley.minimiser.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A Today gives today's date, in UK DD/MM/YYYY form, suitable for
 * text comparison against a stored date to see if they are the
 * same. This is not intended to be user-displayable, hence no
 * attempt at making it Localisable.
 * 
 * @author matt
 *
 */
public final class DefaultToday implements Today {
    private final SimpleDateFormat simpleDateFormat;
    
    /**
     * Create a Today that will return today's date when called.
     */
    public DefaultToday() {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }
    
    /**
     * {@inheritDoc}
     */
    public String getUKDateString() {
        return simpleDateFormat.format(new Date());
    }
}
