package uk.me.gumbley.minimiser.gui.dialog.about;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.apache.commons.lang.StringUtils;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPluginDescriptor;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * A modal About... dialog.
 * 
 * @author matt
 */
public final class AboutDialog extends JDialog implements 
        PropertyChangeListener {
    private static final long serialVersionUID = -5494127720196381487L;
    private static final int TEXTPANE_WIDTH = 550; // to get the GPL to fit!
    private static final int TEXTPANE_HEIGHT = 350;
    private final JOptionPane optionPane;
    private final String btnString1 = "Continue";
    private final ArrayList<SwingWorker> mWorkers;
    private final AWTEventListener awtEventListener;
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry; 

    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     * @param cursor the cursor manager
     * @param pluginRegistry the plugin registry
     */
    public AboutDialog(final Frame parentFrame,
            final CursorManager cursor,
            final PluginRegistry pluginRegistry) {
        super(parentFrame, true);
        mCursorManager = cursor;
        mPluginRegistry = pluginRegistry;
        setTitle("About " + mPluginRegistry.getApplicationName());
        mWorkers = new ArrayList<SwingWorker>();
        
        // Create an array of the text and components to be displayed.
        final StringBuilder msg = new StringBuilder();
        msg.append(mPluginRegistry.getApplicationName());
        msg.append(" v");
        msg.append(mPluginRegistry.getApplicationVersion());
        msg.append("\n");
        final ApplicationPluginDescriptor applicationPluginDescriptor = mPluginRegistry.getApplicationPluginDescriptor();
        if (applicationPluginDescriptor != null
            && !StringUtils.isBlank(applicationPluginDescriptor.getShortLicenseDetails())) {
            msg.append(applicationPluginDescriptor.getShortLicenseDetails().trim());
        }

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("About", getAboutComponent());
        tabbedPane.addTab("Credits", getCreditsComponent());
        tabbedPane.addTab("License", getLicenseComponent());
        cursorNormal();
        
        final JTextArea textArea = new JTextArea(msg.toString());
        textArea.setEditable(false);
        
        final Object[] array = {textArea, tabbedPane};
        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {btnString1};
        
        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_OPTION, null, options, options[0]);

        // Make this dialog display it.
        setContentPane(optionPane);

        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent ce) {
                tabbedPane.requestFocusInWindow();
            }
        });
        // Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        
        // Load and display the about texts after the window has been
        // made visible. Performance legerdemain...
        awtEventListener = new AWTEventListener() {
                            public void eventDispatched(final AWTEvent event) {
                                if (event.getID() == WindowEvent.WINDOW_OPENED) {
                                    for (SwingWorker worker : mWorkers) {
                                        worker.start();
                                    }
                                }
                            }
                            };
        Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.WINDOW_EVENT_MASK);
    }

    private Component getLicenseComponent() {
        final ApplicationPluginDescriptor applicationPluginDescriptor = mPluginRegistry.getApplicationPluginDescriptor();
        if (applicationPluginDescriptor != null
            && !StringUtils.isBlank(applicationPluginDescriptor.getFullLicenseDetailsResourcePath())) {
            return getAppropriateComponentForResource(applicationPluginDescriptor.getFullLicenseDetailsResourcePath().trim());
        }
        return new JLabel("No Application Plugin, or no 'licence' resource defined");
    }

    private Component getAppropriateComponentForResource(final String resourcePath) {
        if (ResourceLoader.resourceExists(resourcePath)) {
            final String lowerResourcePath = resourcePath.toLowerCase();
            if (lowerResourcePath.endsWith(".txt")) {
                return getTextResourceTextPane(resourcePath);
            } else if (lowerResourcePath.endsWith(".html")
                     || lowerResourcePath.endsWith(".htm")) {
                return getHTMLResourceTextPane(resourcePath);
            } else {
                return new JLabel("Resource '" + resourcePath + "' is not text or HTML");
            }
        } else {
            return new JLabel("Resource '" + resourcePath + "' not found");
        }
    }

    private Component getCreditsComponent() {
        return getHTMLResourceTextPane("credits.html");
    }

    private Component getAboutComponent() {
        final ApplicationPluginDescriptor applicationPluginDescriptor = mPluginRegistry.getApplicationPluginDescriptor();
        if (applicationPluginDescriptor != null
            && !StringUtils.isBlank(applicationPluginDescriptor.getAboutDetailsResourcePath())) {
            return getAppropriateComponentForResource(applicationPluginDescriptor.getAboutDetailsResourcePath().trim());
        }
        return new JLabel("No Application Plugin, or no 'about' resource defined");
    }

    private Component getHTMLResourceTextPane(final String resourceName) {
        return getResourceTextPane("text/html", resourceName);
    }

    private Component getTextResourceTextPane(final String resourceName) {
        return getResourceTextPane("text/plain", resourceName);
    }

    private Component getResourceTextPane(final String resourceType, final String resourceName) {
        final JTextPane textPane = new JTextPane();
        textPane.setContentType(resourceType);
        textPane.setText(" . . . loading text . . .");
        loadLater(resourceName, textPane);
        final JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(TEXTPANE_WIDTH, TEXTPANE_HEIGHT));
        scrollPane.setMinimumSize(new Dimension(TEXTPANE_WIDTH, TEXTPANE_HEIGHT));
        return scrollPane;
    }
    
    private void loadLater(final String resourceName, final JTextPane textPane) {
        final SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                assert (!EventQueue.isDispatchThread());
                final StringBuilder text = new StringBuilder();
                ResourceLoader.readResource(text, resourceName);
                return text.toString();
            }
            
            @Override
            public void finished() {
                textPane.setText(get().toString());
                textPane.moveCaretPosition(0);
            }
        };
        mWorkers.add(worker);
    }
    
    private void cursorNormal() {
        final SwingWorker worker = new SwingWorker() {

            @Override
            public Object construct() {
                return null;
            }
            
            @Override
            public void finished() {
                mCursorManager.normal("AboutDialog");
            }
        };
        mWorkers.add(worker);
    }

    /**
     * This method reacts to state changes in the option pane.
     * @param e the event
     */
    public void propertyChange(final PropertyChangeEvent e) {
        final String prop = e.getPropertyName();
        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
                        .equals(prop))) {
            final Object value = optionPane.getValue();
            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                // ignore reset
                return;
            }
            // Reset the JOptionPane's value.
            // If you don't do this, then if the user
            // presses the same button next time, no
            // property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            clearAndHide();
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        if (awtEventListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
        }
        setVisible(false);
        mCursorManager.normal("AboutDialog");
    }

    /**
     * Create an About Dialog.
     * @param pluginRegistry the plugin registry
     * @param parentFrame the parent frame
     * @param cursor the cursor manager
     */
    public static void showAbout(final PluginRegistry pluginRegistry,
            final Frame parentFrame, final CursorManager cursor) {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final AboutDialog dialog = new AboutDialog(parentFrame, cursor, pluginRegistry);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
    }
}
