package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Frame;

/**
 * The default ProblemReporter that currently calls through to the
 * static methods of ProblemDialog.
 * 
 * @author matt
 *
 */
public final class DefaultProblemReporterImpl implements ProblemReporter {
    private final Frame parentFrame;

    /**
     * Create the problem reporter, given the main frame of the
     * application
     * @param frame the main frame
     */
    public DefaultProblemReporterImpl(final Frame frame) {
        parentFrame = frame;
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(final String whileDoing) {
        ProblemDialog.reportProblem(parentFrame, whileDoing);
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exception) {
        ProblemDialog.reportProblem(parentFrame, whileDoing, exception);
    }
}
