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

package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.menu.actionlisteners.SnailActionListener;
import org.devzendo.minimiser.gui.menu.actionlisteners.filenew.FileNewResult;
import org.devzendo.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardChooseFolderPage;
import org.devzendo.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardIntroPage;
import org.devzendo.minimiser.gui.menu.actionlisteners.filenew.FileNewWizardSecurityOptionPage;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseWizardPages;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseWizardPagesFacade;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;


/**
 * Triggers the start of the wizard from the File/New menu.
 * 
 * @author matt
 *
 */
public final class FileNewActionListener extends SnailActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileNewActionListener.class);
    private final OpenDatabaseList mOpenDatabaseList;
    private final AccessFactory mAccessFactory;
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry;
    private final PluginManager mPluginManager;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     * @param pluginRegistry the plugin registry
     * @param pluginManager Tthe plugin manager
     */
    public FileNewActionListener(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager,
            final PluginRegistry pluginRegistry,
            final PluginManager pluginManager) {
        super(cursorManager);
        mOpenDatabaseList = openDatabaseList;
        mAccessFactory = accessFactory;
        mCursorManager = cursorManager;
        mPluginRegistry = pluginRegistry;
        mPluginManager = pluginManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        final WizardPage[] wizardPages = getWizardPages();
        final WizardResultProducer producer = new WizardResultProducer() {
            @SuppressWarnings("unchecked")
            public boolean cancel(final Map settings) {
                return true;
            }

            @SuppressWarnings("unchecked")
            public Object finish(final Map settings) throws WizardException {
                return new FileNewResult(mOpenDatabaseList,
                    mAccessFactory,
                    mCursorManager);
            }
        };
        final Wizard wizard = WizardPage.createWizard(wizardPages, producer);
        // ... and on with the show...
        wizard.show();
    }

    private WizardPage[] getWizardPages() {
        final ArrayList<WizardPage> wizardPages = new ArrayList<WizardPage>();
        // Standard pages....
        wizardPages.add(new FileNewWizardIntroPage(mPluginRegistry));
        wizardPages.add(new FileNewWizardChooseFolderPage(mOpenDatabaseList));
        wizardPages.add(new FileNewWizardSecurityOptionPage());
        // pages from plugins...
        final List<NewDatabaseWizardPages> newDatabaseWizardPagesPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseWizardPages.class);
        for (final NewDatabaseWizardPages newDatabaseWizardPages : newDatabaseWizardPagesPlugins) {
            final NewDatabaseWizardPagesFacade newDatabaseWizardPagesFacade = newDatabaseWizardPages.getNewDatabaseWizardPagesFacade();
            if (newDatabaseWizardPagesFacade == null) {
                LOGGER.warn(
                    "NewDatabaseWizardPages class " 
                    + newDatabaseWizardPages.getClass().getName() 
                    + " returned a null facade - ignoring");
            } else {
                final List<WizardPage> wizardPagesList = newDatabaseWizardPagesFacade.getWizardPages();
                if (wizardPagesList == null) {
                    LOGGER.warn(
                        "NewDatabaseWizardPagesFacade class "
                        + newDatabaseWizardPagesFacade.getClass().getName()
                        + " returned a null WizardPage list - ignoring");
                } else {
                    for (final WizardPage wizardPage : wizardPagesList) {
                        if (wizardPage == null) {
                            LOGGER.warn(
                                "NewDatabaseWizardPagesFacade class " 
                                + newDatabaseWizardPagesFacade.getClass().getName() 
                                + " returned a null WizardPage in the list - ignoring");
                        } else {
                            wizardPages.add(wizardPage);
                        }
                    }
                }
            }
        }
        return wizardPages.toArray(new WizardPage[0]);
    }
}
