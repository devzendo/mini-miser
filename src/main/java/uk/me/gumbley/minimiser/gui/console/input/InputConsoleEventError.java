package uk.me.gumbley.minimiser.gui.console.input;

/**
 * A line of input has been read, but the console has a problem with it
 * and needs to report it to the user. Here's the line and what's wrong
 * with it.
 * 
 * @author matt
 *
 */
public final class InputConsoleEventError extends InputConsoleEvent {

    private final String[] errorLines;

    /**
     * Construct an event containing a line of input and any errors.
     * @param line th eline of text
     * @param errors the errors in this.
     */
    public InputConsoleEventError(final String line, final String ... errors) {
        super(line);
        errorLines = errors;
    }

    /**
     * Obtain the errors in the console input.
     * @return the error lines.
     */
    public String[] getErrorLines() {
        return errorLines;
    }
}
