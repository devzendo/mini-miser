package org.devzendo.minimiser.updatechecker;

/**
 * Thrown from the ChangeLogSectionParser upon a failure to understand part of
 * the change log.
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class ParseException extends Exception {

    /**
     * Construct
     * @param warning why?
     * @param cause due to what?
     */
    public ParseException(final String warning, final Exception cause) {
        super(warning, cause);
    }
}
