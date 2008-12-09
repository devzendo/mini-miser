package uk.me.gumbley.minimiser.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A Today gives today's date, in UK DD/MM/YYYY form, suitable for text
 * comparison against a stored date to see if they are the same. This is not
 * intended to be user-displayable, hence no attempt at making it Localisable.
 * Also used by unit tests to inject a specific value for today.
 * @author matt
 *
 */
public final class DefaultToday implements Today {
    private final String fixDate;
    private final SimpleDateFormat simpleDateFormat;
    
    /**
     * Create a Today that will return today's date when called.
     */
    public DefaultToday() {
        fixDate = null;
        simpleDateFormat = init();
    }

//    /**
//     * Create a Today that will return a fixed 
//     * @param fixedDateString
//     */
//    public Today(final String fixedDateString) {
//        fixDate = fixedDateString;
//        simpleDateFormat = init();
//    }

    private SimpleDateFormat init() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }
    
    /**
     * {@inheritDoc}
     */
    
    public String getUKDateString() {
        if (fixDate == null) {
            return fixDate;
        }
        return simpleDateFormat.format(new Date());
    }
}
