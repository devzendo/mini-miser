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

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commongui.GUIUtils;
import org.devzendo.minimiser.gui.dialog.welcome.WelcomeDialogHelper;
import org.devzendo.minimiser.startupqueue.StartupQueue;


/**
 * Shows the Welcome Dialog upon detection of a fresh installation.
 * Shows the What's New In This Release Dialog upon detection of an upgrade
 * installation.
 * Is otherwise silent.
 * Shows the dialog via the startup queue, so it'll happen at the end of
 * startup. Otherwise, the dialog creates a jarring clash with any open
 * encrypted databases, which prompt for their password.
 * 
 * @author matt
 *
 */
public final class WelcomeMessageDisplayingListener implements Observer<UpgradeEvent> {

    private final StartupQueue startupQueue;

    /**
     * Construct the listener.
     * @param sq the Startup Queue
     */
    public WelcomeMessageDisplayingListener(final StartupQueue sq) {
        this.startupQueue = sq;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (observableEvent instanceof FreshInstallEvent) {
            placeSwingRunnableOnStartupQueue(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWelcomeDialog();
                }
            });
        } else if (observableEvent instanceof SoftwareUpgradedEvent) {
            placeSwingRunnableOnStartupQueue(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWhatsNewDialog();
                }
            });
        }
    }
    
    private void placeSwingRunnableOnStartupQueue(final Runnable showDialog) {
        startupQueue.addRunnable(new Runnable() {
            public void run() {
                GUIUtils.invokeLaterOnEventThread(showDialog);
            }
        });
    }
}
