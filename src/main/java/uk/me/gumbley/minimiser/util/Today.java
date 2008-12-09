package uk.me.gumbley.minimiser.util;

/**
 * A Today gives today's date, in UK DD/MM/YYYY form, suitable for text
 * comparison against a stored date to see if they are the same. This is not
 * intended to be user-displayable, hence no attempt at making it Localisable.
 * @author matt
 *
 */
public interface Today {
    
    /**
     * @return the UK format date strin for today
     */
    String getUKDateString();
}