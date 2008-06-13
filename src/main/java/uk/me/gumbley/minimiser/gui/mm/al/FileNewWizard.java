package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.Dimension;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

public class FileNewWizard {
    public static void startFileNewWizard() {
        Class[] wizardPages = new Class[] {
                //FileNewWizardIntroPage.class,
                //FileNewWizardChooseFolderPage.class,
                FileNewWizardSecurityOptionPage.class,
                FileNewWizardIntroPage.class,
        };
        Wizard wizard = WizardPage.createWizard(wizardPages, WizardResultProducer.NO_OP);
        wizard.show();
    }

    public static JPanel createNicelySizedPanel() {
        JPanel panel = new JPanel();
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.validate();
        panel.add(fileChooser);
        panel.validate();
        Dimension dim = panel.getPreferredSize();
        panel.setPreferredSize(dim);
        panel.remove(fileChooser);
        return panel;
    }
}
