package org.devzendo.minimiser.plugin.facade.newdatabase;

import java.util.List;

import org.netbeans.spi.wizard.WizardPage;

/**
 * Facade provided to allow the File|New wizard to be populated
 * with plugin-supplied pages.
 * 
 * @author matt
 *
 */
public interface NewDatabaseWizardPagesFacade {
    /**
     * Obtain any plugin-specific wizard pages that are shown at
     * the end of the framework-supplied pages (database location,
     * encryption details).
     * 
     * @return a list of wizard pages, can be empty or null
     */
    List<WizardPage> getWizardPages();
}
