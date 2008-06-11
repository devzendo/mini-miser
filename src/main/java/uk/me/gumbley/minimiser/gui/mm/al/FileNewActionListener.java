package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

public class FileNewActionListener implements ActionListener {
    public void actionPerformed(final ActionEvent e) {
        Class[] wizardPages = new Class[] {
                FileNewWizardPage.class,
        };
        Wizard wizard = WizardPage.createWizard(wizardPages, WizardResultProducer.NO_OP);
        wizard.show();
    }
}
