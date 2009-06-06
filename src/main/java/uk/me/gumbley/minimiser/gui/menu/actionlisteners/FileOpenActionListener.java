package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
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
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * Triggers the start of the wizard from the File/Open menu.
 * 
 * @author matt
 *
 */
public final class FileOpenActionListener extends SnailActionListener {
    private final OpenDatabaseList mOpenDatabaseList;
    private final CursorManager mCursorManager;
    private final Opener mOpener;
    private final PluginRegistry mPluginRegistry;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param cursorManager the cursor manager
     * @param opener the database opener
     * @param pluginRegistry the plugin registry
     */
    public FileOpenActionListener(final OpenDatabaseList openDatabaseList,
            final CursorManager cursorManager,
            final Opener opener,
            final PluginRegistry pluginRegistry) {
        super(cursorManager);
        mOpenDatabaseList = openDatabaseList;
        mCursorManager = cursorManager;
        mOpener = opener;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        final WizardPage[] wizardPages = new WizardPage[] {
                new FileOpenWizardIntroPage(mPluginRegistry),
                new FileOpenWizardChooseFolderPage(mOpenDatabaseList),
        };
        final WizardResultProducer producer = new WizardResultProducer() {
            @SuppressWarnings("unchecked")
            public boolean cancel(final Map settings) {
                return true;
            }

            @SuppressWarnings("unchecked")
            public Object finish(final Map settings) throws WizardException {
                return new FileOpenResult(mCursorManager, mOpener);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        // ... and on with the show...
        wizard.show();
    }
}
