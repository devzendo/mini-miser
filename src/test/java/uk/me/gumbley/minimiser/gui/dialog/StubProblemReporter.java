package uk.me.gumbley.minimiser.gui.dialog;


/**
 * A problem reporter that can be used by unit tests.
 * 
 * @author matt
 */
public final class StubProblemReporter implements ProblemReporter {
    private String doing;
    private Exception exception;

    /**
     * {@inheritDoc}
     */
    public void reportProblem(final String whileDoing) {
        this.doing = whileDoing;
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exc) {
        doing = whileDoing;
        exception = exc;
    }

    /**
     * Obtain the text of what the app was doing when the problem
     * occurred.
     * 
     * @return the "doing" text.
     */
    public String getDoing() {
        return doing;
    }

    /**
     * Obtain the exception that occurred when the app was doing
     * something.
     * 
     * @return the exception, or null if the problem wasn't caused by
     * an exception.
     */
    public Exception getException() {
        return exception;
    }
}
