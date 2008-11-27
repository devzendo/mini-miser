package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;

/**
 * A Swing-based MessageQueueViewer.
 * 
 * @author matt
 *
 */
public final class DefaultMessageQueueViewer extends AbstractMessageQueueViewer {

    private static final Logger LOGGER = Logger
            .getLogger(DefaultMessageQueueViewer.class);
    private JDialog dialog;
    private MessageQueue messageQueue;
    private HTMLPanel messageSubjectPane;
    private HTMLPanel noMessagesTextPane;
    private JButton previousButton;
    private JButton removeButton;
    private JButton nextButton;
    private final MessageRendererFactory messageRendererFactory;
    private JPanel mainPanel;
    private JComponent emptyControls;
    private JPanel controlsContainer;
    private JPanel bodyContainer;
    private Message currentMessage;
    private MessageRenderer currentMessageRenderer;

    /**
     * Create the DefaultMessageQueueViewer given its factory.
     * @param factory this viewer's factory
     * @param rendererFactory the message renderer factory
     */
    public DefaultMessageQueueViewer(final MessageQueueViewerFactory factory,
            final MessageRendererFactory rendererFactory) {
        super(factory);
        this.messageRendererFactory = rendererFactory;
        final Frame mainFrame = getMessageQueueViewerFactory().getMainFrame();
        messageQueue = getMessageQueueViewerFactory().getMessageQueue();
        
        dialog = new JDialog(mainFrame, false);
        dialog.setPreferredSize(new Dimension(mainFrame.getWidth() - 40, 200));
        // TODO I'd like it moved so it's bottom edge is just above the
        // status bar, and centred within the main app frame
        dialog.setTitle("Messages from " + AppName.getAppName());
        
        initialiseNoMessagesTextPane();
        initialiseEmptyControlsPane();
        initialiseMainPanel();
        dialog.add(mainPanel);
        
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                getMessageQueueViewerFactory().messageViewerClosed();
            }
        });
        
        updateWithSelectedMessage();
        
        dialog.pack();
        dialog.setVisible(true);
    }

    private void initialiseEmptyControlsPane() {
        emptyControls = new JLabel("");
    }

    private void initialiseNoMessagesTextPane() {
        noMessagesTextPane = new HTMLPanel(createHTMLText("<p align=\"center\">No messages waiting</p>"));
    }

    private void setBodyComponent(final Component component) {
        LOGGER.debug("Setting body component to " + component);
        bodyContainer.removeAll();
        bodyContainer.add(component, BorderLayout.CENTER);
        bodyContainer.revalidate();
        bodyContainer.repaint();
    }

    private void setControlsComponent(final Component controls) {
        LOGGER.debug("Setting controls component to " + controls);
        controlsContainer.removeAll();
        controlsContainer.add(controls, BorderLayout.EAST);
        controlsContainer.revalidate();
        controlsContainer.repaint();
    }
    
    private void updateWithSelectedMessage() {
        final int currentIndex = messageQueue.getCurrentMessageIndex();
        boolean enablePrevious = false;
        boolean enableRemove = false;
        boolean enableNext = false;
        // TODO turn off updates
        if (currentIndex == -1) {
            currentMessage = null;
            currentMessageRenderer = null;
            LOGGER.info("empty - no message display");
            messageSubjectPane.setText(createDialogFontText("<em>No messages</em>"));
            setBodyComponent(noMessagesTextPane);
            setControlsComponent(emptyControls);
        } else {
            currentMessage = messageQueue.getMessageByIndex(currentIndex);
            LOGGER.info("rendering message " + currentIndex + ": " + currentMessage.getSubject());
            // Subject... always text, for all messages. so handle it here.
            messageSubjectPane.setText(createSubjectLabelText(currentIndex + 1, messageQueue.size(), currentMessage.getSubject()));
            currentMessageRenderer = messageRendererFactory.createRenderer(currentMessage);
            setBodyComponent(currentMessageRenderer.render());
            final Component controls = currentMessageRenderer.renderControls();
            setControlsComponent(controls == null ? emptyControls : controls);
            enableRemove = true;
            enablePrevious = currentIndex != 0;
            enableNext = currentIndex != messageQueue.size() - 1;
        }
        messageSubjectPane.setCaretPosition(0);
        previousButton.setEnabled(enablePrevious);
        removeButton.setEnabled(enableRemove);
        nextButton.setEnabled(enableNext);
        mainPanel.validate();
        // TODO turn on updates
    }

    private String createHTMLText(final String bodyText) {
        return String.format("<html><head><title>sample text</title></head><body>%s</body></html>", bodyText);
    }

    private String createDialogFontText(final String input) {
        return String.format("<font face=\"dialog\">%s</font>", input);
    }

    private String createSubjectLabelText(final int messageNumber, final int numberOfMessages, final String subject) {
        return createDialogFontText(String.format("<b>Message %d of %d</b> - <em>%s</em>", messageNumber, numberOfMessages, subject));
    }

    /**
     * @return
     */
    private void initialiseMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(subjectPanel(), BorderLayout.NORTH);
        bodyContainer = new JPanel();
        bodyContainer.setLayout(new BorderLayout());
        mainPanel.add(bodyPanel(), BorderLayout.CENTER);
        controlsContainer = new JPanel();
        controlsContainer.setLayout(new BorderLayout());
        mainPanel.add(controlsPanel(), BorderLayout.SOUTH);
    }

    private Component subjectPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JPanel greypanel = new JPanel();
        greypanel.setLayout(new BorderLayout());
        greypanel.setBackground(Color.GRAY);
        greypanel.setForeground(Color.WHITE);
        messageSubjectPane = new HTMLPanel();
        messageSubjectPane.setHTMLText(createHTMLText("<font face=\"dialog\"><b>No messages</b></font>"));
        messageSubjectPane.setBackground(Color.GRAY);
        messageSubjectPane.setForeground(Color.WHITE);
        greypanel.add(messageSubjectPane, BorderLayout.WEST);
        panel.add(greypanel, BorderLayout.CENTER);
        return panel;
    }

    private Component bodyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(bodyContainer), BorderLayout.CENTER);
        return panel;
    }

    private JPanel controlsPanel() {
        final JPanel controlsMainPanel = new JPanel();
        controlsMainPanel.setLayout(new BorderLayout());
        
        // Dynamic controls
        controlsMainPanel.add(controlsContainer, BorderLayout.CENTER);
        
        // Static controls
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        previousButton = new JButton("<< Previous");
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                messageQueue.previous();
                updateWithSelectedMessage();
            }
        });
        buttonsPanel.add(previousButton);
        
        removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                assert currentMessage != null;
                assert currentMessageRenderer != null;
                // used to be messageQueue.getMessageByIndex(messageQueue.getCurrentMessageIndex())
                messageQueue.removeMessage(currentMessage);
                updateWithSelectedMessage();
            }
        });
        
        buttonsPanel.add(removeButton);
        
        nextButton = new JButton("Next >>");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                messageQueue.next();
                updateWithSelectedMessage();
            }
        });
        
        buttonsPanel.add(nextButton);
        controlsMainPanel.add(buttonsPanel, BorderLayout.EAST);
       
        return controlsMainPanel;
    }
}
