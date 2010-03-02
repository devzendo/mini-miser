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

import java.io.IOException;

/**
 * @author matt
 *
 */
public final class StubUpdateProgressAdapter implements UpdateProgressAdapter {

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
    }

    /**
     * {@inheritDoc}
     */
    public void alreadyCheckedToday() {
    }

    /**
     * {@inheritDoc}
     */
    public void checkStarted() {
    }

    /**
     * {@inheritDoc}
     */
    public void noApplicationVersionDeclared() {
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateURLDeclared() {
    }

    /**
     * {@inheritDoc}
     */
    public void commsFailure(final IOException exception) {
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateAvailable() {
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final IOException exception) {
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final ParseException exception) {
    }

    /**
     * {@inheritDoc}
     */
    public void finished() {
    }
}
