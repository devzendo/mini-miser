package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.Dimension;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

public class FileNewWizard {
    public static void startFileNewWizard() {
        MiniMiserWizardPage.setLHGraphic();
        final Class[] wizardPages = new Class[] {
                FileNewWizardIntroPage.class,
                FileNewWizardChooseFolderPage.class,
                FileNewWizardSecurityOptionPage.class,
                //FileNewWizardIntroPage.class,
        };
        Wizard wizard = WizardPage.createWizard(wizardPages, WizardResultProducer.NO_OP);
        wizard.show();
    }
}
