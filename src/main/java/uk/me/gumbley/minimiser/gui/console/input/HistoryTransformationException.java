package uk.me.gumbley.minimiser.gui.console.input;

/**
 * A line cannot be transformed by the History handler.
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class HistoryTransformationException extends Exception {

    /**
     * History transformation failed for some reason
     * @param message why?
     */
    public HistoryTransformationException(final String message) {
        super(message);
    }
}
