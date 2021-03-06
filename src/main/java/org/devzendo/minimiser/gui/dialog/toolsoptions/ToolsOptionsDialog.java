/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.awt.Container;
import java.awt.Frame;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commonapp.gui.SwingWorker;
import org.devzendo.commonapp.gui.dialog.snaildialog.AbstractSnailDialog;
import org.devzendo.minimiser.prefs.ChangeCollectingMiniMiserPrefs;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;


/**
 * A modal Tools -> Options dialog.
 * @author matt
 *
 */
public final class ToolsOptionsDialog extends AbstractSnailDialog {
    private static final Logger LOGGER = Logger
            .getLogger(ToolsOptionsDialog.class);
    private static final long serialVersionUID = 1685823419193743569L;
    private final MiniMiserPrefs mPrefs;
    private final ToolsOptionsTabFactory toolsOptionsTabFactory;

    private transient ChangeCollectingMiniMiserPrefs mChangeCollectingPrefs;
    private ToolsOptionsDialogPanel mToolsOptionsDialogPanel;

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
            final MiniMiserPrefs preferences, final ToolsOptionsTabFactory tabFactory) {
        super(parentFrame, cursor, "Options");
        mPrefs = preferences;
        toolsOptionsTabFactory = tabFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container createMainComponent() {
        mToolsOptionsDialogPanel = new ToolsOptionsDialogPanel(this);
        return mToolsOptionsDialogPanel;
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
                mChangeCollectingPrefs.commit();
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
                mChangeCollectingPrefs = new ChangeCollectingMiniMiserPrefs(mPrefs);
                return toolsOptionsTabFactory.loadTabs(mChangeCollectingPrefs);
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public void finished() {
               final List<ToolsOptionsTab> loadedTabs = (List<ToolsOptionsTab>) get();
               for (final ToolsOptionsTab tab : loadedTabs) {
                   mToolsOptionsDialogPanel.addTab(tab.getName(), tab.getComponent());
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
                mToolsOptionsDialogPanel.enableButtons();
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
            final CursorManager cursorManager, final MiniMiserPrefs prefs,
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
