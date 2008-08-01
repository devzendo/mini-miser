package uk.me.gumbley.minimiser.opener;

import java.awt.Frame;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.CursorManager;

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

    /**
     * Create the factory
     * @param mainframe the main frame
     * @param cursorMgr the cursor manager
     */
    public DefaultOpenerAdapterFactoryImpl(final Frame mainframe, final CursorManager cursorMgr) {
        this.mainFrame = mainframe;
        this.cursorManager = cursorMgr;
        // TODO pass in the main frame progress bar thing here
    }
    
    /**
     * {@inheritDoc}
     */
    public OpenerAdapter createOpenerAdapter(final String databaseName) {
        return new SilentOpenerAdapter(mainFrame, databaseName, cursorManager);
    }

    // TODO rename from silent when we have the main frame progress thing
    private final class SilentOpenerAdapter extends AbstractOpenerAdapter {
        public SilentOpenerAdapter(final Frame frame, final String name, final CursorManager cursorMgr) {
            super(frame, name, cursorMgr);
        }

        public void reportProgress(final ProgressStage progressStage, final String description) {
            LOGGER.info("Open progress: " + progressStage + ": " + description);
        }
    }
}
