package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.StyleConstants.FontConstants;
import uk.me.gumbley.minimiser.common.AppName;

/**
 * A modal dialog that displays the detail of a serious problem.
 * 
 * @author matt
 */
public final class SeriousProblemDialog extends JDialog implements 
        PropertyChangeListener {

    private JOptionPane optionPane;

    private String btnString1 = "Continue";

    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A serious problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the serious
     * problem isn't due to an exception.
     */
    public SeriousProblemDialog(final Frame parentFrame, final String whileDoing, final Exception exception) {
        super(parentFrame, true);
        setTitle("A serious problem has occurred");
        
        // Create an array of the text and components to be displayed.
        final StringBuilder stackTrace = new StringBuilder();
        final StringBuilder msg = new StringBuilder();
        msg.append("A serious problem has occurred ");
        msg.append(whileDoing);
        msg.append(".\n\n");
        msg.append("Please copy the following details of the problem into an email\n");
        msg.append("and send it to " + getEmailAddress() + ".\n\n");
        if (exception != null) {
            final String message = exception.getMessage();
            if (message != null && message.length() > 0) {
                msg.append(message);
                msg.append("\n");
            }
            
            final StackTraceElement[] stackTraceArray = exception.getStackTrace();
            for (StackTraceElement element : stackTraceArray) {
                stackTrace.append(element.toString());
                stackTrace.append("\n");
            }
        }
        final JTextArea msgArea = new JTextArea(msg.toString()); 
        msgArea.setEditable(false);

        final JTextArea stackTraceArea = new JTextArea(stackTrace.toString());
        stackTraceArea.setEditable(true); // TODO needs to be editable for cut'n'paste, but not changeable
        stackTraceArea.setLineWrap(true);
        stackTraceArea.setRows(8);
        stackTraceArea.setColumns(50);
        stackTraceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        final Object[] array = {msgArea, new JScrollPane(stackTraceArea)};
        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {btnString1};
        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.ERROR_MESSAGE,
                JOptionPane.OK_OPTION, null, options, options[0]);
        // Make this dialog display it.
        setContentPane(optionPane);
        // Handle window closing correctly.
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(final WindowEvent we) {
//                /*
//                 * Instead of directly closing the window, we're going to change
//                 * the JOptionPane's value property.
//                 */
//                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
//            }
//        });
        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(final ComponentEvent ce) {
                stackTraceArea.requestFocusInWindow();
            }
        });
        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    private String getEmailAddress() {
        final StringBuilder mailaddr = new StringBuilder();
        mailaddr.append(AppName.getAppName());
        mailaddr.append("-developers@gumbley.me.uk");
        return mailaddr.toString().toLowerCase();
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
            clearAndHide();
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }
}
