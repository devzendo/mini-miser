/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
