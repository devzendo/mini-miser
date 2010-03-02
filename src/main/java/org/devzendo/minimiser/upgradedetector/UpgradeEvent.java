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

package org.devzendo.minimiser.upgradedetector;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

/**
 * A version change has been detected, either due to a fresh installation or
 * proper upgrade, this is the notification
 * you receive from the UpgradeEventListenerManager, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class UpgradeEvent implements ObservableEvent {
    private final String runningVersion;

    /**
     * Create an UpgradeEvent
     * @param runningVer the currently running verson of the software
     */
    public UpgradeEvent(final String runningVer) {
        this.runningVersion = runningVer;
    }

    /**
     * Get the version of the running software
     * @return the running software version number
     */
    public final String getRunningVersion() {
        return runningVersion;
    }
}
