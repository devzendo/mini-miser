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
package org.devzendo.minimiser.gui;

import org.devzendo.commongui.WindowGeometryStorePersistence;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * Adapter implementation that stores window geometry information in prefs.
 * 
 * @author matt
 *
 */
public final class WindowGeometryStorePrefsPersistence implements
        WindowGeometryStorePersistence {
    private final Prefs mPrefs;

    /**
     * Create an adapter that stores in prefs.
     * 
     * @param prefs the prefs in which to store the geometry info
     */
    public WindowGeometryStorePrefsPersistence(final Prefs prefs) {
        mPrefs = prefs;
    }

    /**
     * {@inheritDoc}
     */
    public String getWindowGeometry(final String windowName) {
        return mPrefs.getWindowGeometry(windowName);
    }

    /**
     * {@inheritDoc}
     */
    public void setWindowGeometry(final String windowName, final String geometry) {
        mPrefs.setWindowGeometry(windowName, geometry);
    }
}
