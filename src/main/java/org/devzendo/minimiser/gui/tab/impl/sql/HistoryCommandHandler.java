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

import java.util.Arrays;
import java.util.List;

import org.devzendo.minimiser.gui.console.input.HistoryObject;
import org.devzendo.minimiser.gui.console.input.InputConsole;
import org.devzendo.minimiser.gui.console.output.OutputConsole;

/**
 * Handles the history commands.
 * 
 * @author matt
 *
 */
final class HistoryCommandHandler implements CommandHandler {
    private final OutputConsole outputConsole;
    private final InputConsole inputConsole;
    
    /**
     * Initialise the HistoryCommandHandler with an input and output console
     * @param output the output console
     * @param input the input console
     */
    public HistoryCommandHandler(final OutputConsole output, final InputConsole input) {
        this.outputConsole = output;
        this.inputConsole = input;
    }
    /**
     * {@inheritDoc}
     */
    public boolean handleCommand(final String command) {
        final String[] words = command.trim().split("\\s+");
        if (words == null || words.length == 0) {
            return false;
        }
        if (words[0].equalsIgnoreCase("help") && words.length > 1 && words[1].equalsIgnoreCase("history")) {
            helpHistory();
            return true;
        }
        if (words[0].equalsIgnoreCase("history") || words[0].equalsIgnoreCase("h")) {
            return displayHistory(words);
        }
        return false;
    }

    private void helpHistory() {
        outputConsole.info("!<number>  - re-execute a numbered command from the history list");
        outputConsole.info("!<start-string> - re-execute the last command from the history that starts with <start-string>");
        outputConsole.info("<cursor-up> - show previous history command in input area");
        outputConsole.info("<cursor-down> - show next history command in input area");
        outputConsole.info("<ctrl-u> - clear input area");
    }
    
    private boolean displayHistory(final String[] words) {
        List<HistoryObject> history;
        if (words.length > 1) {
            try {
                final int num = Integer.parseInt(words[1]);
                history = inputConsole.getLastHistory(num); 
            } catch (final NumberFormatException nfe) {
                outputConsole.warn("'" + words[1] + "' is not numeric in history command");
                return true; // don't pass on to anyone else
            }
        } else {
            history = inputConsole.getHistory();
        }
        if (history != null && history.size() > 0) {
            for (final HistoryObject h : history) {
                outputConsole.info(h.getCommandIndex() + " " + h.getCommandString());
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<String> getIntroText() {
        return Arrays.asList(new String[] {
                "history - shows complete command history",
                "history <num> - shows last <num> entries of command history",
                "help history - shows help on command line history substitution"
        }); 
    }
}