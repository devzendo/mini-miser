package uk.me.gumbley.minimiser.gui.console.input;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * A line of input has been entered, and here it is.
 * @author matt
 *
 */
public class InputConsoleEvent implements ObservableEvent {
    private final String inputLine;

    /**
     * Construct an event containing the input of a single line of text.
     * @param line th eline of text.
     */
    public InputConsoleEvent(final String line) {
        this.inputLine = line;
    }

    /**
     * Obtain the line of input text.
     * @return th eline
     */
    public final String getInputLine() {
        return inputLine;
    }
}
