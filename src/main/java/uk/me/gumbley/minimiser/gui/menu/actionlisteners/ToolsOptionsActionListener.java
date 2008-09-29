package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.ToolsOptionsDialog;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Triggers display of the tools->options dialog.
 * 
 * @author matt
 *
 */
public final class ToolsOptionsActionListener extends SnailActionListener {
    private final Frame mainFrame;
    private final Prefs prefs;

    /**
     * @param frame the main frame
     * @param cursor the cursor manager
     * @param preferences the preferences 
     */
    public ToolsOptionsActionListener(final Frame frame, final CursorManager cursor,
            final Prefs preferences) {
        super(cursor);
        this.mainFrame = frame;
        this.prefs = preferences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
//        getCursorManager().normal();
        ToolsOptionsDialog.showOptions(mainFrame, getCursorManager(), prefs);
    }
}
