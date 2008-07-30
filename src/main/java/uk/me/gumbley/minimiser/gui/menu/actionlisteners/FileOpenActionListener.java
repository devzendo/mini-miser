package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen.FileOpenResult;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen.FileOpenWizardChooseFolderPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen.FileOpenWizardIntroPage;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Triggers the start of the wizard from the File/Open menu.
 * 
 * @author matt
 *
 */
public final class FileOpenActionListener implements ActionListener {
    private final OpenDatabaseList databaseList;
    private final CursorManager cursor;
    private final Opener dbOpener;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param cursorManager the cursor manager
     * @param opener the database opener
     */
    public FileOpenActionListener(final OpenDatabaseList openDatabaseList,
            final CursorManager cursorManager,
            final Opener opener) {
        super();
        this.databaseList = openDatabaseList;
        this.cursor = cursorManager;
        this.dbOpener = opener;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        final WizardPage[] wizardPages = new WizardPage[] {
                new FileOpenWizardIntroPage(),
                new FileOpenWizardChooseFolderPage(databaseList),
        };
        final WizardResultProducer producer = new WizardResultProducer() {
            @SuppressWarnings("unchecked")
            public boolean cancel(final Map settings) {
                return true;
            }

            @SuppressWarnings("unchecked")
            public Object finish(final Map settings) throws WizardException {
                return new FileOpenResult(cursor, dbOpener);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        wizard.show();
    }
}
