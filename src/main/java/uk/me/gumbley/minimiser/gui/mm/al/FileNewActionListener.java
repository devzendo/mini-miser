package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.string.StringUtils;
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
public class FileNewActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileNewActionListener.class);
    private final WorkerPool pool;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;

    public FileNewActionListener(final WorkerPool workerPool, final OpenDatabaseList openDatabaseList, final AccessFactory accessFactory) {
        super();
        this.pool = workerPool;
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        final FileNewParameters params = FileNewWizard.startFileNewWizard();
        // TODO no, make the database creation and addition tasks poke a progress bar
        final Runnable task = new Runnable() {
            public void run() {
                LOGGER.info("Creating database at " + params.getPath());
                // need the db path to be /path/to/db/databasename/databasename
                // i.e. duplicate the directory component at the end
                File path = new File(params.getPath());
                String dbName = path.getName();
                String fullPath = StringUtils.slashTerminate(path.getAbsolutePath()) + dbName;
                LOGGER.info("Final db path is " + fullPath);
                final MiniMiserDatabase database = access.createDatabase(fullPath, params.isEncrypted() ? params.getPassword() : "");
                LOGGER.info("Database created; adding to open database list");
                // TODO the MiniMiserDatabase is now lost - need to add it
                // to the DatabaseDescriptor.
                databaseList.addOpenedDatabase(new DatabaseDescriptor(dbName));
            }
        };
        pool.submit(task);
    }
    
}
