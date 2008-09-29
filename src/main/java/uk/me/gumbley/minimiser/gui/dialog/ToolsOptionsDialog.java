package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Frame;
import javax.swing.JDialog;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A modal Tools -> Options dialog.
 * TODO need a shadow Prefs - that records what's been changed, and if OK is
 * pressed, commits these changes to the real prefs.
 * TODO WOZERE need a JTabbedPane, an OK and Cancel button, and to load up a
 * list of beans that handle the individual tabs and load each one dynamically.
 * @author matt
 *
 */
public class ToolsOptionsDialog extends JDialog {
    private static final long serialVersionUID = 5362271048155735800L;
    private CursorManager cursorManager;
    private final Prefs prefs;

    /**
     * Create the Tools -> Options Dialog
     * @param parentFrame the parent frame, over which this modal dialog
     * will be presented
     * @param cursor the cursor manager, to show an hourglass whilst
     * the individual panels load
     * @param preferences the preferences, where choices made in this dialog will be
     * stored.
     */
    public ToolsOptionsDialog(final Frame parentFrame, final CursorManager cursor, final Prefs preferences) {
        super(parentFrame, true);
        cursorManager = cursor;
        prefs = preferences;
        setTitle("Options");
    }

    /**
     * Create a Tools -> Options dialog
     * @param parentFrame the parent frame, over which this modal dialog
     * will be presented
     * @param cursorManager the cursor manager, to show an hourglass whilst
     * the individual panels load
     * @param prefs the preferences, where choices made in this dialog will be
     * stored.
     */
    public static void showOptions(final Frame parentFrame,
            final CursorManager cursorManager, final Prefs prefs) {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final ToolsOptionsDialog dialog = new ToolsOptionsDialog(parentFrame, cursorManager, prefs);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
        
    }
}
