package uk.me.gumbley.minimiser.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for obtaining the singleton MainFrameTitle - the application's
 * main window's title bar controller.
 * 
 * @author matt
 *
 */
public final class MainFrameTitleFactory implements FactoryBean {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameTitleFactory.class);
    private MainFrameTitle factoryMainFrameTitle;
    
    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        LOGGER.debug("MainFrameTitleFactory returning main frame title as MainFrameTitle object");
        return factoryMainFrameTitle;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return MainFrameTitle.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Factory population method
     * @param frameTitle the MainFrameTitle to return as a Singleton
     */
    public void setMainFrameTitle(final MainFrameTitle frameTitle) {
        LOGGER.debug("MainFrameTitleFactory being populated with MainFrameTitle object");
        factoryMainFrameTitle = frameTitle;
    }
}
