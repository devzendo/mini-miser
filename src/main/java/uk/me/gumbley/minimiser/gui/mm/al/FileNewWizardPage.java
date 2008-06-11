package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardPage;

public class FileNewWizardPage extends WizardPage {
    private static final Logger LOGGER = Logger
            .getLogger(FileNewWizardPage.class);
    private JFileChooser fileChooser;
    private File chosenDirectory;

    public FileNewWizardPage() {
        chosenDirectory = null;
        initComponents();
    }
    
    public static String getDescription() {
        return "Choose new folder for database";
    }

    protected String validateContents(final Component component, final Object object) {
        if (chosenDirectory == null) {
            return "You must create a new folder to hold this database";
        }
        if (!chosenDirectory.exists()) {
            return "The '" + chosenDirectory.getName() + "' folder does not exist";
        }
        if (!chosenDirectory.isDirectory()) {
            return "'" + chosenDirectory.getName() + "' is not a folder";
        }
        return null;
    }
    
    private void initComponents() {
        setLayout(new FlowLayout());
        
        fileChooser = new JFileChooser();
        fileChooser.setName("chooser");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setApproveButtonText("Create");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                LOGGER.info(evt.getPropertyName());
                if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    chosenDirectory = fileChooser.getSelectedFile();
                }
            }});
        add(fileChooser);
    }
}
