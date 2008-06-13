package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileNewActionListener implements ActionListener {
    public void actionPerformed(final ActionEvent e) {
        FileNewWizard.startFileNewWizard();
    }
}
