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
    
    /**
     * If the handler has any introduction text, to display on startup,
     * return its List of Strings here.
     * @return A List of intro Strings
     */
    List<String> getIntroText();
}
