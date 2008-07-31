package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.awt.Frame;
import java.io.File;
import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.opener.AbstractOpenerAdapter;
import uk.me.gumbley.minimiser.opener.IOpener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;

/**
 * The worker for opening databases when the File|Open wizard has finished.
 * @author matt
 *
 */
public final class FileOpenResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileOpenResult.class);
    private final IOpener dbOpener;
    private CursorManager cursor;

    /**
     * Create the FileOpenResult creation worker
     * @param cursorManager the cursor manager
     * @param opener the database opener
     */
    public FileOpenResult(final CursorManager cursorManager, final IOpener opener) {
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
