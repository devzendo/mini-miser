package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.awt.Component;
import java.awt.Label;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * A Tools->Options Tab that's given a (change-collecting) prefs, and allows it
 * to be checked for.
 * Has a label as its component.
 * 
 * @author matt
 *
 */
public final class StubRecordingToolsOptionsTab implements ToolsOptionsTab {
    private static final Logger LOGGER = Logger
            .getLogger(StubRecordingToolsOptionsTab.class);
    private final Prefs prefs;
    private volatile Label label;
    private final boolean constructedOnEventThread;
    private boolean initComponentCalledOnEventThread = false;
    private boolean initComponentIsCalled = false;
    private boolean disposeComponentIsCalled = false;
    private boolean destroyIsCalled = false;
    private boolean destroyedOnNonEventThread = false;
    private boolean disposeComponentCalledOnEventThread = false;;
    
    private static volatile int constructCount = 0;

    /**
     * Construct with prefs given from factory via app context
     * @param preferences the change-collecting prefs
     */
    public StubRecordingToolsOptionsTab(final Prefs preferences) {
        LOGGER.debug("Creating StubRecordingToolsOptionsTab for " + preferences);
        this.prefs = preferences;
        constructedOnEventThread = SwingUtilities.isEventDispatchThread();
        constructCount++;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Stub";
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
     * Get the Prefs injected in
     * @return the Prefs
     */
    public Prefs getPrefs() {
        return prefs;
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
    
    /**
     * Was disposeComponent called at all?
     * @return true iff called
     */
    public boolean isDisposeComponentCalled() {
        return disposeComponentIsCalled;
    }
    
    /**
     * Was destroy called at all?
     * @return true iff called
     */
    public boolean isDestroyCalled() {
        return destroyIsCalled;
    }
    
    /**
     * Was destroy called on a non event thread?
     * @return true iff called on a non event thread
     */
    public boolean isDestroyedOnNonEventThread() {
        return destroyedOnNonEventThread;
    }
    
    /**
     * Was disposeComponent called on an event thread?
     * @return true iff called on an event thread.
     */
    public boolean isDisposedOnEventThread() {
        return disposeComponentCalledOnEventThread;
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        destroyIsCalled = true;
        destroyedOnNonEventThread = !SwingUtilities.isEventDispatchThread();
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        disposeComponentIsCalled = true;
        disposeComponentCalledOnEventThread = SwingUtilities.isEventDispatchThread();
    }
}
