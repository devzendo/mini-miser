package uk.me.gumbley.minimiser.gui.mm.al;

import java.util.Map;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.util.WorkerPool;

public class FileNewProducer implements WizardResultProducer {
    private final WorkerPool pool;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    public FileNewProducer(final WorkerPool workerPool,
            final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        this.pool = workerPool;
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }

    public boolean cancel(final Map settings) {
        return true; // allow user to cancel
    }

    public Object finish(final Map settings) throws WizardException {
        return new FileNewResult(pool,
            databaseList,
            access,
            cursorMan);
    }
}
