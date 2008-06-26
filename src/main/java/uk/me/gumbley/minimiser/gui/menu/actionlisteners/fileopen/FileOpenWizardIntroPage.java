package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

/**
 * Tell the user about database opening.
 * @author matt
 *
 */
public final class FileOpenWizardIntroPage extends MiniMiserWizardPage {
    private static final long serialVersionUID = -1951314726620966608L;

    /**
     * Create an intro page. 
     */
    public FileOpenWizardIntroPage() {
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
              "You are about to open an existing " + AppName.getAppName() + " database.\n\n"
              
            + "Databases comprise several files that are kept together in their own folder.\n"
            + "Together, these files hold all of your account information.\n\n"
            
            + "To open the database, just choose its folder.\n\n"
            
            + "If the database was created by an earlier version of " + AppName.getAppName() + ",\n" 
            + "it may be necessary to convert it into the current format. This conversion cannot be\n"
            + "undone, and after conversion, the database may not be usable by earlier versions\n"
            + "of " + AppName.getAppName() + ".\n\n"
            
            + "Press 'Next>' to choose the database's folder.\n\n"
            
            + "Or, press 'Cancel' to abandon the opening of an existing database.";
    }

    /**
     * @return wizard page description for the LH area
     */
    public static String getDescription() {
        return "About " + AppName.getAppName() + " databases";
    }

}
