package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuBuilder;
import uk.me.gumbley.minimiser.gui.menu.MenuMediator;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A Lifecycle that intiialises the menu and attaches it to the
 * main frame.
 * 
 * Non-TDD.
 * 
 * @author matt
 *
 */
public final class MenuInitialisingLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(MenuInitialisingLifecycle.class);
    private final SpringLoader mSpringLoader;

    /**
     * Construct
     * @param mainFrame the main window, which will have been
     * populated in the MainFrameFactory by now.
     */
    public MenuInitialisingLifecycle(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                final JFrame mainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
                mainFrame.setJMenuBar(createMenu());
            }
        });
    }
    
    private JMenuBar createMenu() {
        LOGGER.info("Getting the menu");
        final Menu menu = mSpringLoader.getBean("menu", Menu.class);
        final RecentFilesList recentList = mSpringLoader.getBean("recentFilesList", RecentFilesList.class);
        menu.refreshRecentList(recentList.getRecentDatabases());
        // TODO SMELL why not have it populated correctly at first?
        // we could pass in the recent strings to a ctor?
        // NOTE the view menu is initially populated via a lifecycle
        LOGGER.info("Got the menu");
        final ActionListener actionListener = mSpringLoader.getBean("mainFrameCloseActionListener", ActionListener.class);
        menu.addMenuActionListener(MenuIdentifier.FileExit, actionListener);
        // wire up dependencies
        LOGGER.info("Wiring menu dependencies and adapters");
        mSpringLoader.getBean("menuMediator", MenuMediator.class);
        LOGGER.info("Menu dependencies wired");
        mSpringLoader.getBean("menuBuilder", MenuBuilder.class).build();
        LOGGER.info("Menu ActionListeners built and wired");
        return menu.getMenuBar();
    }
}
