package uk.me.gumbley.minimiser.gui.dialog;


/**
 * A mechanism for reporting serious problems to the user.
 * In the real app, this is a dialog; in tests, a stub.
 * 
 * @author matt
 *
 */
public interface ProblemReporter {
    
    /**
     * Report a problem that has no exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     */
    void reportProblem(final String whileDoing);
    
    /**
     * Report a problem that has an exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     */
    void reportProblem(final String whileDoing, final Exception exception);
}
