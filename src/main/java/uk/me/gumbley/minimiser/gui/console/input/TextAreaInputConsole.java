package uk.me.gumbley.minimiser.gui.console.input;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * An InputConsole that uses a JTextArea to input data.
 * 
 * @author matt
 *
 */
public final class TextAreaInputConsole implements InputConsole {
    private static final Logger LOGGER = Logger
            .getLogger(TextAreaInputConsole.class);
    private ObserverList<InputConsoleEvent> observerList;
    private JTextArea textArea;
    private History history;
    private int historyIndex;
    private boolean firstFocus;
    
    /**
     * Create a TextAreaInputConsole 
     */
    public TextAreaInputConsole() {
        history = new History();
        historyIndex = 1;
       
        firstFocus = true;
        
        observerList = new ObserverList<InputConsoleEvent>();
        textArea = new JTextArea("<enter your SQL here>");
        textArea.setRows(3); // pointless, but worth a try
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize() - 2));
        textArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        textArea.addFocusListener(new FocusAdapter() {
            public void focusGained(final FocusEvent e) {
                if (firstFocus) {
                    firstFocus = false;
                    clearTextArea();
                }
                
            }
        });

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            new EnterAction());

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK),
            new CtrlUAction());

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
            new UpAction());

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
            new DownAction());
    }
    
    /**
     * Get the text area used by this input console
     * @return the JTextArea
     */
    public JTextArea getTextArea() {
        return textArea;
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

    private void setTextArea(String commandString) {
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
    
    @SuppressWarnings("serial")
    private final class EnterAction extends AbstractAction {
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

    @SuppressWarnings("serial")
    private final class CtrlUAction extends AbstractAction {
        public void actionPerformed(final ActionEvent e) {
            LOGGER.debug("Ctrl-U");
            clearTextArea();
        }
    }

    @SuppressWarnings("serial")
    private final class UpAction extends AbstractAction {
        public void actionPerformed(final ActionEvent e) {
            if (historyIndex == 1) {
                LOGGER.debug("No previous history past this point");
                return;
            }
            LOGGER.debug("Previous history");
            setTextArea(history.getNumberedEntry(setHistoryIndex(Math.max(1, historyIndex - 1))).getCommandString());
        }
    }

    @SuppressWarnings("serial")
    private final class DownAction extends AbstractAction {
        public void actionPerformed(final ActionEvent e) {
            LOGGER.debug("Next history");
            setTextArea(history.getNumberedEntry(setHistoryIndex(Math.min(history.size(), historyIndex + 1))).getCommandString());
        }
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
     * TODO this code is a bit smelly, relying on the order of observers
     * being called and ignoring history transformation failure.
     */
    public int getNextHistoryNumber() {
        return history.size() + 1;
    }


}
