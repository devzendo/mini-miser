package uk.me.gumbley.minimiser.gui.console.input;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * A text area with a slightly smaller font, and a bevelled border.
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class ConsoleTextArea extends JTextArea {
    /**
     * Create a console text area with the given title
     * @param title the title
     */
    public ConsoleTextArea(final String title) {
        super(title);
        setFont(new Font("Monospaced", Font.PLAIN, getFont().getSize() - 2));
        setBorder(BorderFactory.createLoweredBevelBorder());
    }
}
