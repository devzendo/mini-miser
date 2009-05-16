package uk.me.gumbley.minimiser.gui;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The LifecycleStartupAWTEventListener is attached as a listener
 * to the main JFrame, and listens for it becoming visible. At
 * this point, it triggers the Lifecycle startup on a separate
 * thread, surrounding this with an hourglass cursor.
 * 
 * @author matt
 *
 */
public final class LifecycleStartupAWTEventListener implements AWTEventListener {
    private static final Logger LOGGER = Logger
            .getLogger(LifecycleStartupAWTEventListener.class);
    private final CursorManager mCursorManager;
    private final LifecycleManager mLifecycleManager;
    private final SpringLoader mSpringLoader;
    private final JFrame mMainFrame;
    
    /**
     * Construct the window visible listener that triggers the
     * lifecycle startup.
     * @param springLoader the SpringLoader
     */
    public LifecycleStartupAWTEventListener(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
        mLifecycleManager = mSpringLoader.getBean("lifecycleManager", LifecycleManager.class);
        mMainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventDispatched(final AWTEvent event) {
        if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource().equals(mMainFrame)) {
            LOGGER.info("Main frame visible; starting lifecycle manager");
            mCursorManager.hourglass(this.getClass().getSimpleName());
            final SwingWorker worker = new SwingWorker() {
                @Override
                public Object construct() {
                    Thread.currentThread().setName("Lifecycle Startup");
                    
                    mLifecycleManager.startup();
                    return null;
                }
                @Override
                public void finished() {
                    mCursorManager.normal(this.getClass().getSimpleName());
                }
            };
            worker.start();
        }
    }
}