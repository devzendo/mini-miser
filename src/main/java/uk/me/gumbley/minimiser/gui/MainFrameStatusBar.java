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

/**
 * A StatusBar on the bottom of the Main Frame
 * 
 * @author matt
 */
public class MainFrameStatusBar extends AbstractStatusBar {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameStatusBar.class);
    private JProgressBar progressBar;

    private JLabel label;

    private JPanel west;

    /**
     * @param exec the DelayedExecutor
     */
    public MainFrameStatusBar(final DelayedExecutor exec) {
        super(exec);
        west = new JPanel();
        west.setLayout(new BorderLayout());
        west.setBorder(BorderFactory.createEtchedBorder());
        final JPanel panel = new JPanel();
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progressBar.setPreferredSize(new Dimension(150, 16));
        panel.setLayout(new FlowLayout());
        panel.add(progressBar);
        label = new JLabel(" ");
        panel.add(label);
        west.add(panel, BorderLayout.WEST);
    }

    /**
     * @return the panel for adding to the GUI
     */
    public JPanel getPanel() {
        return west;
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
}
