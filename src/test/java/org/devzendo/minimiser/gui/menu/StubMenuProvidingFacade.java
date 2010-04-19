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

package org.devzendo.minimiser.gui.menu;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProvidingFacade;


/**
 * A MenuProvidingFacade for tests.
 *
 * @author matt
 *
 */
public final class StubMenuProvidingFacade implements MenuProvidingFacade {

    private ApplicationMenu mGlobalApplicationMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private boolean mInitialisedOnEventDispatchThread;
    private RuntimeException mException;

    /**
     * {@inheritDoc}
     */
    public void initialise(
            final ApplicationMenu globalApplicationMenu,
            final OpenDatabaseList openDatabaseList,
            final MenuFacade menuFacade) {
        mGlobalApplicationMenu = globalApplicationMenu;
        mOpenDatabaseList = openDatabaseList;

        mInitialisedOnEventDispatchThread = SwingUtilities.isEventDispatchThread();

        if (mException != null) {
            throw mException;
        }

        final JMenu customMenu = new JMenu("Custom");
        mGlobalApplicationMenu.addCustomMenu(customMenu);

        mOpenDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {

            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent open = (DatabaseOpenedEvent) observableEvent;
                    final DatabaseDescriptor databaseDescriptor = open.getDatabaseDescriptor();
                    final ApplicationMenu applicationMenu = databaseDescriptor.getApplicationMenu();
                    applicationMenu.addCustomMenu(new JMenu("DB Custom"));
                }

            }

        });
    }

    /**
     * @return the globalApplicationMenu
     */
    public ApplicationMenu getGlobalApplicationMenu() {
        return mGlobalApplicationMenu;
    }

    /**
     * @return true iff initialise was called on the Swing Event Thread
     */
    public boolean initialisedOnEventThread() {
        return mInitialisedOnEventDispatchThread;
    }

    /**
     * @param exception a failure to inject that should get displayed by the problem reporter
     */
    public void injectFailure(final RuntimeException exception) {
        mException = exception;
    }
}
