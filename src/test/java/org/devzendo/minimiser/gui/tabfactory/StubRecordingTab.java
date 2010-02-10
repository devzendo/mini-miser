package org.devzendo.minimiser.gui.tabfactory;

import java.awt.Component;
import java.awt.Label;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;

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
    private final TabParameter tabParameter;
    private final TabIdentifier tabIdentifier;
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
     * Construct with descriptor and parameter given from factory via app context
     * @param descriptor the database descriptor
     * @param parameter the tab parameter
     */
    public StubRecordingTab(final DatabaseDescriptor descriptor, final TabParameter parameter) {
        this(descriptor, parameter, null);
    }

    /**
     * Construct with descriptor and parameter given from factory via unit test
     * @param descriptor the database descriptor
     * @param parameter the tab parameter
     * @param tabId the tab identifier
     */
    public StubRecordingTab(final DatabaseDescriptor descriptor, final TabParameter parameter, final TabIdentifier tabId) {
        LOGGER.debug("Creating StubRecordingTab for " + descriptor.getDatabaseName());
        this.databaseDescriptor = descriptor;
        this.tabParameter = parameter;
        this.tabIdentifier = tabId;
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
        label = new Label(tabIdentifier == null ? "null" : tabIdentifier.getDisplayableName());
    }

    /**
     * Get the database descriptor injected in
     * @return the database descriptor
     */
    public DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }

    /**
     * Get the tab parameter injected in
     * @return the tab parameter
     */
    public TabParameter getTabParameter() {
        return tabParameter;
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
