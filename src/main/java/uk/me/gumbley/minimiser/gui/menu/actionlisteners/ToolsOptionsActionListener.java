package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.toolsoptions.ToolsOptionsDialog;
import uk.me.gumbley.minimiser.gui.dialog.toolsoptions.ToolsOptionsTabFactory;
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
    private final ToolsOptionsTabFactory toolsOptionsTabFactory;

    /**
     * @param frame the main frame
     * @param cursor the cursor manager
     * @param preferences the preferences 
     * @param tabFactory the Tools->Options tab factory
     */
    public ToolsOptionsActionListener(final Frame frame, final CursorManager cursor,
            final Prefs preferences, final ToolsOptionsTabFactory tabFactory) {
        super(cursor);
        this.mainFrame = frame;
        this.prefs = preferences;
        this.toolsOptionsTabFactory = tabFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
//        getCursorManager().normal();
        ToolsOptionsDialog.showOptions(mainFrame, getCursorManager(), prefs, toolsOptionsTabFactory);
    }
}
