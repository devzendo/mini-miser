package uk.me.gumbley.minimiser.gui.lifecycle;

import javax.swing.JFrame;
import uk.me.gumbley.minimiser.gui.WindowGeometryStore;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;

/**
 * A Lifecycle that saves the geometry of the main window.
 * 
 * @author matt
 *
 */
public class MainFrameGeometrySaverLifecycle implements Lifecycle {
    private final WindowGeometryStore windowGeometryStore;
    private final JFrame frame;

    /**
     * Construct
     * @param geometryStore the geometry store
     * @param mainFrame the main window, which will have been populated in the
     * MainFrameFactory by now.
     */
    public MainFrameGeometrySaverLifecycle(final WindowGeometryStore geometryStore, final JFrame mainFrame) {
        this.windowGeometryStore = geometryStore;
        this.frame = mainFrame;
        
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        windowGeometryStore.saveGeometry(frame);
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing - the geometry is applied way before Lifecycle startup 
    }
}
