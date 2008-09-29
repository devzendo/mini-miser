package uk.me.gumbley.minimiser.gui.menu;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * A Spring FactoryBean for the MenuState Singleton
 * @author matt
 *
 */
public final class MenuStateFactory implements FactoryBean {
    private static final Logger LOGGER = Logger.getLogger(MenuStateFactory.class);
    private MenuState factoryMenuState;

    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        LOGGER.debug(String.format("MenuStateFactory returning %s as MenuState object", factoryMenuState));
        return factoryMenuState;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return MenuState.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }
    
    /**
     * Factory population method
     * @param menuState the MenuState object to return as a Singleton
     */
    public void setMenuState(final MenuState menuState) {
        LOGGER.debug(String.format("MenuStateFactory being populated with %s as MenuState object", menuState));
        factoryMenuState = menuState;
    }
}
