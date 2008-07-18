package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewResult;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardChooseFolderPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardCurrencyPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardIntroPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardSecurityOptionPage;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;

/**
 * Triggers the start of the wizard from the File/New menu.
 * 
 * @author matt
 *
 */
public final class FileNewActionListener implements ActionListener {
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileNewActionListener(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        super();
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        final WizardPage[] wizardPages = new WizardPage[] {
                new FileNewWizardIntroPage(),
                new FileNewWizardChooseFolderPage(databaseList),
                new FileNewWizardSecurityOptionPage(),
                new FileNewWizardCurrencyPage(),
        };
        final WizardResultProducer producer = new WizardResultProducer() {
            public boolean cancel(final Map settings) {
                return true;
            }

            public Object finish(final Map settings) throws WizardException {
                return new FileNewResult(databaseList,
                    access,
                    cursorMan);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        wizard.show();
    }
}
