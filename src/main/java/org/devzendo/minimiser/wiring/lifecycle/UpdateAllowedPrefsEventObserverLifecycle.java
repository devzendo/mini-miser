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

package org.devzendo.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsEvent;
import org.devzendo.minimiser.updatechecker.UpdateChecker;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;


/**
 * Adapts between the 'update allowed' prefs change events and the
 * triggering of update availability checks.
 * Also enables the relevant menu item. This latter functionality
 * should probably be broken out into another prefs event observer.
 * @author matt
 *
 */
public final class UpdateAllowedPrefsEventObserverLifecycle implements Lifecycle, Observer<PrefsEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(UpdateAllowedPrefsEventObserverLifecycle.class);

    private final Prefs mPrefs;
    private final UpdateChecker mUpdateChecker;
    private final UpdateProgressAdapterFactory mUpdateProgressAdapterFactory;
    private final Menu mMenu;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param updateChecker the update checker
     * @param prefs the prefs
     * @param adapterFactory the update progress adapter factory
     * @param menu the menu
     */
    public UpdateAllowedPrefsEventObserverLifecycle(
            final UpdateChecker updateChecker,
            final Prefs prefs,
            final UpdateProgressAdapterFactory adapterFactory,
            final Menu menu) {
        this.mUpdateChecker = updateChecker;
        this.mPrefs = prefs;
        this.mUpdateProgressAdapterFactory = adapterFactory;
        this.mMenu = menu;
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PrefsEvent observableEvent) {
        LOGGER.debug("Prefs event: " + observableEvent.getPrefsSection());
        if (observableEvent.getPrefsSection() != Prefs.PrefsSection.BOOLEAN_FLAGS) {
            return;
        }
        final boolean updatesAllowed = mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED);
        mMenu.setHelpCheckForUpdatesEnabled(updatesAllowed);
        if (!updatesAllowed) {
            LOGGER.info("Prefs have changed, but update checks are disallowed");
            return;
        }
        LOGGER.info("Triggering update check");
        mUpdateChecker.triggerUpdateCheck(mUpdateProgressAdapterFactory.createVisibleUpdateProgressAdapter());
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
        mPrefs.addChangeListener(this);
    }
}
