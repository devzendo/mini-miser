package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.util.WorkerPool;

/**
 * Triggers the start of the wizard from the File/New menu.
 * 
 * @author matt
 *
 */
public final class FileNewActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileNewActionListener.class);
    private final WorkerPool pool;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Construct the listener
     * @param workerPool the worker pool singleton
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileNewActionListener(final WorkerPool workerPool,
            final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        super();
        this.pool = workerPool;
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        final Class[] wizardPages = new Class[] {
                FileNewWizardIntroPage.class,
                FileNewWizardChooseFolderPage.class,
                FileNewWizardSecurityOptionPage.class,
                FileNewWizardCurrencyPage.class,
        };
        WizardResultProducer producer = new FileNewProducer(pool,
            databaseList,
            access,
            cursorMan);
        Wizard wizard = WizardPage.createWizard(wizardPages, producer); //WizardResultProducer.NO_OP);
        wizard.show();
        
        
        //final FileNewParameters params = FileNewWizard.startFileNewWizard();
/*
        if (params == null) {
            LOGGER.info("User cancelled File|New");
            return;
        }
        cursorMan.hourglass();
        // WOZERE also need a status bar informing user of current activity?


        // TODO no, make the database creation and addition tasks poke a progress bar
        final Runnable task = new Runnable() {
            public void run() {
                LOGGER.info("Creating database at " + params.getPath());
                // need the db path to be /path/to/db/databasename/databasename
                // i.e. duplicate the directory component at the end
                final File path = new File(params.getPath());
                final String dbName = path.getName();
                final String fullPath = StringUtils.slashTerminate(path.getAbsolutePath()) + dbName;
                LOGGER.info("Final db path is " + fullPath);
                final MiniMiserDatabase database = access.createDatabase(fullPath, params.isEncrypted() ? params.getPassword() : "");

                final Runnable swingTask = new Runnable() {
                    public void run() {
                        LOGGER.info("Database created; adding to open database list");
                        // TODO the MiniMiserDatabase is now lost - need to add it
                        // to the DatabaseDescriptor.
                        databaseList.addOpenedDatabase(new DatabaseDescriptor(dbName));
                        cursorMan.normal();
                    }
                };
                GUIUtils.runOnEventThread(swingTask);
            }
        };
        pool.submit(task);
        
        */
    }
}
