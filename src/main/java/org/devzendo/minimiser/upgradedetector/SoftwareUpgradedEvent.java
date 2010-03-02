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

/**
 * An Upgrade Event that denotes an upgrade installation of the software
 * @author matt
 *
 */
public final class SoftwareUpgradedEvent extends UpgradeEvent {

    private final String previousVersion;

    /**
     * Construct a software upgraded event
     * @param runningVer the running software version
     * @param previousVer the previous version of the software
     */
    public SoftwareUpgradedEvent(final String runningVer, final String previousVer) {
        super(runningVer);
        this.previousVersion = previousVer;
    }

    /**
     * Obtain the version of the software that was running previously
     * @return the previous version
     */
    public String getPreviousVersion() {
        return previousVersion;
    }
}
