package uk.me.gumbley.minimiser.logging;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


/**
 * Base class of all TestCases that initialises log4j.
 * @author matt
 *
 */
public abstract class LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(LoggingTestCase.class);
    static {
        BasicConfigurator.configure();
        LOGGER.info("LoggingTestCase::static");
    }
}
