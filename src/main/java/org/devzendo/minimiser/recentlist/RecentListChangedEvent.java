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

package org.devzendo.minimiser.recentlist;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

/**
 * The recent list has changed - something has been added that wasn't there
 * before, or something that was there before has been moved to the head,
 * and this is the notification you receive from it, if you're an observer.
 * 
 * At the moment, there's only one event, hence this being final.
 * 
 * @author matt
 *
 */
public final class RecentListChangedEvent implements ObservableEvent {
    /**
     * Create a RecentListChangedEvent
     */
    public RecentListChangedEvent() {
    }
}
