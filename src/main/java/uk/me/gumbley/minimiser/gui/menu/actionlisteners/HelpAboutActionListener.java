package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.about.AboutDialog;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * Triggers display of the about dialog.
 * 
 * @author matt
 *
 */
public final class HelpAboutActionListener extends SnailActionListener {
    private final Frame mMainFrame;
    private final PluginRegistry mPluginRegistry;

    /**
     * @param pluginRegistry the plugin registry
     * @param frame the main frame
     * @param cursor the cursor manager 
     */
    public HelpAboutActionListener(final PluginRegistry pluginRegistry,
            final Frame frame, final CursorManager cursor) {
        super(cursor);
        mPluginRegistry = pluginRegistry;
        mMainFrame = frame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        AboutDialog.showAbout(mPluginRegistry, mMainFrame, getCursorManager());
    }
}
