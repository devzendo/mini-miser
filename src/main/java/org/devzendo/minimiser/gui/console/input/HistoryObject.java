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

package org.devzendo.minimiser.gui.console.input;

/**
 * Encapsulation of a command string and its index into the history list.
 * @author matt
 *
 */
public final class HistoryObject {
    private final int commandIndex;
    private final String commandString;

    /**
     * Create a historic command
     * @param index the index in the command history
     * @param command the command string
     */
    public HistoryObject(final int index, final String command) {
        this.commandIndex = index;
        this.commandString = command;
    }

    /**
     * @return the index of this command into the command history
     */
    public int getCommandIndex() {
        return commandIndex;
    }

    /**
     * @return the command string
     */
    public String getCommandString() {
        return commandString;
    }
}
