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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.devzendo.minimiser.gui.wizard.MiniMiserWizardPage;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Tell the user about database opening.
 * @author matt
 *
 */
public final class FileOpenWizardIntroPage extends MiniMiserWizardPage {
    private static final long serialVersionUID = -1951314726620966608L;
    private static PluginRegistry gPluginRegistry; // must be static for Wizard :(

    /**
     * Create an intro page. 
     * @param pluginRegistry the plugin registry
     */
    public FileOpenWizardIntroPage(final PluginRegistry pluginRegistry) {
        FileOpenWizardIntroPage.gPluginRegistry = pluginRegistry;
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
              "You are about to open an existing " + gPluginRegistry.getApplicationName() + " database.\n\n"
              
            + "Databases comprise several files that are kept together in their own folder.\n"
            + "Together, these files hold all of your account information.\n\n"
            
            + "To open the database, just choose its folder.\n\n"
            
            + "If the database was created by an earlier version of " + gPluginRegistry.getApplicationName() + ",\n" 
            + "it may be necessary to convert it into the current format. This conversion cannot be\n"
            + "undone, and after conversion, the database may not be usable by earlier versions\n"
            + "of " + gPluginRegistry.getApplicationName() + ".\n\n"
            
            + "Press 'Next>' to choose the database's folder.\n\n"
            
            + "Or, press 'Cancel' to abandon the opening of an existing database.";
    }

    /**
     * @return wizard page description for the LH area
     */
    public static String getDescription() {
        return "About " + gPluginRegistry.getApplicationName() + " databases";
    }

}
