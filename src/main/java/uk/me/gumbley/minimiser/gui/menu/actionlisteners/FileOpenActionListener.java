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
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;

/**
 * Triggers the start of the wizard from the File/Open menu.
 * 
 * Provides a number of branches:
 * A) The Intro and Database chooser branch
 * B) The check to see if the database is encrypted, initial entry and
 *    possible repeated re-entry of password with the option to cancel if you
 *    can't get the password right.
 * C) The check to see if migration is required, and the migration progress
 *    if necessary. 
 * 
 * You might occasionally need to create a dynamic wizard that chooses (based 
 * on user input) one of several alternative sequences of steps to execute after
 *  executing an initial sequence. For example, after executing the A-B sequence 
 *  (step A followed by step B), a dynamic wizard might need to choose between 
 *  executing the C-D-E sequence or the F-G sequence.

In its answer to a question on multiple branch points, Wizard's FAQ points out 
that a fixed sequence of steps is one wizard, each of several alternative 
wizards is a branch, and the step that leads to one of these branches is a 
branch point. Furthermore, it points out that the resulting wizard encompassing 
all of these wizards is a meta wizard.

Define a meta wizard by defining each branch via WizardPage or 
WizardPanelProvider, by subclassing the abstract 
org.netbeans.spi.wizard.WizardBranchController class for each branch point, by
 passing the branch leading to a branch point to the WizardBranchController 
 superclass via one of its constructors, and by overriding one of the following 
 methods in each subclass:

    * protected WizardPanelProvider getPanelProviderForStep(String step, Map settings)
    * protected Wizard getWizardForStep(String step, Map settings)

According to WizardBranchController's Javadoc, you can "override 
getPanelProviderForStep to provide instances of WizardPanelProvider if there 
are no subsequent branch points and the continuation is a simple wizard." 
Alternatively, you can "override getWizardForStep to return one or another
 wizard which represents the subsequent steps after a decision point."

The meta wizard tries to find a branch via getWizardForStep(). This method 
defaults to invoking getPanelProviderForStep(), which defaults to throwing an 
error. If you override either method, it should return null if step doesn't 
indicate a branch point (such as B in the earlier A-B sequence). Otherwise, the
 method uses settings to determine the appropriate branch, which is returned to
  the meta wizard. 
 * 
 * @author matt
 *
 */
public final class FileOpenActionListener implements ActionListener {
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileOpenActionListener(final OpenDatabaseList openDatabaseList,
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
        final Class[] wizardPages = new Class[] {
                FileOpenWizardIntroPage.class,
                FileOpenWizardChooseFolderPage.class,
//                FileNewWizardSecurityOptionPage.class,
//                FileNewWizardCurrencyPage.class,
        };
        final WizardResultProducer producer = new WizardResultProducer() {
            public boolean cancel(final Map settings) {
                return true;
            }

            public Object finish(final Map settings) throws WizardException {
                return new FileOpenResult(databaseList,
                    access,
                    cursorMan);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        wizard.show();
    }
}
