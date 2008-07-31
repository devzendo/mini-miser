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
public class DefaultOpenerAdapterFactoryImpl implements OpenerAdapterFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultOpenerAdapterFactoryImpl.class);
    private final CursorManager cursorManager;

    /**
     * Create the factory
     * @param cursorMgr the cursor manager
     */
    public DefaultOpenerAdapterFactoryImpl(final CursorManager cursorMgr) {
        this.cursorManager = cursorMgr;
        // TODO pass in the main frame progress bar thing here
    }
    
    /**
     * {@inheritDoc}
     */
    public OpenerAdapter createOpenerAdapter(final Frame frame, final String databaseName) {
        return new SilentOpenerAdapter(frame, databaseName, cursorManager);
    }
    

    private final class SilentOpenerAdapter extends AbstractOpenerAdapter {
        public SilentOpenerAdapter(final Frame frame, final String name, final CursorManager cursorMgr) {
            super(frame, name, cursorMgr);
        }

        public void reportProgress(final ProgressStage progressStage, final String description) {
            LOGGER.info("Open progress: " + progressStage + ": " + description);
        }
    }

}
