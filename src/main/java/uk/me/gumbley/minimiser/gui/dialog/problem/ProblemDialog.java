package uk.me.gumbley.minimiser.gui.dialog.problem;

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

import org.apache.commons.lang.StringUtils;

import uk.me.gumbley.minimiser.pluginmanager.PluginDescriptor;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * A modal dialog that displays the detail of a problem.
 * 
 * @author matt
 */
public final class ProblemDialog extends JDialog implements 
        PropertyChangeListener {
    private static final long serialVersionUID = -5625177120250936170L;
    private final JOptionPane optionPane;
    private final String btnString1 = "Continue";
    private final PluginRegistry mPluginRegistry;

    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     * @param caller the calling Thread
     * @param pluginRegistry the plugin registry which will be used
     * to obtain details of the app
     */
    public ProblemDialog(final Frame parentFrame,
            final String whileDoing,
            final Exception exception,
            final Thread caller,
            final PluginRegistry pluginRegistry) {
        super(parentFrame, true);
        mPluginRegistry = pluginRegistry;
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

        detail.append(mPluginRegistry.getApplicationName());
        detail.append(" v");
        detail.append(mPluginRegistry.getApplicationVersion());
        detail.append("\n\n");
        detail.append("Calling thread:\n  ");
        detail.append(caller);
        if (exception != null) {
            detail.append("\n\n");
            detail.append(exceptionDetails(exception));
        }
        msg.append("Please copy all of the following details of the problem\n");
        msg.append("and send it to " + getContactDetails() + "\n");

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
            @Override
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

    private String getContactDetails() {
        final StringBuilder mailaddr = new StringBuilder();
        final PluginDescriptor appPlugin = mPluginRegistry.getApplicationPluginDescriptor();
        if (appPlugin == null
            || StringUtils.isBlank(appPlugin.getDevelopersContactDetails())) {
            mailaddr.append("minimiser-dev@gumbley.me.uk"); // TODO make this a resource?
        } else {
            mailaddr.append(appPlugin.getDevelopersContactDetails().trim());
        }
        return mailaddr.toString();
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
