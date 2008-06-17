package uk.me.gumbley.minimiser.gui.mm.al;

import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

public class FileNewWizard {
    private static final Logger LOGGER = Logger.getLogger(FileNewWizard.class);
    public static FileNewParameters startFileNewWizard() {
        MiniMiserWizardPage.setLHGraphic();
        final Class[] wizardPages = new Class[] {
                FileNewWizardIntroPage.class,
                FileNewWizardChooseFolderPage.class,
                FileNewWizardSecurityOptionPage.class,
                FileNewWizardCurrencyPage.class,
        };
        Wizard wizard = WizardPage.createWizard(wizardPages, WizardResultProducer.NO_OP);
        final Map<String, Object> result = (Map<String, Object>) WizardDisplayer.showWizard(wizard);
        LOGGER.info("Result: " + result);
        for (String key : result.keySet()) {
            Object obj = result.get(key);
            LOGGER.info("key " + key + " value '" + obj + "' type " + obj.getClass().getSimpleName());
            
        }
        FileNewParameters params = new FileNewParameters(
            (String) result.get(FileNewWizardChooseFolderPage.PATH_NAME),
            (Boolean) result.get(FileNewWizardSecurityOptionPage.ENCRYPTED),
            (String) result.get(FileNewWizardSecurityOptionPage.PASSWORD),
            (String) result.get(FileNewWizardCurrencyPage.CURRENCY));
        return params;
    }
}
