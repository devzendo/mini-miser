package uk.me.gumbley.minimiser.gui.console.output;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
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
    private StringBuilder updateText;
    
    /**
     * Construct a text area-based OutputConsole 
     */
    public TextAreaOutputConsole() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize() - 2));
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);

        updateText = new StringBuilder();
        final Thread updateThread = new Thread(new Runnable() {

            public void run() {
                while (Thread.currentThread().isAlive()) {
                    synchronized (updateText) {
                        if (updateText.length() > 0) {
                            final String out = updateText.toString();
                            updateText.delete(0, updateText.length() - 1);
                            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                                public void run() {
                                    textArea.append(out);
                                    textAreaContentLength += out.length();
                                    textArea.setCaretPosition(textAreaContentLength);
                                }
                            });
                        }
                        ThreadUtils.waitNoInterruption(100);
                    }
                }
                
            }
            
        });
        updateThread.setDaemon(true);
        updateThread.setName("Console updater");
        updateThread.start();
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
        synchronized (updateText) {
            updateText.append(text);
            if (!text.endsWith("\n")) {
                updateText.append("\n");
            }
        }
//        final StringBuilder sb = new StringBuilder(text);
//        final String writeString = sb.toString();
//        GUIUtils.invokeLaterOnEventThread(new Runnable() {
//            public void run() {
//                textArea.append(writeString);
//                textAreaContentLength += writeString.length();
//                textArea.setCaretPosition(textAreaContentLength);
//            }
//        });
    }
}
