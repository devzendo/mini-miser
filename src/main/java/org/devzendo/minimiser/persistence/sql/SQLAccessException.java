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
 * Thrown by the SQLAccess subsystem, or by the getSQLAccess method when
 * the SQLAccess subsystem is at fault.
 * @author matt
 *
 */
public class SQLAccessException extends RuntimeException {
    private static final long serialVersionUID = 2756943237189928339L;

    /**
     * Construct an exception with a message and a cause
     * @param message the message
     * @param cause the causing exception
     */
    public SQLAccessException(final String message, final SQLException cause) {
        super(message, cause);
    }

    /**
     * Construct an exception with just a message
     * @param message the message
     */
    public SQLAccessException(final String message) {
        super(message);
    }
}
