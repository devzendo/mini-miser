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

package org.devzendo.minimiser.lifecycle;

import org.devzendo.minimiser.springbeanlistloader.SpringBeanListLoader;


/**
 * Manager that controls the lifecycle of various objects. Allows objects to
 * be started when the main window is made visible, and stopped when the
 * main window is closed.
 *  
 * @author matt
 *
 */
public interface LifecycleManager extends SpringBeanListLoader<Lifecycle> {
    /**
     * Start up the beans managed by the LifecycleManager, in the order that
     * they were given when instantiating the LifecycleManager. 
     */
    void startup();

    /**
     * Shut down the beans managed by the LifecycleManager, in the reverse order
     * that they were given when instantiating the LifecycleManager. 
     */
    void shutdown();
}
