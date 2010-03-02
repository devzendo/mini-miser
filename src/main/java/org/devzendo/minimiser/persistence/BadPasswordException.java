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

package org.devzendo.minimiser.persistence;

/**
 * A BadPasswordException is thrown when an attempt is made to access
 * an encrypted database for migration but the wrong password is
 * supplied.
 * 
 * @author matt
 *
 */
public class BadPasswordException extends RuntimeException {
    private static final long serialVersionUID = -3378063389975036678L;

    /**
     * Couldn't open database due to bad password
     * @param message helpful text
     */
    public BadPasswordException(final String message) {
        super(message);
    }

}
