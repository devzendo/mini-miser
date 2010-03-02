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

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.minimiser.gui.menu.MenuMediatorUnittestCase;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests the lifecycle that initialises the Help|Check for updates
 * Menu Item
 * @author matt
 *
 */
public final class TestHelpCheckForUpdatesMenuInitialiserLifecycle extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void isCheckForUpdatesMenuDisabledWhenUpdatesDisabled() {
        Assert.assertFalse(getPrefs().isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));

        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        // Start lifecycle (manually)
        final HelpCheckForUpdatesMenuInitialiserLifecycle lifecycle = new HelpCheckForUpdatesMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());
    }

    /**
     *
     */
    @Test
    public void isCheckForUpdatesMenuEnabledWhenUpdatesEnabled() {
        getPrefs().setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);

        Assert.assertTrue(getPrefs().isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));

        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isHelpCheckForUpdatesEnabled());

        // Start lifecycle (manually)
        final HelpCheckForUpdatesMenuInitialiserLifecycle lifecycle = new HelpCheckForUpdatesMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // now it should have changed
        ThreadUtils.waitNoInterruption(100);
        Assert.assertTrue(getStubMenu().isHelpCheckForUpdatesEnabled());
    }
}
