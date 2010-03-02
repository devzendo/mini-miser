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

import org.devzendo.minimiser.gui.menu.MenuProvidingFacadeInitialiser;
import org.devzendo.minimiser.lifecycle.Lifecycle;

/**
 * Initialises any plugins' MenuProvidingFacades.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeInitialiserLifecycle implements Lifecycle {
    private final MenuProvidingFacadeInitialiser mInitialiser;

    /**
     * @param initialiser the MenuProvidingFacadeInitialiser
     */
    public MenuProvidingFacadeInitialiserLifecycle(final MenuProvidingFacadeInitialiser initialiser) {
        mInitialiser = initialiser;
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
        mInitialiser.initialise();
    }
}
