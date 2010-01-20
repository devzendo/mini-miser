package org.devzendo.minimiser.gui.console.output;

/**
 * An OutputConsole is essentially a wrapper around a Log4j logger.
 * 
 * @author matt
 *
 */
public interface OutputConsole {
    /**
     * Output some debugging object. In the context of an OutputConsole this
     * might just be echoing the lines entered via an InputConsole.
     * @param obj the object to debug
     */
    void debug(Object obj);
    
    /**
     * Output some normal, expected output.
     * @param obj the object to output
     */
    void info(Object obj);
    
    /**
     * Output a warning about something
     * @param obj the thing to warn about.
     */
    void warn(Object obj);
    
    /**
     * Output an error about something
     * @param obj the thing to raise as an error.
     */
    void error(Object obj);
}
