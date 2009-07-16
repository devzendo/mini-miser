package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewResult;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardChooseFolderPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardIntroPage;
import uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardSecurityOptionPage;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * Triggers the start of the wizard from the File/New menu.
 * 
 * @author matt
 *
 */
public final class FileNewActionListener extends SnailActionListener {
    private final OpenDatabaseList mOpenDatabaseList;
    private final AccessFactory mAccessFactory;
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     * @param pluginRegistry the plugin registry
     */
    public FileNewActionListener(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager,
            final PluginRegistry pluginRegistry) {
        super(cursorManager);
        mOpenDatabaseList = openDatabaseList;
        mAccessFactory = accessFactory;
        mCursorManager = cursorManager;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        final WizardPage[] wizardPages = new WizardPage[] {
                new FileNewWizardIntroPage(mPluginRegistry),
                new FileNewWizardChooseFolderPage(mOpenDatabaseList),
                new FileNewWizardSecurityOptionPage(),
                // TODO add back in a later release new FileNewWizardCurrencyPage(),
                // rather, get other pages from the plugins...
        };
        final WizardResultProducer producer = new WizardResultProducer() {
            @SuppressWarnings("unchecked")
            public boolean cancel(final Map settings) {
                return true;
            }

            @SuppressWarnings("unchecked")
            public Object finish(final Map settings) throws WizardException {
                return new FileNewResult(mOpenDatabaseList,
                    mAccessFactory,
                    mCursorManager);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        // ... and on with the show...
        wizard.show();
    }
}
