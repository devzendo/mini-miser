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

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;


/**
 * An InputConsole that uses a JTextArea to input data, with input map handlers
 * for common history functions:
 * <ul>
 * <li> next / previous history
 * <li> clear line
 * <li> enter to submit command
 * </ul>
 * 
 * @author matt
 *
 */
public final class TextAreaInputConsole implements InputConsole {
    private static final Logger LOGGER = Logger
            .getLogger(TextAreaInputConsole.class);
    private ObserverList<InputConsoleEvent> observerList;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private History history;
    private int historyIndex;
    
    /**
     * Create a TextAreaInputConsole
     * @param initialText the text to display in the console initially 
     */
    @SuppressWarnings("serial")
    public TextAreaInputConsole(final String initialText) {
        history = new History();
        historyIndex = 1;
       
        observerList = new ObserverList<InputConsoleEvent>();
        textArea = new ConsoleTextArea(initialText);

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    LOGGER.debug("Enter");
                    final String line = textArea.getText();
                    final String transformedLine = processLine(line);
                    if (transformedLine != null) {
                        clearTextArea();
                        history.add(transformedLine);
                        setHistoryIndex(history.size() + 1);
                    }
                }
            }
        );

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK),
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    LOGGER.debug("Ctrl-U");
                    clearTextArea();
                }
            }
        );

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    if (historyIndex == 1) {
                        LOGGER.debug("No previous history past this point");
                        return;
                    }
                    LOGGER.debug("Previous history");
                    setTextArea(history.getNumberedEntry(setHistoryIndex(Math.max(1, historyIndex - 1))).getCommandString());
                }
            }
        );

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    LOGGER.debug("Next history");
                    setTextArea(history.getNumberedEntry(setHistoryIndex(Math.min(history.size(), historyIndex + 1))).getCommandString());
                }
            }
        );
        
        scrollPane = new JScrollPane(textArea);
    }
    
    /**
     * Get the text area used by this input console
     * @return the JTextArea in a JScrollPane
     */
    public JComponent getTextArea() {
        return scrollPane;
    }

    /**
     * {@inheritDoc}
     */
    public void addInputConsoleEventListener(
            final Observer<InputConsoleEvent> observer) {
        observerList.addObserver(observer);
    }

    private String processLine(final String inputLine) {
        try {
            final String historyTransformedLine = history.transform(inputLine);
            LOGGER.debug("Processing line '" + inputLine + "'='" + historyTransformedLine);
            observerList.eventOccurred(new InputConsoleEvent(historyTransformedLine));
            return historyTransformedLine;
        } catch (final HistoryTransformationException e) {
            LOGGER.warn("Line '" + inputLine + "' failed history transformation: " + e.getMessage());
            observerList.eventOccurred(new InputConsoleEventError(inputLine, new String[] {e.getMessage()}));
            return null;
        }
    }

    private void setTextArea(final String commandString) {
        textArea.setText(commandString);
    }

    private void clearTextArea() {
        textArea.setText("");
    }

    private int setHistoryIndex(final int newIndex) {
        LOGGER.debug("historyIndex is now " + newIndex);
        historyIndex = newIndex;
        return historyIndex;
    }

    /**
     * {@inheritDoc}
     */
    public List<HistoryObject> getHistory() {
        return history.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public List<HistoryObject> getLastHistory(final int number) {
        return history.getLast(number);
    }

    /**
     * @return the next history number. Can be called by InputConsoleEvent
     * observers to determine the history number that a command will be stored
     * under, if it succeeds in being transformed as a history command (or
     * passed through if it isn't a history command)
     * TODO: this code is a bit smelly, relying on the order of observers
     * being called and ignoring history transformation failure.
     */
    public int getNextHistoryNumber() {
        return history.size() + 1;
    }
}
