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

import org.devzendo.minimiser.gui.menu.MenuMediatorUnittestCase;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests the lifecycle that initialises the View Menu
 * @author matt
 *
 */
public final class TestViewMenuInitialiserLifecycle extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void initialiseViewMenuCorrectly() {
        getPrefs().setTabHidden("SQL");
        getPrefs().clearTabHidden("Categories");

        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        startMediator();

        // Still no change
        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        // Start lifecycle (manually)
        final ViewMenuInitialiserLifecycle lifecycle = new ViewMenuInitialiserLifecycle(getPrefs(), getStubMenu());
        lifecycle.startup();

        // now it should have changed
        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isTabHidden("Categories"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }
}
