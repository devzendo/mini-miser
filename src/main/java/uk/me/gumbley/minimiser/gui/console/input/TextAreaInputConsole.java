package uk.me.gumbley.minimiser.gui.console.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JTextArea;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * An InputConsole that uses a JTextArea to input data.
 * 
 * @author matt
 *
 */
public final class TextAreaInputConsole implements InputConsole {
    private ObserverList<InputConsoleEvent> observerList;
    private JTextArea textArea;
    private History history;
    
    /**
     * Create a TextAreaInputConsole 
     */
    public TextAreaInputConsole() {
        history = new History();
        observerList = new ObserverList<InputConsoleEvent>();
        textArea = new JTextArea();
        textArea.addKeyListener(new InputConsoleKeyListener());
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

    private void processLine(final String inputLine) {
        // TODO handle history lookups
        observerList.eventOccurred(new InputConsoleEvent(inputLine));
    }
    
    private void clearTextArea() {
        textArea.setText("");
    }

    private final class InputConsoleKeyListener extends KeyAdapter {
        /**
         * {@inheritDoc}
         */
        public void keyTyped(final KeyEvent e) {
            switch (e.getID()) {
                case KeyEvent.VK_ENTER:
                    final String line = textArea.getText();
                    processLine(line);
                    clearTextArea();
                    break;
                case KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_U:
                    clearTextArea();
                    break;
                default:
                    break;
            }
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


}
