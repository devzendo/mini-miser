package uk.me.gumbley.minimiser.gui.tabpanemanager;

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

import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.layout.CentredLayout;
import uk.me.gumbley.minimiser.gui.menu.MenuWiring;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.gui.panel.ImagePanel;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

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