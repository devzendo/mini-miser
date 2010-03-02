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
 * A line of input has been read, but the console has a problem with it
 * and needs to report it to the user. Here's the line and what's wrong
 * with it.
 * 
 * @author matt
 *
 */
public final class InputConsoleEventError extends InputConsoleEvent {

    private final String[] errorLines;

    /**
     * Construct an event containing a line of input and any errors.
     * @param line the line of text
     * @param errors the errors in this.
     */
    public InputConsoleEventError(final String line, final String ... errors) {
        super(line);
        errorLines = errors;
    }

    /**
     * Obtain the errors in the console input.
     * @return the error lines.
     */
    public String[] getErrorLines() {
        return errorLines;
    }
}
