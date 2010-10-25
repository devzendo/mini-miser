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

package org.devzendo.minimiser.gui.tabpanemanager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.devzendo.commoncode.resource.ResourceLoader;
import org.devzendo.minimiser.gui.layout.CentredLayout;
import org.devzendo.minimiser.gui.menu.MenuIdentifier;
import org.devzendo.minimiser.gui.menu.MenuWiring;
import org.devzendo.minimiser.gui.panel.ImagePanel;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * The introduction panel that's visible when no databases are open
 * that provides a set of useful 'getting started' menu shortcuts
 * on buttons.
 * 
 * @author matt
 */
@SuppressWarnings("serial")
public final class IntroPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(IntroPanel.class);
    private final JPanel mButtonGridPanel;
    private final MenuWiring mMenuWiring;
    private final PluginRegistry mPluginRegistry;

    /**
     * Create the IntroPanel that triggers menu items via the
     * MenuWiring.
     * @param menuWiring the menu wiring.
     * @param pluginRegistry the plugin registry via which the
     * background graphic will be sought
     */
    public IntroPanel(final MenuWiring menuWiring, final PluginRegistry pluginRegistry) {
        super(new CentredLayout());
        mMenuWiring = menuWiring;
        mPluginRegistry = pluginRegistry;

        mButtonGridPanel = createBackgroundPanel();
        final GridBagLayout gridBagLayout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        mButtonGridPanel.setLayout(gridBagLayout);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.ipady = 20; 
        c.insets = new Insets(10, 50, 10, 50);
        c.gridx = 0;

        c.gridy = 0;
        mButtonGridPanel.add(createButton("Create new database", MenuIdentifier.FileNew), c);

        c.gridy = 2;
        mButtonGridPanel.add(createButton("Open existing database", MenuIdentifier.FileOpen), c);

        c.gridy = 4;
        mButtonGridPanel.add(createButton("Exit", MenuIdentifier.FileExit), c);
        add(mButtonGridPanel);
    }

    private JPanel createBackgroundPanel() {
        final JPanel panel;
        final String imagePath = getBackgroundImagePath();
        if (imagePath == null) {
            panel = new JPanel();
            panel.setMaximumSize(new Dimension(400, 300));
        } else {
            panel = new ImagePanel(ResourceLoader.getResourceURL(imagePath));
        }
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        return panel;
    }

    private String getBackgroundImagePath() {
        if (mPluginRegistry.getApplicationPluginDescriptor() == null) {
            LOGGER.warn("No application plugin - no intro panel graphic");
            return null;
        }
        final String imagePath = mPluginRegistry.getApplicationPluginDescriptor().getIntroPanelBackgroundGraphicResourcePath();
        if (StringUtils.isBlank(imagePath)) {
            LOGGER.warn("Blank intro panel graphic specified in resource path");
            return null;
        }
        if (!ResourceLoader.resourceExists(imagePath)) {
            LOGGER.warn("Intro panel resource path '" + imagePath + "' does not exist");
            return null;
        }
        LOGGER.info("Using '" + imagePath + "' as the intro panel background graphic");
        return imagePath;
    }

    private JButton createButton(final String text, final MenuIdentifier menuId) {
        final JButton button = new JButton(text);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                mMenuWiring.triggerActionListener(menuId);
            }
        });
        return button;
    }
}
