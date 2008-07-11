package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;
import uk.me.gumbley.minimiser.persistence.DatabaseDirectoryValidator;

/**
 * Choose an existing directory that holds a database.
 * Sets result: pathName 
 * 
 * @author matt
 *
 */
public final class FileOpenWizardChooseFolderPage extends MiniMiserWizardPage {
    private static final long serialVersionUID = -8420603958193604163L;
    /**
     * The name of the key that's populated in the results map for the path
     * name of this database. 
     */
    public static final String PATH_NAME = "pathName";
    private static final Logger LOGGER = Logger
            .getLogger(FileOpenWizardChooseFolderPage.class);
    private JFileChooser fileChooser;
    private File chosenDirectory;
    private JTextField hiddenPathName;
    private final OpenDatabaseList openDatabaseList;

    /**
     * Construct the wizard page
     * @param databaseList the open database that gets consulted if a database
     * is a possible open candidate, since you can't have the same db open twice.
     */
    public FileOpenWizardChooseFolderPage(final OpenDatabaseList databaseList) {
        openDatabaseList = databaseList;
        chosenDirectory = null;
        initComponents();
    }
    
    /**
     * @return description for the left-hand area
     */
    public static String getDescription() {
        return "Choose existing database folder";
    }

    /**
     * {@inheritDoc}
     */
    protected String validateContents(final Component component, final Object object) {
        LOGGER.debug("validateContents called for chosenDirectory " + chosenDirectory);
        final String validDirectory = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(chosenDirectory);
        if (validDirectory != null) {
            return validDirectory;
        }
        final String dbName = chosenDirectory.getName();
        if (openDatabaseList.containsDatabase(new DatabaseDescriptor(dbName))) {
            return "A database called '" + dbName + "' is already open";
        }
        return null;
    }

    private void initComponents() {
        setLayout(new FlowLayout());
        
        // Add a hidden text field to receive the valid contents of the
        // file chooser, since those contents don't get populated in the
        // wizard's output map.
        hiddenPathName = new JTextField();
        hiddenPathName.setVisible(false);
        hiddenPathName.setName(PATH_NAME);
        add(hiddenPathName);

        final JPanel panel = createNicelySizedPanel();
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    final File selectedFile = fileChooser.getSelectedFile();
                    LOGGER.debug("propertyChange gave me: " + selectedFile);
                    chosenDirectory = selectedFile;
                    hiddenPathName.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        panel.add(fileChooser);
        add(fileChooser);
        
        
        panel.validate();
        add(panel);
    }
}
