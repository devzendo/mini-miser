package uk.me.gumbley.minimiser.gui.dialog.toolsoptions;

import java.awt.Container;
import java.awt.Frame;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.snaildialog.AbstractSnailDialog;
import uk.me.gumbley.minimiser.prefs.ChangeCollectingPrefs;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A modal Tools -> Options dialog.
 * @author matt
 *
 */
public final class ToolsOptionsDialog extends AbstractSnailDialog {
    private static final Logger LOGGER = Logger
            .getLogger(ToolsOptionsDialog.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1685823419193743569L;
    private final Prefs prefs;
    private final ToolsOptionsTabFactory toolsOptionsTabFactory;

    private ChangeCollectingPrefs ccp;
    private ToolsOptionsDialogPanel toolsOptionsDialogPanel;

    /**
     * Create the Tools -> Options Dialog
     * @param parentFrame the parent frame, over which this modal dialog
     * will be presented
     * @param cursor the cursor manager, to show an hourglass whilst
     * the individual panels load
     * @param preferences the preferences, where choices made in this dialog will be
     * stored.
     * @param tabFactory the Tools->Options Tab Factory
     */
    public ToolsOptionsDialog(final Frame parentFrame, final CursorManager cursor,
            final Prefs preferences, final ToolsOptionsTabFactory tabFactory) {
        super(parentFrame, cursor, "Options");
        prefs = preferences;
        toolsOptionsTabFactory = tabFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container createMainComponent() {
        toolsOptionsDialogPanel = new ToolsOptionsDialogPanel(this);
        return toolsOptionsDialogPanel;
    }

    /**
     * OK was pressed; commit the collected changes and close the dialog.
     */
    public void okPressed() {
        assert SwingUtilities.isEventDispatchThread();

        clearAndHide();
        
        final SwingWorker okWorker = new SwingWorker() {
            @Override
            public Object construct() {
                Thread.currentThread().setName("ToolsOptions OK Worker");
                LOGGER.info("Committing Tools->Options changes");
                ccp.commit();
                return null;
            }
        };
        okWorker.start();
    }
    
    /**
     * Cancel was pressed; just close the dialog.
     */
    public void cancelPressed() {
        assert SwingUtilities.isEventDispatchThread();
        LOGGER.info("Cancelling Tools->Options changes");
        clearAndHide();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialise() {
        addSwingWorker(addTabFactorySetup());
        addSwingWorker(addOKCancelEnabler());
    }
    
    private SwingWorker addTabFactorySetup() {
        return new SwingWorker() {
            @Override
            public Object construct() {
                ccp = new ChangeCollectingPrefs(prefs);
                return toolsOptionsTabFactory.loadTabs(ccp);
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public void finished() {
               final List<ToolsOptionsTab> loadedTabs = (List<ToolsOptionsTab>) get();
               for (final ToolsOptionsTab tab : loadedTabs) {
                   toolsOptionsDialogPanel.addTab(tab.getName(), tab.getComponent());
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
                toolsOptionsDialogPanel.enableButtons();
            }
        };
    }
    
    /**
     * Create a Tools -> Options dialog
     * @param parentFrame the parent frame, over which this modal dialog
     * will be presented
     * @param cursorManager the cursor manager, to show an hourglass whilst
     * the individual panels load
     * @param prefs the preferences, where choices made in this dialog will be
     * stored.
     * @param tabFactory the Tab Factory
     */
    public static void showOptions(final Frame parentFrame,
            final CursorManager cursorManager, final Prefs prefs,
            final ToolsOptionsTabFactory tabFactory) {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final ToolsOptionsDialog dialog =
                    new ToolsOptionsDialog(parentFrame, cursorManager,
                                           prefs, tabFactory);
                dialog.postConstruct();
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
        
    }
}
