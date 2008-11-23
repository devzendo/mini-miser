package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;

/**
 * A Swing-based MessageQueueViewer.
 * 
 * @author matt
 *
 */
public final class DefaultMessageQueueViewer extends AbstractMessageQueueViewer {

    private JDialog dialog;
    private MessageQueue messageQueue;
    private JTextPane messageSubjectPane;
    private JTextPane messageTextPane;

    /**
     * Create the DefaultMessageQueueViewer given its factory.
     * @param factory this viewer's factory
     */
    public DefaultMessageQueueViewer(final MessageQueueViewerFactory factory) {
        super(factory);
        final Frame mainFrame = getMessageQueueViewerFactory().getMainFrame();
        messageQueue = getMessageQueueViewerFactory().getMessageQueue();
        dialog = new JDialog(mainFrame, false);
        dialog.setPreferredSize(new Dimension(mainFrame.getWidth() - 40, 200));
        // TODO I'd like it moved so it's bottom edge is just above the
        // status bar, and centred within the main app frame
        dialog.setTitle("Messages from " + AppName.getAppName());
        dialog.add(initialiseMainPanel());
        
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                getMessageQueueViewerFactory().messageViewerClosed();
            }
        });
        
        updateWithSelectedMessage();
        
        dialog.pack();
        dialog.setVisible(true);
    }

    private void updateWithSelectedMessage() {
        messageSubjectPane.setText(createSubjectLabelText(1, 1, "this is a fake message"));
        messageSubjectPane.setCaretPosition(0);
        messageTextPane.setText(sampleText());
        messageTextPane.setCaretPosition(0);
    }
    
    private String createSubjectLabelText(final int messageNumber, final int numberOfMessages, final String subject) {
        return String.format("<font face=\"dialog\"><b>Message %d of %d</b> - <em>%s</em></font>", messageNumber, numberOfMessages, subject);
    }

    private Component initialiseMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(subjectPanel(), BorderLayout.NORTH);
        mainPanel.add(bodyPanel(), BorderLayout.CENTER);
        mainPanel.add(controlsPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private Component subjectPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JPanel greypanel = new JPanel();
        greypanel.setLayout(new BorderLayout());
        greypanel.setBackground(Color.GRAY);
        greypanel.setForeground(Color.WHITE);
        messageSubjectPane = new JTextPane();
        messageSubjectPane.setContentType("text/html");
        messageSubjectPane.setText("<font face=\"dialog\"><b>No messages</b></font>");
        messageSubjectPane.setBackground(Color.GRAY);
        messageSubjectPane.setForeground(Color.WHITE);
        messageSubjectPane.setEditable(false);
        greypanel.add(messageSubjectPane, BorderLayout.WEST);
        panel.add(greypanel, BorderLayout.CENTER);
        return panel;
    }

    private Component bodyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        messageTextPane = new JTextPane();
        messageTextPane.setContentType("text/html");
        messageTextPane.setText("");
        messageTextPane.setCaretPosition(0);
        messageTextPane.setEditable(false);
        panel.add(new JScrollPane(messageTextPane), BorderLayout.CENTER);
        return panel;
    }

    private String sampleText() {
        return "<html><head><title>sample text</title></head><body>"
            + "here is a sample document in HTML<br>"
            + "it can have <b>bold</b> text and <em>italic</em> text<br>"
            + "and to show off long documents, it has <br>"
            + "many lines<br>"
            + "of rather uninteresting text<br>"
            + "before ending the document<br>"
            + "but nevertheless, there are enough lines<br>"
            + "and new paragraphs...<p>"
            + "to make it long enough<br>"
            + "to make the scrollbar active."
            + "</body></html>";
    }

    private Component controlsPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JSeparator(), BorderLayout.NORTH);
        
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(new JButton("<< Previous message"));
        buttonsPanel.add(new JButton("Remove message"));
        buttonsPanel.add(new JButton("Next message >>"));
        panel.add(buttonsPanel, BorderLayout.EAST);
       
        return panel;
    }
}
