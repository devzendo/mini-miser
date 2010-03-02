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

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

/**
 * A line of input has been entered, and here it is.
 * @author matt
 *
 */
public class InputConsoleEvent implements ObservableEvent {
    private final String inputLine;

    /**
     * Construct an event containing the input of a single line of text.
     * @param line th eline of text.
     */
    public InputConsoleEvent(final String line) {
        this.inputLine = line;
    }

    /**
     * Obtain the line of input text.
     * @return th eline
     */
    public final String getInputLine() {
        return inputLine;
    }
}
