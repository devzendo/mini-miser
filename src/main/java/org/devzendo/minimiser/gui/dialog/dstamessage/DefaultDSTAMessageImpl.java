package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * A DSTA Message that presents its content, and checkbox, using a JOptionPane.
 * 
 * @author matt
 *
 */
public final class DefaultDSTAMessageImpl extends AbstractDSTAMessage implements PropertyChangeListener {

    private static final Logger LOGGER = Logger
            .getLogger(DefaultDSTAMessageImpl.class);
    
    private static final String OK = "OK";
    private final Frame frame;
    private JDialog dialog;
    private JOptionPane optionPane;

    private JCheckBox dstaCheckBox;

    /**
     * @param factory the message factory that generated this message
     * @param mainFrame the application's main frame
     * @param msgId the key for this message
     * @param string the text content
     */
    public DefaultDSTAMessageImpl(final DSTAMessageFactory factory, final Frame mainFrame, final DSTAMessageId msgId, final String string) {
        super(factory, msgId, string);
        frame = mainFrame;
        initComponents();
    }


    /**
     * @param factory the message factory that generated this message
     * @param mainFrame the application's main frame
     * @param msgId the key for this message
     * @param content the graphical content
     */
    public DefaultDSTAMessageImpl(final DSTAMessageFactory factory, final Frame mainFrame, final DSTAMessageId msgId, final Component content) {
        super(factory, msgId, content);
        this.frame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        dialog = new JDialog(frame, true);
        dialog.setTitle("Message");
        dstaCheckBox = new JCheckBox("Don't show this message again");
        Component mainComponent = null;
        if (getMessageContent() != null) {
            mainComponent = getMessageContent();
        } else {
            mainComponent = new JTextArea(getMessageText());
            final JTextArea textArea = (JTextArea) mainComponent;
            textArea.setEditable(false);
        }
        
        final Object[] array = {mainComponent, dstaCheckBox};
        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {OK};
        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.OK_OPTION, null, options, options[0]);
        dialog.setContentPane(optionPane);
        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        // Handle window closing correctly.
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent we) {
                /*
                 * Instead of directly closing the window, we're going to change
                 * the JOptionPane's value property.
                 */
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);        
    }

    /**
     * This method reacts to state changes in the option pane.
     * @param e the event
     */
    public void propertyChange(final PropertyChangeEvent e) {
        final String prop = e.getPropertyName();
        if (dialog.isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
                        .equals(prop))) {
            final Object value = optionPane.getValue();
            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                // ignore reset
                return;
            }
            // Reset the JOptionPane's value.
            // If you don't do this, then if the user
            // presses the same button next time, no
            // property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if (OK.equals(value)) {
                LOGGER.debug("Testing DSTA checkbox");
                if (dstaCheckBox.isSelected()) {
                    LOGGER.debug("User has indicated this message is to be prevented");
                    setDontShowThisAgain();
                } else {
                    LOGGER.debug("User has indicated this message is to be retained");
                }
                // we're done; clear and dismiss the dialog
            } else { // user closed dialog or clicked cancel
                LOGGER.debug("User closed dialog");
            }
            clearAndHide();
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        dialog.setVisible(false);
        dialog.dispose();
    }
}
