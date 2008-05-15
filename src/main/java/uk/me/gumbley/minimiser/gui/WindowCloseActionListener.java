/**
 * 
 */
package uk.me.gumbley.minimiser.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.SwingWorker;

/**
 * Invoked when the app is to exit, either from the menu, or from an adapted
 * WindowListener.
 * 
 * @author matt
 *
 */
final class WindowCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(WindowCloseActionListener.class);

    private final JFrame mainFrame;
    private final MainFrameFacade mainFrameFacade;
    
    /**
     * @param frame the application main frame
     * @param facade the facade into the application
     */
    public WindowCloseActionListener(final JFrame frame, final MainFrameFacade facade) {
        this.mainFrame = frame;
        this.mainFrameFacade = facade;
    }
    
    /**
     * Note that e can be null when called from the adapted WindowListener.
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        int opt = JOptionPane.showConfirmDialog(this.mainFrame,
            "Are you sure you want to exit?", "Confirm exit",
            JOptionPane.YES_NO_OPTION);
        // myLogger.info("option returned is " + opt);
        if (opt == 0) {
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    LOGGER.info("Shutting down...");
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                WindowCloseActionListener.this.mainFrame.setCursor(new Cursor(
                                        Cursor.WAIT_CURSOR));
                                mainFrameFacade.enableDisableControls(false);
                            }
                        });
                    } catch (final InterruptedException e) {
                    } catch (final InvocationTargetException e) {
                    }
                    mainFrameFacade.shutdown();
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                WindowCloseActionListener.this.mainFrame.setCursor(new Cursor(
                                        Cursor.DEFAULT_CURSOR));
                            }
                        });
                    } catch (final InterruptedException e) {
                    } catch (final InvocationTargetException e) {
                    }
                    return null;
                }
            };
            worker.start();
        }
    }
}