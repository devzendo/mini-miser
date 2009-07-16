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

import uk.me.gumbley.minimiser.gui.layout.CentredLayout;
import uk.me.gumbley.minimiser.gui.menu.MenuWiring;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;

/**
 * The introduction panel that's visible when no databases are open
 * that provides a set of useful 'getting started' menu shortcuts
 * on buttons.
 * 
 * @author matt
 */
@SuppressWarnings("serial")
public final class IntroPanel extends JPanel {
    private final JPanel mButtonGridPanel;
    private final MenuWiring mMenuWiring;

    /**
     * Create the IntroPanel that triggers menu items via the
     * MenuWiring.
     * @param menuWiring the menu wiring.
     */
    public IntroPanel(final MenuWiring menuWiring) {
        super(new CentredLayout());
        mMenuWiring = menuWiring;

        mButtonGridPanel = new JPanel();
        mButtonGridPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mButtonGridPanel.setMaximumSize(new Dimension(400, 300));
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
