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

package org.devzendo.minimiser.plugin;

import org.devzendo.commonapp.spring.springloader.SpringLoader;


/**
 * An abstract base plugin type that you can extend to provide
 * default (i.e. empty) before/after plugin resolution lists.
 * Also handles storage of the provided SpringLoader.
 *
 * @author matt
 *
 */
public abstract class AbstractPlugin implements Plugin {
    private SpringLoader mSpringLoader;

    /**
     * {@inheritDoc}
     */
    public final SpringLoader getSpringLoader() {
        if (mSpringLoader == null) {
            throw new IllegalStateException("Cannot use the SpringLoader as it has not been set");
        }
        return mSpringLoader;
    }

    /**
     * {@inheritDoc}
     */
    public final void setSpringLoader(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }
}
