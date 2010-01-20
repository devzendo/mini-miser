package org.devzendo.minimiser.plugin.facade.newdatabase;

/**
 * Plugins that want to customise the File|New wizard must
 * implement this and provide the facade.
 * 
 * @author matt
 *
 */
public interface NewDatabaseWizardPages {
    /**
     * @return an instance of the facade
     */
    NewDatabaseWizardPagesFacade getNewDatabaseWizardPagesFacade();
}
