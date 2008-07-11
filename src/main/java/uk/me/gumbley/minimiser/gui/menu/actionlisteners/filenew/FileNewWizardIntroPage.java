package uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

/**
 * Tell the user about databases.
 * @author matt
 *
 */
public final class FileNewWizardIntroPage extends MiniMiserWizardPage {
    private static final long serialVersionUID = 3743607043316984644L;
    /**
     * Create an intro page. 
     */
    public FileNewWizardIntroPage() {
        initComponents();
    }
    
    private void initComponents() {
        final JPanel sizedPanel = createNicelySizedPanel();
        sizedPanel.setLayout(new BorderLayout());
        final JTextArea textArea = new JTextArea(getText());
        textArea.setEditable(false);
        sizedPanel.add(textArea, BorderLayout.WEST);
        add(sizedPanel);
    }
    
    private String getText() {
        return
              "You are about to create a new empty " + AppName.getAppName() + " database.\n\n"
              
            + "Databases comprise several files that are kept together in their own folder.\n"
            + "Together, these files hold all of your account information.\n\n"
            
            + "The folder name you choose will also be used in the database file names.\n"
            + "Please choose a meaningful, unique name! You cannot open multiple databases\n"
            + "with the same name, even if in different folders.\n\n"
            
            + "Once created, an empty database can be used immediately, or data from other\n"
            + "personal finance software can be imported into it.\n\n"
            
            + "Databases can be optionally encrypted for security. " + AppName.getAppName() + " uses AES-256\n"
            + "(Advanced Encryption System) to ensure your information is secure.\n"
            + "If encrypted, the password must be supplied when opening the database.\n"
            + "Don't lose the password, as there is no way to retrieve it or break the encryption!\n\n\n"
            
            + "Press 'Next>' to choose or create the folder that will hold your new database.\n\n"
            
            + "Or, press 'Cancel' to abandon the creation of a new database.";
    }

    /**
     * @return wizard page description for the LH area
     */
    public static String getDescription() {
        return "About " + AppName.getAppName() + " databases";
    }

}
