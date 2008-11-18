package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.util.DelayedExecutor;
import uk.me.gumbley.minimiser.util.Sleeper;

/**
 * A StatusBar on the bottom of the Main Frame
 * 
 * @author matt
 */
public final class MainFrameStatusBar extends AbstractStatusBar {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameStatusBar.class);
    private JProgressBar progressBar;

    private JLabel label;

    private JPanel statusBarPanel;
    private MessagesButton messageQueueButton;

    /**
     * @param exec the DelayedExecutor
     */
    public MainFrameStatusBar(final DelayedExecutor exec) {
        super(exec);
        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new BorderLayout());
        statusBarPanel.setBorder(BorderFactory.createEtchedBorder());
        
        final JPanel progressBarPanel = new JPanel();
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progressBar.setPreferredSize(new Dimension(150, 16));
        progressBarPanel.setLayout(new FlowLayout());
        progressBarPanel.add(progressBar);
        label = new JLabel(" ");
        progressBarPanel.add(label);
        statusBarPanel.add(progressBarPanel, BorderLayout.WEST);
        
        messageQueueButton = new MessagesButton(new Sleeper()); // TODO inject this
        messageQueueButton.setPreferredSize(new Dimension(150, 16));
        messageQueueButton.setVisible(false);
        statusBarPanel.add(messageQueueButton, BorderLayout.EAST);
    }

    /**
     * @return the panel for adding to the GUI
     */
    public JPanel getPanel() {
        return statusBarPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void internalSetMessageTextNow(final String message) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                label.setText(message);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void clearProgress() {
        LOGGER.debug("clearing progress");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setMaximum(0);
                progressBar.setMinimum(0);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setEncryptedIndicator(final boolean encrypted) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressLength(final int max) {
        LOGGER.debug("progress length is " + max);
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setMaximum(max);
                progressBar.setMinimum(0);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressStep(final int step) {
        LOGGER.debug("progress step is " + step);
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setValue(step);
            }
        });
    }
    
    /**
     * Sets the number of queued messages
     * @param number the number of queued messages
     */
    public void setNumberOfQueuedMessages(final int number) {
        super.setNumberOfQueuedMessages(number);
        messageQueueButton.setNumberOfMessages(number);
    }
}
