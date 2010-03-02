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

package org.devzendo.minimiser.updatechecker;

/**
 * Creates UpdateProgressAdapters that either provide feedback via
 * the progress bar, or are invisible.
 * 
 * @author matt
 *
 */
public final class DefaultUpdateProgressAdapterFactory implements
        UpdateProgressAdapterFactory {
    private final UpdateProgressAdapter mVisibleUpdateProgressAdapter;

    /**
     * Construct the factory using the singleton GUI based update
     * progress adapter.
     * @param visibleUpdateProgressAdapter the default GUI UPA.
     */
    public DefaultUpdateProgressAdapterFactory(final UpdateProgressAdapter visibleUpdateProgressAdapter) {
        this.mVisibleUpdateProgressAdapter = visibleUpdateProgressAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createVisibleUpdateProgressAdapter() {
        return mVisibleUpdateProgressAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createBackgroundUpdateProgressAdapter() {
        return new NullUpdateProgressAdapter();
    }
}
