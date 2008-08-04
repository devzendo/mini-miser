package uk.me.gumbley.minimiser.gui.dialog;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * A modal About... dialog.
 * 
 * @author matt
 */
public final class AboutDialog extends JDialog implements 
        PropertyChangeListener {
    private static final long serialVersionUID = -5494127720196381487L;
    private static final Logger LOGGER = Logger.getLogger(AboutDialog.class);
    private static final int TEXTPANE_WIDTH = 550; // to get the GPL to fit!
    private static final int TEXTPANE_HEIGHT = 350;
    private JOptionPane optionPane;
    private String btnString1 = "Continue";
    private ArrayList<SwingWorker> workers;
    private AWTEventListener awtEventListener;
    private final CursorManager cursorManager; 

    /**
     * Creates the reusable dialog.
     * @param parentFrame the parent frame
     */
    public AboutDialog(final Frame parentFrame, final CursorManager cursor) {
        super(parentFrame, true);
        cursorManager = cursor;
        setTitle("About " + AppName.getAppName());
        workers = new ArrayList<SwingWorker>();
        
        // Create an array of the text and components to be displayed.
        final StringBuilder msg = new StringBuilder();
        msg.append(AppName.getAppName());
        msg.append(" v");
        msg.append(AppVersion.getVersion());
        msg.append("\n(C) 2008 Matt Gumbley");

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
                                    for (SwingWorker worker : workers) {
                                        worker.start();
                                    }
                                }
                            }
                            };
        Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.WINDOW_EVENT_MASK);
    }

    private Component getLicenseComponent() {
        return getTextResourceTextPane("COPYING.txt");
    }

    private Component getCreditsComponent() {
        return getHTMLResourceTextPane("credits.html");
    }

    private Component getAboutComponent() {
        return getHTMLResourceTextPane("about.html");
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
                readResource(text, resourceName);
                return text.toString();
            }
            
            public void finished() {
                textPane.setText(get().toString());
                textPane.moveCaretPosition(0);
            }
        };
        workers.add(worker);
    }
    
    private void cursorNormal() {
        final SwingWorker worker = new SwingWorker() {

            @Override
            public Object construct() {
                return null;
            }
            
            public void finished() {
                cursorManager.normal();
            }
        };
        workers.add(worker);
    }

    private void readResource(final StringBuilder store, final String resourceName) {
        final InputStream resourceAsStream = Thread.currentThread().
            getContextClassLoader().
            getResourceAsStream(resourceName);
        final int bufsize = 16384;
        final byte[] buf = new byte[bufsize];
        int nread;
        try {
            while ((nread = resourceAsStream.read(buf, 0, bufsize)) != -1) {
                final String block = new String(buf, 0, nread);
                store.append(block);
            }
        } catch (final IOException e) {
            LOGGER.warn("Could not read resource '" + resourceName + "': " + e.getMessage());
        } finally {
            try {
                resourceAsStream.close();
            } catch (final IOException ioe) {
            }
        }
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
        cursorManager.normal();
    }

    /**
     * Create an About Dialog.
     * @param parentFrame the parent frame
     */
    public static void showAbout(final Frame parentFrame, final CursorManager cursor) {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final AboutDialog dialog = new AboutDialog(parentFrame, cursor);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
    }
}
