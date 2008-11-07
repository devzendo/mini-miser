package uk.me.gumbley.minimiser.logging;

import org.apache.log4j.BasicConfigurator;


/**
 * Base class of all TestCases that initialises log4j.
 * @author matt
 *
 */
public abstract class LoggingTestCase {
    static {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }
}
