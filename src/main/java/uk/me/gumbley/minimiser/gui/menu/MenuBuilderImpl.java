package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Attaches all the ActionListeners to the relevant MenuItems, via
 * MenuWiring.
 *
 * @author matt
 *
 */
public class MenuBuilderImpl implements MenuBuilder {
    private static final Logger LOGGER = Logger.getLogger(MenuBuilderImpl.class);
    private final MenuWiring wiring;
    private final SpringLoader loader;

    /**
     * Create a MenuBuilder
     * @param menuWiring the MenuWiring instance to use.
     * @param springLoader the SpringLoader instance
     */
    public MenuBuilderImpl(final MenuWiring menuWiring, final SpringLoader springLoader) {
        this.wiring = menuWiring;
        this.loader = springLoader;
    }

    /**
     * Build the menu by wiring it all up.
     */
    public void build() {
        loadAndWire(MenuIdentifier.FileNew);
        loadAndWire(MenuIdentifier.FileOpen);
        loadAndWire(MenuIdentifier.FileClose);
        loadAndWire(MenuIdentifier.FileCloseAll);

        loadAndWire(MenuIdentifier.ToolsOptions);

        loadAndWire(MenuIdentifier.HelpAbout);
        loadAndWire(MenuIdentifier.HelpWelcome);
        loadAndWire(MenuIdentifier.HelpWhatsNew);
        loadAndWire(MenuIdentifier.HelpCheckForUpdates);
    }

    private void loadAndWire(final MenuIdentifier menuIdentifier) {
        LOGGER.info(String.format("Loading ActionListener '%s'", menuIdentifier.toString()));
        wiring.setActionListener(menuIdentifier, loader.getBean("menuAL" + menuIdentifier.toString(), ActionListener.class));
    }

}
