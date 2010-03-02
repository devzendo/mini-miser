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

package org.devzendo.minimiser.persistence.sql;

import java.sql.SQLException;

/**
 * Thrown by the SQLAccess parse method when the supplied SQL is malformed.
 * @author matt
 *
 */
public final class BadSQLException extends SQLAccessException {
    private static final long serialVersionUID = 7811380206297578786L;

    /**
     * Construct an exception with a message and a cause
     * @param message the message
     * @param cause the causing exception
     */
    public BadSQLException(final String message, final SQLException cause) {
        super(message, cause);
    }
}
