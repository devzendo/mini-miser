package uk.me.gumbley.minimiser.gui.messagequeue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.Beautifier;
import uk.me.gumbley.minimiser.gui.MainFrameStatusBar;
import uk.me.gumbley.minimiser.gui.StatusBarMessageQueueAdapter;
import uk.me.gumbley.minimiser.gui.messagequeueviewer.DefaultMessageQueueViewerFactory;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.util.DelayedExecutor;
import uk.me.gumbley.minimiser.version.AppVersion;


/**
 * @author matt
 *
 */
public final class DriveStatusBarDisplay {

    private static final Logger LOGGER = Logger
            .getLogger(DriveStatusBarDisplay.class);
    private MessageQueue messageQueue;
    private int messageNumber;
    
    private JFrame frame;
    private DefaultMessageQueueViewerFactory messageQueueViewerFactory;

    private DriveStatusBarDisplay()  {
        frame = new JFrame("title");
        frame.setPreferredSize(new Dimension(600, 480));
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        final JButton addMessageButton = new JButton("Add sample message");
        addMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                addMessage();
            }
        });
        
        buttonPanel.add(addMessageButton);
        
        final JButton removeMessageButton = new JButton("Remove sample message");
        removeMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                removeMessage();
            }
        });
        
        buttonPanel.add(removeMessageButton);
        
        frame.add(buttonPanel, BorderLayout.NORTH);

        messageQueue = new MessageQueue();
        
        final MainFrameStatusBar mainFrameStatusBar = new MainFrameStatusBar(new DelayedExecutor());
        frame.add(mainFrameStatusBar.getPanel(), BorderLayout.SOUTH);

        messageQueueViewerFactory = new DefaultMessageQueueViewerFactory(mainFrameStatusBar, frame, messageQueue);
        mainFrameStatusBar.addLaunchMessageQueueActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                messageQueueViewerFactory.createMessageQueueViewer();
            }
        });

        
        frame.pack();
        frame.setVisible(true);
        
        
        new StatusBarMessageQueueAdapter(mainFrameStatusBar, messageQueue).wireAdapter();
    }

    /**
     * 
     */
    protected void addMessage() {
        final double imprandom = Math.random();
        Message.Importance importance;
        if (imprandom < 0.3) {
            importance = Message.Importance.HIGH;
        } else if (imprandom < 0.6) {
            importance = Message.Importance.MEDIUM;
        } else {
            importance = Message.Importance.LOW;
        }
        final String subject = "Message # " + messageNumber++;
        final String content = "Here is a sample message\nIt's a multiline message\n"
            + "But otherwise, rather boring...";
        messageQueue.addMessage(new SimpleMessage(subject, content, importance));
    }

    private void removeMessage() {
        messageQueue.removeMessage(messageQueue.getMessage(0));
    }

    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        

        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                LOGGER.info(String.format("%s %s message queue display experimentation starting...", AppName.getAppName(), AppVersion.getVersion()));
                Beautifier.makeBeautiful();
                new DriveStatusBarDisplay();
            }
        });
    }
}
