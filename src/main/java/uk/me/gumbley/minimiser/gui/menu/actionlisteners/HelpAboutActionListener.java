package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.me.gumbley.minimiser.gui.dialog.AboutDialog;

/**
 * Triggers display of the about dialog.
 * 
 * @author matt
 *
 */
public final class HelpAboutActionListener implements ActionListener {
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        // TODO pass in the main frame
        AboutDialog.showAbout(null);
    }
}
