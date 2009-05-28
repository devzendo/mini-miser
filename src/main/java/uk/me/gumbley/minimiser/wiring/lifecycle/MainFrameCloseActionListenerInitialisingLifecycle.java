package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A Lifecycle that initialises the close action listener
 * and attaches it to the main frame.
 * 
 * Non-TDD.
 * 
 * @author matt
 *
 */
public final class MainFrameCloseActionListenerInitialisingLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameCloseActionListenerInitialisingLifecycle.class);
    private final SpringLoader mSpringLoader;

    /**
     * Construct
     * @param springLoader the SpringLoader.
     */
    public MainFrameCloseActionListenerInitialisingLifecycle(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                LOGGER.debug("Setting the main frame's Close ActionListener");
                final JFrame mainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
                final ActionListener closeActionListener = mSpringLoader.getBean("mainFrameCloseActionListener", ActionListener.class);
                mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                mainFrame.addWindowListener(new WindowListener() {
                    public void windowOpened(final WindowEvent e) {
                    }

                    public void windowClosing(final WindowEvent e) {
                        closeActionListener.actionPerformed(null);
                    }

                    public void windowClosed(final WindowEvent e) {
                    }

                    public void windowIconified(final WindowEvent e) {
                    }

                    public void windowDeiconified(final WindowEvent e) {
                    }

                    public void windowActivated(final WindowEvent e) {
                    }

                    public void windowDeactivated(final WindowEvent e) {
                    }
                });
                LOGGER.debug("The main frame's Close ActionListener is set");
            }
        });
    }
}