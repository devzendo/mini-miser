package uk.me.gumbley.minimiser.gui.tabfactory;

import java.awt.Component;
import java.awt.Label;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A Tab that's given a database descriptor, and allows it to be checked for.
 * Has a label as its component.
 * 
 * @author matt
 *
 */
public final class StubRecordingTab implements Tab {
    private static final Logger LOGGER = Logger
            .getLogger(StubRecordingTab.class);
    private final DatabaseDescriptor databaseDescriptor;
    private volatile Label label;
    private final boolean constructedOnEventThread;
    private boolean initComponentCalledOnEventThread = false;
    private boolean initComponentIsCalled = false;
    
    private static volatile int constructCount = 0;

    /**
     * Construct with descriptor given from factory via app context
     * @param descriptor the database descritpor
     */
    public StubRecordingTab(final DatabaseDescriptor descriptor) {
        LOGGER.debug("Creating StubRecordingTab for " + descriptor.getDatabaseName());
        this.databaseDescriptor = descriptor;
        constructedOnEventThread = SwingUtilities.isEventDispatchThread();
        constructCount++;
    }
    /**
     * {@inheritDoc}
     */
    public synchronized Component getComponent() {
        LOGGER.debug("Label " + label + " being returned from getComponent()");
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void initComponent() {
        LOGGER.debug("initComponent being called");
        initComponentCalledOnEventThread = SwingUtilities.isEventDispatchThread();
        initComponentIsCalled = true;
        label = new Label();
    }
    
    /** 
     * Get the database descriptor injected in
     * @return the database descriptor
     */
    public DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }
    
    /**
     * Was this tab constructed on a Swing Event Thread?
     * @return true iff constructed on a Swing Event Thread
     */
    public boolean isConstructedOnEventThread() {
        return constructedOnEventThread;
    }
    
    /**
     * Was initComponents called on a Swing Event Thread?
     * @return truw iff called on a Swing Event Thread.
     */
    public boolean isInitComponentsCalledOnEventThread() {
        return initComponentCalledOnEventThread;
    }
    
    /**
     * Was initComponent called at all?
     * @return true iff called
     */
    public boolean isInitComponentCalled() {
        return initComponentIsCalled;
    }
    
    /**
     * How many times has this class been constructed?
     * @return the count of cinstructions
     */
    public static int getConstructCount() {
        return constructCount;
    }
    
    /**
     * Clear the count of constructions
     */
    public static void clearConstructCount() {
        constructCount = 0;
    }
}
