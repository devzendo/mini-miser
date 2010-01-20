package org.devzendo.minimiser.gui.console.input;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * A text area with a slightly smaller font, and a bevelled border.
 * Also shows an intro message that clears on getting focus.
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class ConsoleTextArea extends JTextArea {
    private boolean firstFocus;

    /**
     * Create a console text area with the given title
     * @param title the title
     */
    public ConsoleTextArea(final String title) {
        super(title);
        setFont(new Font("Monospaced", Font.PLAIN, getFont().getSize()));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setRows(3);

        firstFocus = true;

        addFocusListener(new FocusAdapter() {
            public void focusGained(final FocusEvent e) {
                if (firstFocus) {
                    firstFocus = false;
                    setText("");
                }
                
            }
        });
    }
}
