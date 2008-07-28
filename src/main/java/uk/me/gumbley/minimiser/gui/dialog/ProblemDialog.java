package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * A modal dialog that displays the detail of a problem.
 * 
 * @author matt
 */
public final class ProblemDialog extends JDialog implements 
        PropertyChangeListener {
    private static final long serialVersionUID = -5625177120250936170L;
    private JOptionPane optionPane;
    private String btnString1 = "Continue";

    // WOZERE need to test possible combinations of this again manually
    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     * @param caller the calling Thread
     */
    public ProblemDialog(final Frame parentFrame, final String whileDoing, final Exception exception, final Thread caller) {
        super(parentFrame, true);
        setTitle("A problem has occurred");
        
        // Create an array of the text and components to be displayed.
        final StringBuilder detail = new StringBuilder();
        final StringBuilder msg = new StringBuilder();
        msg.append("A problem has occurred ");
        msg.append(whileDoing);
        if (!whileDoing.endsWith(".")) {
            msg.append(".");
        }
        msg.append("\n\n");

        final String problem = formExceptionMessageDescription(exception);
        msg.append(problem.toString());

        detail.append(AppName.getAppName());
        detail.append(" v");
        detail.append(AppVersion.getVersion());
        detail.append("\n\n");
        detail.append("Calling thread:\n  ");
        detail.append(caller);
        if (exception != null) {
            detail.append("\n\n");
            detail.append(exceptionDetails(exception));
        }
        msg.append("Please copy all of the following details of the problem into an email\n");
        msg.append("and send it to " + getEmailAddress() + ".\n");

        final JTextArea msgArea = new JTextArea(msg.toString()); 
        msgArea.setEditable(false);
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        msgArea.setColumns(70);

        final JTextArea detailArea = new JTextArea(detail.toString());
        detailArea.setEditable(false);
        detailArea.setBackground(Color.WHITE);
        detailArea.setWrapStyleWord(true);
        detailArea.setLineWrap(true);
        detailArea.setRows(16);
        detailArea.setColumns(70);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        final Object[] array = {msgArea, new JScrollPane(detailArea)};
        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {btnString1};
        
        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.ERROR_MESSAGE,
                JOptionPane.OK_OPTION, null, options, options[0]);

        // Make this dialog display it.
        setContentPane(optionPane);

        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(final ComponentEvent ce) {
                detailArea.requestFocusInWindow();
            }
        });
        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    private String exceptionDetails(final Exception exception) {
        final StringBuilder detail = new StringBuilder();
        detail.append("Exception:\n  ");
        detail.append(exception.getClass().getName());
        detail.append("\n\n");
        detail.append("Full exception message:\n  ");
        detail.append(exception.getMessage());
        detail.append("\n\n");
        detail.append("Problem occurred at:\n");
        final StackTraceElement[] stackTraceArray = exception.getStackTrace();
        for (StackTraceElement element : stackTraceArray) {
            detail.append("  ");
            detail.append(element.toString());
            detail.append("\n");
        }
        return detail.toString();
    }

    private String formExceptionMessageDescription(final Exception exception) {
        final StringBuilder problem = new StringBuilder();
        final String exceptionMessage = exception == null ? "" : exception.getMessage() == null ? "" : exception.getMessage();
        if (exceptionMessage.length() > 0) {
            problem.append("Problem: '");
            problem.append(getFirstSentence(exceptionMessage));
            if (!exceptionMessage.endsWith(".")) {
                problem.append(".");
            }
            problem.append("'\n\n");
        }
        return problem.toString();
    }

    private String getFirstSentence(final String exceptionMessage) {
        final int dotIndex = exceptionMessage.indexOf('.');
        if (dotIndex != -1) {
            return exceptionMessage.substring(0, dotIndex);
        } else {
            return exceptionMessage;
        }
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

    /**
     * Create a Problem Dialog.
     * @param parentFrame the parent frame
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     */
    public static void reportProblem(final Frame parentFrame, final String whileDoing) {
        reportProblem(parentFrame, whileDoing, null);
    }
    
    /**
     * Create a Problem Dialog.
     * @param parentFrame the parent frame
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     */
    public static void reportProblem(final Frame parentFrame, final String whileDoing, final Exception exception) {
        final Thread callingThread = Thread.currentThread();
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                final ProblemDialog dialog = new ProblemDialog(parentFrame, whileDoing, exception, callingThread);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
    }
}
