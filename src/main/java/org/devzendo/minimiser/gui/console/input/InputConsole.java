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
