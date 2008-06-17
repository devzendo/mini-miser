package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardController;

import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

/**
 * Choose an empty dirctory to hold the database.
 * Sets result: pathName 
 * 
 * @author matt
 *
 */
public class FileNewWizardChooseFolderPage extends MiniMiserWizardPage {
    public static final String PATH_NAME = "pathName";
    private static final long serialVersionUID = -2393755991521527684L;
    private static final Logger LOGGER = Logger
            .getLogger(FileNewWizardChooseFolderPage.class);
    private JFileChooser fileChooser;
    private File chosenDirectory;
    private JTextField hiddenPathName;

    /**
     * Construct the wizard page
     */
    public FileNewWizardChooseFolderPage() {
        chosenDirectory = null;
        initComponents();
    }
    
    /**
     * @return description for the left-hand area
     */
    public static String getDescription() {
        return "Choose new folder for database";
    }

    /**
     * {@inheritDoc}
     */
    protected String validateContents(final Component component, final Object object) {
        return validateGoodDirectory();
    }

    private String validateGoodDirectory() {
        LOGGER.info("validateGoodDirectory");
        if (chosenDirectory == null) {
            return "You must create a new folder to hold this database";
        }
        if (!chosenDirectory.exists()) {
            return "The '" + chosenDirectory.getName() + "' folder does not exist";
        }
        if (!chosenDirectory.isDirectory()) {
            return "'" + chosenDirectory.getName() + "' is not a folder";
        }
        int numFiles = chosenDirectory.list().length;
        if (numFiles > 0) {
            return "The folder must be empty - there are " + numFiles + " "
            + StringUtils.pluralise("file", numFiles)
            + " in the " + chosenDirectory.getName() + " folder";
        }
        return null;
    }
    
    private void initComponents() {
        setLayout(new FlowLayout());
        
        JPanel panel = createNicelySizedPanel();
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                LOGGER.info("Type: " + evt.getPropertyName());
                LOGGER.info(evt);
                if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    setChosenDirectory(fileChooser.getSelectedFile());
                }
            }
        });
        panel.add(fileChooser);
        
        // Add a hidden text field to receive the valid contents of the
        // file chooser, since those contents don't get populated in the
        // wizard's output map.
        hiddenPathName = new JTextField();
        hiddenPathName.setVisible(false);
        hiddenPathName.setName(PATH_NAME);
        add(hiddenPathName);
        
        panel.validate();
        add(panel);
    }
    
    /**
     * Set the chosen directory. Simple components added to the wizard (e.g.
     * JTextField, JCheckBox) will be monitored, and validateCOntents...) called
     * when they change. The Wizard doesn't monitor the JFileChooser, so wire
     * this method up to it, which calls the abstracted-out validation code,
     * and calls back into the wizard to set the problem text and forward
     * navigation mode.
     *  
     * @param chosenDir the file or directory chosen by the JFileChooser.
     */
    private void setChosenDirectory(final File chosenDir) {
        chosenDirectory = chosenDir;
        LOGGER.info("Chosen directory:" + chosenDirectory);
        final String problem = validateGoodDirectory();
        if (problem != null) {
            LOGGER.warn(problem);
            setProblem(problem);
            setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
        } else {
            setProblem(null); //"The " + chosenDirectory.getName() + " folder can be used to hold the new database");
            setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
            hiddenPathName.setText(chosenDirectory.getAbsolutePath());
        }
    }
}
