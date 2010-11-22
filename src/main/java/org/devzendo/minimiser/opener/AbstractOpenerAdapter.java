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

package org.devzendo.minimiser.opener;

import java.awt.Frame;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commonapp.gui.GUIValueObtainer;
import org.devzendo.minimiser.gui.dialog.passwordentry.PasswordEntryDialogHelper;
import org.devzendo.minimiser.gui.dialog.problem.ProblemDialogHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;


/**
 * Provides the common app-wide facilities of an OpenerAdapter:
 * <ul>
 * <li> hourglass cursor on start
 * <li> PasswordEntryDialogHelper used for password prompts
 * <li> JOptionPane display of migration prompt
 * <li> JOptionPane display of database not found
 * <li> ProblemDialog display of serious problems
 * <li> normal cursor on stop
 * </ul>
 * 
 * @author matt
 *
 */
public abstract class AbstractOpenerAdapter implements OpenerAdapter {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractOpenerAdapter.class);
    private final Frame parentFrame;
    private final String dbName;
    private final CursorManager cursorManager;

    /**
     * Construct the AbstractOpenerAdapter
     * @param frame the parent frame over which any dialogs would be displayed
     * @param databaseName the name of the database to open
     * @param cursorMgr the cursor manager
     */
    public AbstractOpenerAdapter(final Frame frame, final String databaseName, final CursorManager cursorMgr) {
        this.parentFrame = frame;
        this.dbName = databaseName;
        this.cursorManager = cursorMgr;
    }

    /**
     * {@inheritDoc}
     */
    public final String requestPassword() {
        final PasswordEntryDialogHelper passwordHelper = new PasswordEntryDialogHelper();
        return passwordHelper.promptForPassword(parentFrame, dbName);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean requestMigration() {
        final GUIValueObtainer<Boolean> obtainer = new GUIValueObtainer<Boolean>();
        try {
            return obtainer.obtainFromEventThread(new Callable<Boolean>() {

                public Boolean call() throws Exception {
                    final int opt = JOptionPane.showConfirmDialog(parentFrame,
                        "This database was created by an older version\n"
                        + "of the software, and must be upgraded before\n"
                        + "it can be used by this version.\n\n"
                        + "After upgrading, you will no longer be able\n"
                        + "to open it in older versions of the software.\n"
                        + "Upgrading cannot be undone.\n\n"
                        + "Choose Yes to upgrade the database, or\n"
                        + "No to leave the database alone and not open it",
                        "Upgrade database?",
                        JOptionPane.YES_NO_OPTION);
                    return opt == 0;
                }
                
            });
        } catch (final Exception e) {
            // it has been logged by the GUIValueObtainer
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void migrationNotPossible() {
        LOGGER.warn("Could not migrate database '" + dbName + "'");
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame,
                    "Could not open database '" + dbName + "'.\n"
                    + "It was created by a more recent version of\n"
                    + "the software. You must upgrade your software\n"
                    + "in order to open this database.", 
                    "Could not open database '" + dbName + "'",
                    JOptionPane.OK_OPTION);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public final void createdByOtherApplication() {
        LOGGER.warn("Could not open database '" + dbName + "' created by another application");
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame,
                    "Could not open database '" + dbName + "'.\n"
                    + "It was created by another application.", 
                    "Could not open database '" + dbName + "'",
                    JOptionPane.OK_OPTION);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public final void noApplicationPluginAvailable() {
        LOGGER.warn("Could not open database '" + dbName + "' since no application is loaded");
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame,
                    "Could not open database '" + dbName + "'.\n"
                    + "No application plugin is present.", 
                    "Could not open database '" + dbName + "'",
                    JOptionPane.OK_OPTION);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public final void migrationFailed(final DataAccessException exception) {
        LOGGER.warn("Migration failed with data access exception: " + exception.getMessage(), exception);
        ProblemDialogHelper.reportProblem("trying to upgrade database '" + dbName + "'.", exception);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void startOpening() {
        cursorManager.hourglassViaEventThread(this.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    public final void databaseNotFound(final DataAccessResourceFailureException exception) {
        LOGGER.warn("Could not open database '" + dbName + "': " + exception.getMessage());
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame,
                    "Could not open database '" + dbName + "'\n" + exception.getMessage(),
                    "Could not open database '" + dbName + "'",
                    JOptionPane.OK_OPTION);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public final void seriousProblemOccurred(final DataAccessException exception) {
        LOGGER.warn("Data access exception: " + exception.getMessage(), exception);
        ProblemDialogHelper.reportProblem("trying to open database '" + dbName + "'.", exception);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void stopOpening() {
        cursorManager.normalViaEventThread(this.getClass().getSimpleName());
    }
}
