package uk.me.gumbley.minimiser.gui.mm;

import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.mm.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Attaches all the ActionListeners to the relevant MenuItems, via MenuWiring.
 * 
 * @author matt
 *
 */
public class MenuBuilder {
    private static final Logger LOGGER = Logger.getLogger(MenuBuilder.class);
    private final MenuWiring wiring;
    private final SpringLoader loader;

    /**
     * Create a MenuBuiller
     * @param menuWiring the MenuWiring instance to use.
     * @param springLoader the SpringLoader instance
     */
    public MenuBuilder(final MenuWiring menuWiring, final SpringLoader springLoader) {
        this.wiring = menuWiring;
        this.loader = springLoader;
    }

    /**
     * Build the menu by wiring it all up.
     */
    public void build() {
        loadAndWire(MenuIdentifier.FileNew);
    }

    private void loadAndWire(final MenuIdentifier menuIdentifier) {
        LOGGER.info(String.format("Loading ActionListener '%s'", menuIdentifier.toString()));
        wiring.setActionListener(menuIdentifier, loader.getBean("menuAL" + menuIdentifier.toString(), ActionListener.class));
    }
}