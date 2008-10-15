package uk.me.gumbley.minimiser.gui.console.output;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import uk.me.gumbley.commoncode.gui.GUIUtils;

/**
 * An OutputConsole that uses a TextArea to logs its information.
 * 
 * @author matt
 *
 */
public final class TextAreaOutputConsole implements OutputConsole {
    private int textAreaContentLength = 0;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    
    /**
     * Construct a text area-based OutputConsole 
     */
    public TextAreaOutputConsole() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize() - 2));
        textArea.setEditable(false);
        
        scrollPane = new JScrollPane(textArea);
    }
    
    /**
     * Obtain the text area used by this OutputConsole
     * @return the JTextArea in a JScrollPane
     */
    public JComponent getTextArea() {
        return scrollPane;
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }
    
    private void appendText(final String text) {
        final StringBuilder sb = new StringBuilder(text);
        if (!text.endsWith("\n")) {
            sb.append("\n");
        }
        final String writeString = sb.toString();
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                textArea.append(writeString);
                textAreaContentLength += writeString.length();
                textArea.setCaretPosition(textAreaContentLength);
            }
        });
    }
}
