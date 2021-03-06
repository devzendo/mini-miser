/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.menu.actionlisteners.filenew;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.wizard.MiniMiserWizardPage;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.persistence.DatabaseDirectoryValidator;
import org.netbeans.spi.wizard.WizardController;


/**
 * Choose an empty directory to hold the database.
 * Sets result: pathName 
 * 
 * @author matt
 *
 */
public final class FileNewWizardChooseFolderPage extends MiniMiserWizardPage {
    private static final long serialVersionUID = 8622934565584167141L;
    /**
     * The name of the key that's populated in the results map for the path
     * name of this database. 
     */
    public static final String PATH_NAME = "pathName";
    private static final Logger LOGGER = Logger
            .getLogger(FileNewWizardChooseFolderPage.class);
    private JFileChooser mFileChooser;
    private File mChosenDirectory;
    private JTextField mHiddenPathName;
    private final transient OpenDatabaseList mOpenDatabaseList;

    /**
     * Construct the wizard page
     * @param databaseList the open database that gets consulted if a database
     * is a possible open candidate, since you can't have the same db open twice.
     */
    public FileNewWizardChooseFolderPage(final OpenDatabaseList databaseList) {
        mOpenDatabaseList = databaseList;
        mChosenDirectory = null;
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
    @Override
    protected String validateContents(final Component component, final Object object) {
        final String validDirectory = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(mChosenDirectory);
        if (validDirectory != null) {
            return validDirectory;
        }
        final String dbName = mChosenDirectory.getName();
        if (mOpenDatabaseList.containsDatabase(new DatabaseDescriptor(dbName))) {
            return "A database called '" + dbName + "' is already open";
        }
        return null;
    }

    private void initComponents() {
        setLayout(new FlowLayout());
        
        final JPanel panel = createNicelySizedPanel();
        
        mFileChooser = new JFileChooser();
        mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mFileChooser.setMultiSelectionEnabled(false);
        mFileChooser.setControlButtonsAreShown(false);
        mFileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                LOGGER.info("Type: " + evt.getPropertyName());
                LOGGER.info(evt);
                if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    setChosenDirectory(mFileChooser.getSelectedFile());
                }
            }
        });
        panel.add(mFileChooser);
        
        // Add a hidden text field to receive the valid contents of the
        // file chooser, since those contents don't get populated in the
        // wizard's output map.
        mHiddenPathName = new JTextField();
        mHiddenPathName.setVisible(false);
        mHiddenPathName.setName(PATH_NAME);
        add(mHiddenPathName);
        
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
        if (chosenDir == null) {
            LOGGER.debug("chose null directory"); // happens on Mac
            return;
        }
        mChosenDirectory = chosenDir;
        LOGGER.info("Chosen directory:" + mChosenDirectory);
        final String problem = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(mChosenDirectory);
        if (problem != null) {
            LOGGER.warn(problem);
            setProblem(problem);
            setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
        } else {
            setProblem(null); //"The " + chosenDirectory.getName() + " folder can be used to hold the new database");
            setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
            mHiddenPathName.setText(mChosenDirectory.getAbsolutePath());
        }
    }
}
