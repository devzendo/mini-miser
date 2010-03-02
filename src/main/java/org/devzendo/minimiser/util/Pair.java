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

/**
 * A Pair allows a pair of objects of specific types to be
 * returned from some method.
 *
 * @author matt
 *
 * @param <F> the type of the first object
 * @param <S> the type of the second object
 */
public final class Pair<F, S> {
    private final F mFirst;
    private final S mSecond;

    /**
     * @param first the first object
     * @param second the second object
     */
    public Pair(final F first, final S second) {
        mFirst = first;
        mSecond = second;
    }

    /**
     * @return the first
     */
    public F getFirst() {
        return mFirst;
    }

    /**
     * @return the second
     */
    public S getSecond() {
        return mSecond;
    }
}
