package uk.me.gumbley.minimiser.gui.tab.impl.sql;

/**
 * A CommandHandler handles commands - that is, there will be several
 * CommandHandlers in the system that handle commands for different subsystems.
 * Incoming commands will be passed to each CommandHandler in turn, to see if
 * they can handle it. This chain stops when one of them indicates that they
 * can process the command.
 *  
 * @author matt
 *
 */
public interface CommandHandler {
    /**
     * Attempt to handle a command
     * @param command the command string
     * @return true iff handled, false if not.
     */
    boolean handleCommand(String command);
}
