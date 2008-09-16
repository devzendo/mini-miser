package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Frame;
import org.apache.log4j.Logger;

/**
 * The default ProblemReporter that currently calls through to the
 * static methods of ProblemDialog.
 * 
 * @author matt
 *
 */
public final class DefaultProblemReporterImpl implements ProblemReporter {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultProblemReporterImpl.class);
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
        LOGGER.error("Error occurred " + whileDoing);
        ProblemDialog.reportProblem(parentFrame, whileDoing);
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exception) {
        LOGGER.error("Error occurred " + whileDoing, exception);
        ProblemDialog.reportProblem(parentFrame, whileDoing, exception);
    }
}
