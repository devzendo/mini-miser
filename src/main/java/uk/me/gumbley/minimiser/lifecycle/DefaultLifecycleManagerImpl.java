package uk.me.gumbley.minimiser.lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Default impl of LifecycleManager. Loads Lifecycle implementors using the
 * SpringLoader from a provided list.
 * 
 * @author matt
 *
 */
public final class DefaultLifecycleManagerImpl implements LifecycleManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultLifecycleManagerImpl.class);
    
    private final List<String> beanNames;
    private final Map<String, Lifecycle> lifecycleBeansMap;
    private final SpringLoader loader;
    
    /**
     * @param springLoader the Spring loader
     * @param lifecycleBeanNames the list of Lifecycle beans to manage.
     */
    public DefaultLifecycleManagerImpl(final SpringLoader springLoader, final List<String> lifecycleBeanNames) {
        this.loader = springLoader;
        lifecycleBeansMap = new HashMap<String, Lifecycle>();
        this.beanNames = instantiateLifecycleBeans(lifecycleBeanNames);
    }
    
    private List<String> instantiateLifecycleBeans(final List<String> originalBeanNames) {
        final List<String> loadedNames = new ArrayList<String>(); 
        LOGGER.info("LifecycleManager loading Lifecycle beans");
        for (final String beanName : originalBeanNames) {
            LOGGER.info("Obtaining Lifecycle bean '" + beanName + "'");
            try {
                final Lifecycle lifecycleBean = loader.getBean(beanName, Lifecycle.class);
                lifecycleBeansMap.put(beanName, lifecycleBean);
                loadedNames.add(beanName);
            } catch (final RuntimeException re) {
                LOGGER.warn("Could not load '" + beanName + ": " + re.getMessage(), re);
            }
        }
        LOGGER.info("Lifecycle beans loaded");
        return loadedNames;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getLifecycleBeanNames() {
        return beanNames;
    }

    /**
     * {@inheritDoc}
     */
    public Lifecycle getLifecycle(final String beanName) {
        return lifecycleBeansMap.get(beanName);
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        LOGGER.info("LifecycleManager starting Lifecycle beans...");
        for (final String beanName : beanNames) {
            LOGGER.info("Starting Lifecycle bean '" + beanName + "'");
            try {
                final Lifecycle lifecycleBean = lifecycleBeansMap.get(beanName);
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
        for (int i = beanNames.size() - 1; i >= 0; i--) {
            final String beanName = beanNames.get(i);
            LOGGER.info("Shutting down Lifecycle bean '" + beanName + "'");
            try {
                final Lifecycle lifecycleBean = lifecycleBeansMap.get(beanName);
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
