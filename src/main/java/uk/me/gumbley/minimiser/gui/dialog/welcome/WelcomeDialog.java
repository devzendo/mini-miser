package uk.me.gumbley.minimiser.gui.dialog.welcome;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.snaildialog.AbstractSnailDialog;

/**
 * A dialog that shows a card layout as the main component, initially containing
 * the welcome text, with a button that switches this text to the what's new
 * text (the button then allowes reversion to the welcome text).
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class WelcomeDialog extends AbstractSnailDialog {
    private static final String WELCOME_HTML = "welcome.html";
    private static final String CHANGELOG_HTML = "changelog.html";
    private static final Logger LOGGER = Logger.getLogger(WelcomeDialog.class);
    private static final String BLANK_PANEL_NAME = "*special*blank*panel*";
    private static final int TEXTPANE_WIDTH = 550;
    private static final int TEXTPANE_HEIGHT = 350;
    private JButton switchButton;
    private JButton cancelButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private final boolean welcome;
    private ActionListener switchActionListener;
    private Map<String, CountDownLatch> loadedResourceLatchMap;

    /**
     * Construct the Welcome Dialog
     * @param parentFrame the main app frame
     * @param cursor the cursor manager
     * @param showWelcome true iff showing the welcome screen initally, false
     * for the what's new screen.
     */
    public WelcomeDialog(final Frame parentFrame, final CursorManager cursor, final boolean showWelcome) {
        super(parentFrame, cursor, "Loading...");
        this.welcome = showWelcome;
        loadedResourceLatchMap = new HashMap<String, CountDownLatch>();
        loadedResourceLatchMap.put(WELCOME_HTML, new CountDownLatch(1));
        loadedResourceLatchMap.put(CHANGELOG_HTML, new CountDownLatch(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container createMainComponent() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Inside there's a display panel with a card layout, but sized to hold
        // the largest content.

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.setPreferredSize(new Dimension(TEXTPANE_WIDTH, TEXTPANE_HEIGHT));
        
        final JPanel blankPanel = new JPanel();
        cardPanel.add(blankPanel, BLANK_PANEL_NAME);

        cardLayout.show(cardPanel, BLANK_PANEL_NAME);
        
        mainPanel.add(cardPanel, BorderLayout.NORTH);
        
        // A separator, with the buttons under it, right-justified
        final JPanel buttonsAndSeparatorPanel = new JPanel();
        buttonsAndSeparatorPanel.setLayout(new BorderLayout());
        buttonsAndSeparatorPanel.add(new JSeparator(), BorderLayout.NORTH);
        
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsAndSeparatorPanel.add(buttonsPanel, BorderLayout.EAST);
        
        switchButton = new JButton("");
        switchButton.setVisible(false);

        cancelButton = new JButton("Continue");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelPressed();
            }
        });
        buttonsPanel.add(switchButton);
        buttonsPanel.add(cancelButton);
        mainPanel.add(buttonsAndSeparatorPanel, BorderLayout.SOUTH);

        return mainPanel;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialise() {
        if (welcome) {
            addSwingWorker(addWelcomeResourceLoader());
            addSwingWorker(addWhatsNewResourceLoader());
        } else {
            addSwingWorker(addWhatsNewResourceLoader());
            addSwingWorker(addWelcomeResourceLoader());
        }
        addSwingWorker(addInitialSwitcher());
        addSwingWorker(addOKCancelEnabler());
    }
    
    private void readResource(final StringBuilder store, final String resourceName) {
        LOGGER.info("Loading '" + resourceName + "'");
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

    private final class ResourceLoadingSwingWorker extends SwingWorker {
        private final String resource;

        public ResourceLoadingSwingWorker(final String resourceName) {
            this.resource = resourceName;
        }
        @Override
        public Object construct() {
            assert (!EventQueue.isDispatchThread());
            final StringBuilder text = new StringBuilder();
            readResource(text, resource);
            return text.toString();
        }
        
        @SuppressWarnings("unchecked")
        public void finished() {
            final JTextPane textPane = new JTextPane();
            textPane.setContentType("text/html");
            final JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setPreferredSize(new Dimension(TEXTPANE_WIDTH, TEXTPANE_HEIGHT));
            scrollPane.setMinimumSize(new Dimension(TEXTPANE_WIDTH, TEXTPANE_HEIGHT));
            cardPanel.add(scrollPane, resource);

            textPane.setText(get().toString());
            textPane.moveCaretPosition(0);
            
            loadedResourceLatchMap.get(resource).countDown();
        }
    }
    private SwingWorker addWelcomeResourceLoader() {
        return new ResourceLoadingSwingWorker(WELCOME_HTML);
    }
    
    private void switchToWhatsNew() {
        try {
            loadedResourceLatchMap.get(CHANGELOG_HTML).await();
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        setTitle("What's new in this release?");
        switchButton.setText("Welcome to " + AppName.getAppName());
        switchButton.setVisible(true);
        setSwitchActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                switchToWelcome();
            }
        });
        cardLayout.show(cardPanel, CHANGELOG_HTML);
    }

    private SwingWorker addWhatsNewResourceLoader() {
        return new ResourceLoadingSwingWorker(CHANGELOG_HTML);
    }

    private void switchToWelcome() {
        try {
            loadedResourceLatchMap.get(WELCOME_HTML).await();
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        setTitle("Welcome to " + AppName.getAppName());
        switchButton.setText("What's new in this release?");
        switchButton.setVisible(true);
        setSwitchActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                switchToWhatsNew();
            }
        });
        cardLayout.show(cardPanel, WELCOME_HTML);
    }

    private synchronized void setSwitchActionListener(final ActionListener listener) {
        if (switchActionListener != null) {
            switchButton.removeActionListener(switchActionListener);
        }
        switchActionListener = listener;
        switchButton.addActionListener(switchActionListener);
    }

    private SwingWorker addInitialSwitcher() {
        return new SwingWorker() {
            @Override
            public Object construct() {
                try {
                    if (welcome) {
                        loadedResourceLatchMap.get(WELCOME_HTML).await();
                    } else {
                        loadedResourceLatchMap.get(CHANGELOG_HTML).await();

                    }
                } catch (final InterruptedException e) {
                    // nothing
                }
                return null;
            }
            
            @SuppressWarnings("unchecked")
            public void finished() {
                if (welcome) {
                    switchToWelcome();
                } else {
                    switchToWhatsNew();
                }
            }
        };
    }
    private SwingWorker addOKCancelEnabler() {
        return new SwingWorker() {
            @Override
            public Object construct() {
                return null;
            }
            
            @SuppressWarnings("unchecked")
            public void finished() {
                enableButtons();
            }
        };
    }
    
    
    /**
     * Enable the ok/cancel buttons, when all the tabs have been loaded.
     */
    public void enableButtons() {
        assert SwingUtilities.isEventDispatchThread();
        
        switchButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }


    /**
     * OK was pressed; commit the collected changes and close the dialog.
     */
    public void okPressed() {
        assert SwingUtilities.isEventDispatchThread();
        clearAndHide();
    }
    
    /**
     * Cancel was pressed; just close the dialog.
     */
    public void cancelPressed() {
        assert SwingUtilities.isEventDispatchThread();
        clearAndHide();
    }

}
