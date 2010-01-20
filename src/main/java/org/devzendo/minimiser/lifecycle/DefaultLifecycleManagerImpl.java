package org.devzendo.minimiser.lifecycle;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Default impl of LifecycleManager. Loads Lifecycle implementors using the
 * SpringLoader from a provided list.
 * 
 * @author matt
 *
 */
public final class DefaultLifecycleManagerImpl extends AbstractSpringBeanListLoaderImpl<Lifecycle> implements LifecycleManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultLifecycleManagerImpl.class);
    
    /**
     * @param springLoader the Spring loader
     * @param lifecycleBeanNames the list of Lifecycle beans to manage.
     */
    public DefaultLifecycleManagerImpl(final SpringLoader springLoader, final List<String> lifecycleBeanNames) {
        super(springLoader, lifecycleBeanNames);
    }
    
    /**
     * {@inheritDoc}
     */
    public void startup() {
        LOGGER.info("LifecycleManager starting Lifecycle beans...");
        for (final String beanName : getBeanNames()) {
            LOGGER.info("Starting Lifecycle bean '" + beanName + "'");
            try {
                final Lifecycle lifecycleBean = getBean(beanName);
                if (lifecycleBean != null) {
                    lifecycleBean.startup();
                }
            } catch (final RuntimeException re) {
                LOGGER.warn("Could not start up '" + beanName + ": " + re.getMessage(), re);
            }
        }
        LOGGER.info("End of LifecycleManager startup");
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        LOGGER.info("LifecycleManager shutting down Lifecycle beans...");
        for (int i = getBeanNames().size() - 1; i >= 0; i--) {
            final String beanName = getBeanNames().get(i);
            LOGGER.info("Shutting down Lifecycle bean '" + beanName + "'");
            try {
                final Lifecycle lifecycleBean = getBean(beanName);
                if (lifecycleBean != null) {
                    lifecycleBean.shutdown();
                }
            } catch (final RuntimeException re) {
                LOGGER.warn("Could not shut down '" + beanName + ": " + re.getMessage(), re);
            }
        }
        LOGGER.info("End of LifecycleManager shutdown");
    }
}
