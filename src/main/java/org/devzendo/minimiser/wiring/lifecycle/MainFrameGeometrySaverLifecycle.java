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

import javax.swing.JFrame;

import org.devzendo.minimiser.gui.WindowGeometryStore;
import org.devzendo.minimiser.lifecycle.Lifecycle;

/**
 * A Lifecycle that saves the geometry of the main window.
 * 
 * @author matt
 *
 */
public final class MainFrameGeometrySaverLifecycle implements Lifecycle {
    private final WindowGeometryStore windowGeometryStore;
    private final JFrame frame;

    /**
     * Construct
     * @param geometryStore the geometry store
     * @param mainFrame the main window, which will have been populated in the
     * MainFrameFactory by now.
     */
    public MainFrameGeometrySaverLifecycle(final WindowGeometryStore geometryStore, final JFrame mainFrame) {
        this.windowGeometryStore = geometryStore;
        this.frame = mainFrame;
        
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        windowGeometryStore.saveGeometry(frame);
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing - the geometry is applied way before Lifecycle startup 
    }
}
