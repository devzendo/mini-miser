package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.about.AboutDialog;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;

/**
 * Triggers display of the about dialog.
 * 
 * @author matt
 *
 */
public final class HelpAboutActionListener extends SnailActionListener {
    private final Frame mMainFrame;
    private final AppDetails mAppDetails;

    /**
     * @param appDetails the application details
     * @param frame the main frame
     * @param cursor the cursor manager 
     */
    public HelpAboutActionListener(final AppDetails appDetails,
            final Frame frame, final CursorManager cursor) {
        super(cursor);
        mAppDetails = appDetails;
        mMainFrame = frame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        AboutDialog.showAbout(mAppDetails, mMainFrame, getCursorManager());
    }
}
