package uk.me.gumbley.minimiser.gui.dialog.problem;

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
    /**
     * Create the problem reporter
     */
    public DefaultProblemReporterImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(final String whileDoing) {
        LOGGER.error("Error occurred " + whileDoing);
        ProblemDialogHelper.reportProblem(whileDoing);
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exception) {
        LOGGER.error("Error occurred " + whileDoing, exception);
        ProblemDialogHelper.reportProblem(whileDoing, exception);
    }
}
