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

package org.devzendo.minimiser.util;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;
import org.devzendo.commoncode.patterns.observer.Observer;

/**
 * An observable event listener that has a name and records its event
 * occurrences.
 * 
 * @author matt
 *
 */
public final class NamedListener implements Observer<ObservableEvent> {
    private final OrderMonitor orderMonitor;
    private final String listenerName;

    /**
     * Construct a NamedListener
     * @param monitor the OrderMonitor to use for recording
     * @param name the name of this Listener
     */
    public NamedListener(final OrderMonitor monitor, final String name) {
        this.orderMonitor = monitor;
        this.listenerName = name;
        if (orderMonitor == null) {
            throw new IllegalArgumentException("no ordermonitor!");
        }
        if (listenerName == null) {
            throw new IllegalArgumentException("no name!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final ObservableEvent observableEvent) {
        orderMonitor.add(listenerName + ' ' + observableEvent.getClass().getSimpleName());
    }
}
