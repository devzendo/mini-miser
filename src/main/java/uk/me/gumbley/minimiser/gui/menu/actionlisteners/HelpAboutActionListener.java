package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.about.AboutDialog;

/**
 * Triggers display of the about dialog.
 * 
 * @author matt
 *
 */
public final class HelpAboutActionListener extends SnailActionListener {
    private final Frame mainFrame;

    /**
     * @param frame the main frame
     * @param cursor the cursor manager 
     */
    public HelpAboutActionListener(final Frame frame, final CursorManager cursor) {
        super(cursor);
        this.mainFrame = frame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        AboutDialog.showAbout(mainFrame, getCursorManager());
    }
}
