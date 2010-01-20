package org.devzendo.minimiser.gui.tab.impl.sql;

import java.util.List;

import org.devzendo.minimiser.gui.console.output.OutputConsole;

/**
 * Processes SQL and other commands.
 * 
 * @author matt
 */
public final class CommandProcessor {
    private final OutputConsole outputConsole;
    private final List<CommandHandler> commandHandlers;

    /**
     * Create a CommandProcessor that uses input and output consoles and
     * operates on a database.
     * 
     * @param output
     *        the output console
     * @param cmdHandlers
     *        the list of command handlers
     */
    public CommandProcessor(final OutputConsole output, final List<CommandHandler> cmdHandlers) {
        this.outputConsole = output;
        this.commandHandlers = cmdHandlers;
    }
    
    /**
     * Process a command by passing it down the chain of CommandHandlers until
     * one processes it, or report an error.
     * @param command the command string
     */
    public void processCommand(final String command) {
        for (final CommandHandler commandHandler : commandHandlers) {
            if (commandHandler.handleCommand(command)) {
                return;
            }
        }
        outputConsole.warn("The command '" + command + "' was not understood.");
    }
}
