package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.devzendo.minimiser.gui.dialog.welcome.WelcomeDialogHelper;

/**
 * Triggers display of the welcome dialog.
 * 
 * @author matt
 *
 */
public final class HelpWelcomeActionListener implements ActionListener {

    /**
     */
    public HelpWelcomeActionListener() {
    }

    /**
     * @param e an event
     */
    public void actionPerformed(final ActionEvent e) {
        WelcomeDialogHelper.showWelcomeDialog();
    }
}
