package uk.me.gumbley.minimiser.gui.tabpanefactory;

import java.awt.Component;
import javax.swing.SwingUtilities;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A Tab that's given a database descriptor, and allows it to be checked for.
 * @author matt
 *
 */
public final class StubRecordingTab implements Tab {
    private final DatabaseDescriptor databaseDescriptor;
    private final boolean constructedOnEventThread;
    private boolean initComponentCalledOnEventThread = false;
    private boolean initComponentIsCalled = false;
    
    private static volatile int constructCount = 0;

    /**
     * Construct with descriptor given from factory via app context
     * @param descriptor the database descritpor
     */
    public StubRecordingTab(final DatabaseDescriptor descriptor) {
        this.databaseDescriptor = descriptor;
        constructedOnEventThread = SwingUtilities.isEventDispatchThread();
        constructCount++;
    }
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        initComponentCalledOnEventThread = SwingUtilities.isEventDispatchThread();
        initComponentIsCalled = true;
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
