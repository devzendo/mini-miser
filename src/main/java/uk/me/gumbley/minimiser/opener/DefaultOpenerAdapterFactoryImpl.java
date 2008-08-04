package uk.me.gumbley.minimiser.opener;

import java.awt.Frame;
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.MainFrameStatusBar;

/**
 * An OpenerAdapterfactory that creates OpenerAdapters that control the
 * hourglass cursor, and display progress updates on the main frame.
 * 
 * @author matt
 *
 */
public final class DefaultOpenerAdapterFactoryImpl implements OpenerAdapterFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultOpenerAdapterFactoryImpl.class);
    private final CursorManager cursorManager;
    private final Frame mainFrame;
    private final MainFrameStatusBar statusBar;

    /**
     * Create the factory
     * @param mainframe the main frame
     * @param cursorMgr the cursor manager
     * @param status the status bar
     */
    public DefaultOpenerAdapterFactoryImpl(final Frame mainframe, final CursorManager cursorMgr, final MainFrameStatusBar status) {
        this.mainFrame = mainframe;
        this.cursorManager = cursorMgr;
        this.statusBar = status;
    }
    
    /**
     * {@inheritDoc}
     */
    public OpenerAdapter createOpenerAdapter(final String databaseName) {
        return new MainFrameOpenerAdapter(mainFrame, databaseName, cursorManager);
    }

    private final class MainFrameOpenerAdapter extends AbstractOpenerAdapter {
        public MainFrameOpenerAdapter(final Frame frame, final String name, final CursorManager cursorMgr) {
            super(frame, name, cursorMgr);
        }

        public void reportProgress(final ProgressStage progressStage, final String description) {
            LOGGER.info("Open progress: " + progressStage + ": " + description);
            statusBar.setProgressLength(progressStage.getMaximumValue());
            if (progressStage.getValue() == progressStage.getMaximumValue()) {
                // not ideal, could use the delayed executor here...
                new Thread(new Runnable() {
                    public void run() {
                        ThreadUtils.waitNoInterruption(500);
                        statusBar.clearProgress();
                        statusBar.clearMessage();
                    }
                }).start();
            }
            statusBar.setProgressStep(progressStage.getValue());
            statusBar.displayMessage(description);
        }
    }
}
