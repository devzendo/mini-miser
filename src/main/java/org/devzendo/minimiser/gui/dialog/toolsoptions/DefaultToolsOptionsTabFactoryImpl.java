/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commonspring.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.gui.dialog.problem.ProblemReporter;
import org.devzendo.minimiser.prefs.ChangeCollectingPrefsFactory;
import org.devzendo.minimiser.prefs.Prefs;


/**
 * The default implementation of the ToolsOptionsTabFactory.
 * 
 * @author matt
 *
 */
public final class DefaultToolsOptionsTabFactoryImpl implements ToolsOptionsTabFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultToolsOptionsTabFactoryImpl.class);
    
    private final SpringLoader springLoader;
    private final ProblemReporter problemReporter;
    private final List<String> tabBeanNames;
    
    // Used by the run-on-EDT code in callInitComponentOnSwingEventThread
    private final Object lock = new Object();


    /**
     * Construct the ToolsOptionsTabFactory.
     * @param loader the SpringLoader
     * @param reporter the problem reporter for serious problems
     * @param beanNames the list of ToolsOptionTab beans to manage.
     */
    public DefaultToolsOptionsTabFactoryImpl(final SpringLoader loader,
            final ProblemReporter reporter,
            final List<String> beanNames) {
        this.springLoader = loader;
        this.problemReporter = reporter;
        this.tabBeanNames = beanNames;
    }

    /**
     * Construct the ToolsOptionsTabFactory. There's no problem reporter; use
     * this variant in unit tests, for simplicity.
     * @param loader the SpringLoader
     * @param beanNames the list of ToolsOptionTab beans to manage.
     */
    public DefaultToolsOptionsTabFactoryImpl(final SpringLoader loader,
            final List<String> beanNames) {
        this(loader, null, beanNames);
    }
    
    /**
     * Get the ChangeCollectingPrefsFactory
     * @return the change-collecting prefs factory 
     */
    private ChangeCollectingPrefsFactory getChangeCollectingPrefsFactory() {
        return springLoader.getBean("&changeCollectingPrefs", ChangeCollectingPrefsFactory.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ToolsOptionsTab> loadTabs(final Prefs prefs) {
        LOGGER.info("Initialising Tools->Options tab beans");
        assert !SwingUtilities.isEventDispatchThread();

        final ArrayList<ToolsOptionsTab> tabList = new ArrayList<ToolsOptionsTab>(); 

        // Set the change collecting prefs in the factory so that it can be
        // retrieved by any tab beans that need it
        LOGGER.debug("Setting change-collecting prefs");
        final ChangeCollectingPrefsFactory ccpFactory = getChangeCollectingPrefsFactory();
        ccpFactory.setChangeCollectingPrefs(prefs);
        
        try {
            // Now load the tabs, and initialise their components on the EDT.
            LOGGER.debug("Loading tab beans");
            final ToolsOptionsTabLoader loader = new ToolsOptionsTabLoader(springLoader, tabBeanNames);
            LOGGER.debug("Tab beans loaded; initialising components");
            for (final String beanName : loader.getBeanNames()) {
                final ToolsOptionsTab tab = loader.getBean(beanName);
                callInitComponentOnSwingEventThread(tab);
                tabList.add(tab);
            }
            LOGGER.debug("Components initialised");
        } catch (final RuntimeException re) {
            problemReporter.reportProblem("while loading the Tools->Options tabs", re);
        } finally {
            // Clear change collecting prefs in factory
            LOGGER.debug("Claring change-collecting prefs");
            ccpFactory.clearChangeCollectingPrefs();
    
            LOGGER.info("Finished loading Tools->Options tab beans");
        }
        return tabList;
    }

    /**
     * {@inheritDoc}
     */
    public void closeTabs(final List<ToolsOptionsTab> tabs) {
        assert !SwingUtilities.isEventDispatchThread();
        
        for (final ToolsOptionsTab tab : tabs) {
            if (tab != null) {
                tab.destroy();
                callDisposeComponentOnSwingEventThread(tab);
            }
        }
    }

    /**
     * Call the initComponent method on the Swing Event Thread.
     * Precondition: this code is never executed on the EDT - there's an
     * assertion in the calling method for this.
     * 
     * @param tab the tab to initialise
     */
    private void callInitComponentOnSwingEventThread(final ToolsOptionsTab tab) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    tab.initComponent();
                }
            }
        });
        // this might be voodoo, but it might help update caches on multiprocessors?
        synchronized (lock) {
            return;
        }
    }

    /**
     * Call the disposeComponent method on the Swing Event Thread.
     * Precondition: this code is never executed on the EDT - there's an 
     * assertion in the calling method for this.
     * 
     * @param tab the tab to dispose whose component is to be disposed.
     */
    private void callDisposeComponentOnSwingEventThread(final ToolsOptionsTab tab) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    tab.disposeComponent();
                }
            }
        });
        // this might be voodoo, but it might help update caches on multiprocessors?
        synchronized (lock) {
            return;
        }
    }

    /**
     * A SpringBeanListLoader that loads the Tools->Options tabs given a list.
     * @author matt
     *
     */
    private static class ToolsOptionsTabLoader extends AbstractSpringBeanListLoaderImpl<ToolsOptionsTab> {

        /**
         * Load the tabs from a list of beans
         * @param springLoader the SpringLoader
         * @param tabBeanNames the list of tab bean names
         */
        public ToolsOptionsTabLoader(final SpringLoader springLoader, final List<String> tabBeanNames) {
            super(springLoader, tabBeanNames);
        }
        
    }
}
