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

package org.devzendo.minimiser.gui.menu.actionlisteners.fileopen;

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
    private JFileChooser mFileChooser;
    private File mChosenDirectory;
    private JTextField mHiddenPathName;
    private final transient OpenDatabaseList mOpenDatabaseList;

    /**
     * Construct the wizard page
     * @param databaseList the open database that gets consulted if a database
     * is a possible open candidate, since you can't have the same db open twice.
     */
    public FileOpenWizardChooseFolderPage(final OpenDatabaseList databaseList) {
        mOpenDatabaseList = databaseList;
        mChosenDirectory = null;
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
    @Override
    protected String validateContents(final Component component, final Object object) {
        LOGGER.debug("validateContents called for chosenDirectory " + mChosenDirectory);
        final String validDirectory = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(mChosenDirectory);
        if (validDirectory != null) {
            LOGGER.debug("Not valid for open: " + validDirectory);
            return validDirectory;
        }
        final String dbName = mChosenDirectory.getName();
        LOGGER.debug("valid database '" + dbName + "', checking ODL to see if it's already open");
        LOGGER.debug("Open DBs: " + mOpenDatabaseList.getOpenDatabases());
        if (mOpenDatabaseList.containsDatabase(new DatabaseDescriptor(dbName))) {
            LOGGER.debug("it is already open");
            return "A database called '" + dbName + "' is already open";
        }
        LOGGER.debug("It is not open already - allowing it");
        return null;
    }

    private void initComponents() {
        setLayout(new FlowLayout());
        
        // Add a hidden text field to receive the valid contents of the
        // file chooser, since those contents don't get populated in the
        // wizard's output map.
        mHiddenPathName = new JTextField();
        mHiddenPathName.setVisible(false);
        mHiddenPathName.setName(PATH_NAME);
        add(mHiddenPathName);

        final JPanel panel = createNicelySizedPanel();
        
        mFileChooser = new JFileChooser();
        mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mFileChooser.setMultiSelectionEnabled(false);
        mFileChooser.setControlButtonsAreShown(false);
        mFileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    final File selectedFile = mFileChooser.getSelectedFile();
                    if (selectedFile == null) {
                        LOGGER.debug("selected file is null"); // happens on the mac with a Temp folder
                        return;
                    }
                    LOGGER.debug("propertyChange gave me: " + selectedFile);
                    mChosenDirectory = selectedFile;
                    mHiddenPathName.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        panel.add(mFileChooser);
        add(mFileChooser);
        
        
        panel.validate();
        add(panel);
    }
}
