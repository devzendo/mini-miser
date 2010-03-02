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

package org.devzendo.minimiser.gui.menu.actionlisteners.fileopen;

import java.awt.Frame;
import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.opener.AbstractOpenerAdapter;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;


/**
 * The worker for opening databases when the File|Open wizard has finished.
 * @author matt
 *
 */
public final class FileOpenResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileOpenResult.class);
    private final Opener dbOpener;
    private CursorManager cursor;

    /**
     * Create the FileOpenResult creation worker
     * @param cursorManager the cursor manager
     * @param opener the database opener
     */
    public FileOpenResult(final CursorManager cursorManager, final Opener opener) {
        this.cursor = cursorManager;
        this.dbOpener = opener;
    }
    
    private final class FileOpenWizardOpenerAdapter extends AbstractOpenerAdapter {
        private final ResultProgressHandle progressHandle;

        /**
         * The wizard-based OpenerAdapter
         * @param parentFrame the parent frame that dialogs would be displayed over
         * @param name the name of the database, for display purposes on error 
         * @param cursorMgr the CursorManager
         * @param progress the wizard's progress handle
         */
        public FileOpenWizardOpenerAdapter(final Frame parentFrame,
                final String name,
                final CursorManager cursorMgr,
                final ResultProgressHandle progress) {
            super(parentFrame, name, cursorMgr);
            this.progressHandle = progress;
        }

        /**
         * {@inheritDoc}
         */
        public void reportProgress(final ProgressStage progressStage, final String description) {
            progressHandle.setProgress(description, progressStage.getValue(), progressStage.getMaximumValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(final Map map, final ResultProgressHandle progress) {
        final Map<String, Object> result = (Map<String, Object>) map;
        if (result == null) {
            LOGGER.info("User cancelled File|Open");
            return;
        }
        
        final String dbPath = (String) result.get(FileOpenWizardChooseFolderPage.PATH_NAME);
        // dbPath is the path to the directory - need to add the name of the directory
        // to the end, as that's the db name
        final File dbPathFile = new File(dbPath);
        final String dbName = dbPathFile.getName();
        final String dbFullPath = StringUtils.slashTerminate(dbPath) + dbName;

        // TODO pass the wizard's frame in here
        final OpenerAdapter openerAdapter = new FileOpenWizardOpenerAdapter(null, dbName, cursor, progress);
        dbOpener.openDatabase(dbName, dbFullPath, openerAdapter);

        // A small delay to allow the user to notice the
        // Opened OK progress result - otherwise the wizard just
        // vanishes a little too abruptly.
        ThreadUtils.waitNoInterruption(250);
        progress.finished(null);
    }
}
