package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.awt.Component;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageHelper;
import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * A developer-friendly (and moderately user-friendly, if you know SQL) SQL
 * investigation, debug and diagnosis tab.
 * <p>
 * No TDD here.
 *  
 * @author matt
 *
 */
public final class SQLTab implements Tab {
    private final DatabaseDescriptor mDatabaseDescriptor;
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry;
    private volatile SQLTabPanel mainPanel;

    /**
     * Construct the SQL tab
     * @param descriptor the database descriptor
     * @param cursor the cursor manager
     * @param pluginRegistry the plugin registry
     */
    public SQLTab(final DatabaseDescriptor descriptor,
            final CursorManager cursor,
            final PluginRegistry pluginRegistry) {
        mDatabaseDescriptor = descriptor;
        mCursorManager = cursor;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        mainPanel = new SQLTabPanel(mDatabaseDescriptor, mCursorManager, mPluginRegistry);
        
        DSTAMessageHelper.possiblyShowMessage(DSTAMessageId.SQL_TAB_INTRO, 
            "The SQL view is intended to aid developers in diagnosing\n"
            + "problems with databases. For further information on its use,\n"
            + "please consult the Technical Guide.\n\n"
            + "The SQL view is not intended for day-to-day use.\n"
        );
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        mainPanel.finished();
    }
}
