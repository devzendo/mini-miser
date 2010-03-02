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

import java.util.List;

import org.devzendo.commoncode.patterns.observer.Observer;


/**
 * An InputConsole receives changes to a single line of input from the user,
 * resolves any history-editing changes and delivers the final input string
 * to its listeners.
 * <p>
 * Other ideas for the future include:
 * <ul>
 * <li> It can be operated in password mode, i.e. the changes the user makes
 *      are echoed as asterisks.
 * <li> History is managed internally, although there are methods for retrieving
 *      the history for display elsewhere (i.e. in response to some 'history'
 *      command). 
 * </ul>
 * @author matt
 *
 */
public interface InputConsole {
    
    /**
     * Add an observer of InputConsoleEvents 
     * @param observer the observer.
     */
    void addInputConsoleEventListener(Observer<InputConsoleEvent> observer);
    
    /**
     * Obtain the complete command history
     * @return the complete history
     */
    List<HistoryObject> getHistory();
    
    /**
     * Obtain a partial tail history
     * @param number the number of entries to return
     * @return the entries, fewer if there aren't that many
     */
    List<HistoryObject> getLastHistory(int number);
}
