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

import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.snaildialog.AbstractSnailDialog;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.updatechecker.DefaultChangeLogTransformer;
import uk.me.gumbley.minimiser.updatechecker.ParseException;

/**
 * A dialog that shows a card layout as the main component, initially containing
 * the welcome text, with a button that switches this text to the what's new
 * text (the button then allows reversion to the welcome text).
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class WelcomeDialog extends AbstractSnailDialog {
    private static final String WELCOME_HTML = "welcome.html";
    private static final String CHANGELOG_HTML = "*changelog.html*";
    private static final String BLANK_PANEL_NAME = "*special*blank*panel*";
    private static final int TEXTPANE_WIDTH = 550;
    private static final int TEXTPANE_HEIGHT = 350;
    private JButton switchButton;
    private final boolean mWelcome;
    private final AppDetails mAppDetails;
    private final Map<String, CountDownLatch> loadedResourceLatchMap;
    private JButton cancelButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ActionListener switchActionListener;

    /**
     * Construct the Welcome Dialog
     * @param parentFrame the main app frame
     * @param cursor the cursor manager
     * @param appDetails the application details
     * @param showWelcome true iff showing the welcome screen initally, false
     * for the what's new screen.
     */
    public WelcomeDialog(final Frame parentFrame,
            final CursorManager cursor,
            final AppDetails appDetails,
            final boolean showWelcome) {
        super(parentFrame, cursor, "Loading...");
        mAppDetails = appDetails;
        mWelcome = showWelcome;
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
        if (mWelcome) {
            addSwingWorker(addWelcomeResourceLoader());
            addSwingWorker(addWhatsNewResourceLoader());
        } else {
            addSwingWorker(addWhatsNewResourceLoader());
            addSwingWorker(addWelcomeResourceLoader());
        }
        addSwingWorker(addInitialSwitcher());
        addSwingWorker(addOKCancelEnabler());
    }
    
    private abstract class HTMLDisplayingSwingWorker extends SwingWorker {
        private final String resource;

        public HTMLDisplayingSwingWorker(final String resourceName) {
            this.resource = resourceName;
        }

        protected String getResource() {
            return resource;
        }
        
        @Override
        public abstract Object construct();
        
        @Override
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
    
    private final class ResourceLoadingSwingWorker extends HTMLDisplayingSwingWorker {
        public ResourceLoadingSwingWorker(final String resourceName) {
            super(resourceName);
        }
        
        @Override
        public Object construct() {
            assert (!EventQueue.isDispatchThread());
            final StringBuilder text = new StringBuilder();
            ResourceLoader.readResource(text, getResource());
            return text.toString();
        }
    }

    private final class ChangeLogTransformingSwingWorker extends HTMLDisplayingSwingWorker {
        public ChangeLogTransformingSwingWorker(final String resourceName) {
            super(resourceName);
        }
        
        @Override
        public Object construct() {
            assert (!EventQueue.isDispatchThread());
            final InputStream resourceAsStream = Thread.currentThread().
                getContextClassLoader().
                getResourceAsStream("changelog.txt");
            final DefaultChangeLogTransformer transformer = new DefaultChangeLogTransformer(); // TODO WOZERE inject this!!
            try {
                final String transformedChangeLog = transformer.readAllStream(resourceAsStream);
                return transformedChangeLog;
            } catch (final IOException e) {
                return ("Could not read change log: " + e.getMessage());
            } catch (final ParseException e) {
                return ("Could not parse change log: " + e.getMessage());
            }
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
        switchButton.setText("Welcome to " + mAppDetails.getApplicationName());
        switchButton.setVisible(true);
        setSwitchActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                switchToWelcome();
            }
        });
        cardLayout.show(cardPanel, CHANGELOG_HTML);
    }

    private SwingWorker addWhatsNewResourceLoader() {
        return new ChangeLogTransformingSwingWorker(CHANGELOG_HTML);
    }

    private void switchToWelcome() {
        try {
            loadedResourceLatchMap.get(WELCOME_HTML).await();
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        setTitle("Welcome to " + mAppDetails.getApplicationName());
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
                    if (mWelcome) {
                        loadedResourceLatchMap.get(WELCOME_HTML).await();
                    } else {
                        loadedResourceLatchMap.get(CHANGELOG_HTML).await();

                    }
                } catch (final InterruptedException e) {
                    // nothing
                }
                return null;
            }
            
            @Override
            public void finished() {
                if (mWelcome) {
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
            
            @Override
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
