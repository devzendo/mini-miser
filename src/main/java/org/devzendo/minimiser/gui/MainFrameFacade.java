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

/**
 * A facade into the main application frame
 * 
 * @author matt
 *
 */
public interface MainFrameFacade {
    /**
     * @param enable true to enable all the main frame's controls, false to disable
     */
    void enableDisableControls(boolean enable);
    
    /**
     * Shut down the application
     */
    void shutdown();
    
    /**
     * Have we got any databases open?
     * @return true iff one or more databases open.
     */
    boolean anyDatabasesOpen();
}
