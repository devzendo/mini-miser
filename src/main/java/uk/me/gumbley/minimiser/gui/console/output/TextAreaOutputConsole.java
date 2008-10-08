package uk.me.gumbley.minimiser.gui.console.output;

import javax.swing.JTextArea;

/**
 * An OutputConsole that uses a TextArea to logs its information.
 * 
 * @author matt
 *
 */
public final class TextAreaOutputConsole implements OutputConsole {
    private JTextArea textArea;
    
    /**
     * Construct a text area-based OutputConsole 
     */
    public TextAreaOutputConsole() {
        textArea = new JTextArea();
    }
    
    /**
     * Obtain the text area used by this OutputConsole
     * @return the JTextArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object obj) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object obj) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object obj) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object obj) {
        // TODO Auto-generated method stub
    }
}
