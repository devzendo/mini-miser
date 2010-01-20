package org.devzendo.minimiser.gui.dialog.passwordentry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import org.devzendo.minimiser.util.PasswordValidator;


/**
 * A modal dialog that prompts the user for a database password, reminding them
 * of the rules for passwords.
 * 
 * @author matt
 */
public final class PasswordEntryDialog extends JDialog implements ActionListener,
        PropertyChangeListener {
    private static final long serialVersionUID = -7735298608803797513L;

    private char[] password = new char[0];

    private final JPasswordField passwordField;
    private final JTextArea critArea;

    private final JOptionPane optionPane;
    private final JButton openButton;

    private final String btnString1 = "Open";

    private final String btnString2 = "Cancel";

    /**
     * Returns the entered password otherwise, returns the password as the user
     * entered it.
     * @return the password
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     * @param dbName the name of the database
     */
    public PasswordEntryDialog(final Frame parentFrame, final String dbName) {
        super(parentFrame, true);
        setTitle("Enter Password for '" + dbName + "'");
        passwordField = new JPasswordField(45);
        critArea = new JTextArea("");
        critArea.setEditable(false);
        critArea.setLineWrap(true);
        critArea.setForeground(Color.BLUE); // like Wizard
        // Create an array of the text and components to be displayed.
        final String msg = "The '" + dbName + "' database is encrypted.\n"
            + "The correct password must be entered before it can be opened.";
        final JTextArea msgArea = new JTextArea(msg); 
        msgArea.setEditable(false);
        final Object[] array = {msgArea, passwordField, critArea};
        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {btnString1, btnString2};
        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, options, options[0]);
        // Make this dialog display it.
        openButton = findOpenButton(optionPane);
        setContentPane(optionPane);
        // Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                /*
                 * Instead of directly closing the window, we're going to change
                 * the JOptionPane's value property.
                 */
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });
        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent ce) {
                passwordField.requestFocusInWindow();
            }
        });
        // Register an event handler that puts the text into the option pane.
        passwordField.addActionListener(this);

        passwordField.addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent e) {
            }

            public void keyReleased(final KeyEvent e) {
                validatePassword();
            }

            public void keyTyped(final KeyEvent e) {
            }
        });
        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        // Show initial password problem.
        validatePassword();
    }
    
    private JButton findOpenButton(final Container container) {
        final Component[] components = container.getComponents();
        final ArrayList<Container> containers = new ArrayList < Container >();
        for (Component component : components) {
            if (component instanceof JButton) {
                final JButton button = (JButton) component;
                if (button.getText().equals(btnString1)) {
                    return button;
                }
            } else if (component instanceof Container) {
                containers.add((Container) component);
            }
        }
        for (Container subcontainer : containers) {
            final JButton ret = findOpenButton(subcontainer);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    /**
     * This method handles events for the text field.
     * @param e the text field event
     */
    public void actionPerformed(final ActionEvent e) {
        optionPane.setValue(btnString1);
        validatePassword();
    }

    private void validatePassword() {
        final char[] passwordContents = passwordField.getPassword();
        final String criticism = PasswordValidator
                .criticisePassword(passwordContents);
        if (criticism == null) {
            critArea.setText(""); // The password meets the criteria. Try it!
            password = passwordContents;
            openButton.setEnabled(true);
        } else {
            critArea.setText(criticism + ".");
            openButton.setEnabled(false);
        }

    }
    /**
     * This method reacts to state changes in the option pane.
     * @param e the event
     */
    public void propertyChange(final PropertyChangeEvent e) {
        final String prop = e.getPropertyName();
        if (isVisible()
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
            if (btnString1.equals(value)) {
                password = passwordField.getPassword();
                // we're done; clear and dismiss the dialog
            } else { // user closed dialog or clicked cancel
                password = new char[0];
            }
            clearAndHide();
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        passwordField.setText(null);
        setVisible(false);
    }
}
