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

package org.devzendo.minimiser.wiring.menu;

import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageHelper;
import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.gui.menu.ViewMenuChoice;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;


/**
 * Displays a DSTA warning message on opening the SQL view.
 *
 * @author matt
 *
 */
public final class SQLViewChoiceObserver implements MenuWiringAdapter, Observer<ViewMenuChoice> {
    private final Menu mMenu;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param menu the menu
     */
    public SQLViewChoiceObserver(final Menu menu) {
        mMenu = menu;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> tab opener (the view menu)
        mMenu.addViewChoiceObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final ViewMenuChoice observableEvent) {
        if (observableEvent.getTabId().equals(SystemTabIdentifiers.SQL) && observableEvent.isOpened()) {
            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                public void run() {
                    DSTAMessageHelper.possiblyShowMessage(DSTAMessageId.SQL_TAB_INTRO, 
                        "The SQL view is intended to aid developers in diagnosing\n"
                        + "problems with databases. For further information on its use,\n"
                        + "please consult the Technical Guide.\n\n"
                        + "The SQL view is not intended for day-to-day use.\n"
                    );
                }});
        }
    }
}
