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

package org.devzendo.minimiser.gui.dialog.problem;




/**
 * A problem reporter that can be used by unit tests.
 * 
 * @author matt
 */
public final class StubProblemReporter implements ProblemReporter {
    private String doing;
    private Exception exception;

    /**
     * {@inheritDoc}
     */
    public void reportProblem(final String whileDoing) {
        this.doing = whileDoing;
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exc) {
        doing = whileDoing;
        exception = exc;
    }

    /**
     * Obtain the text of what the app was doing when the problem
     * occurred.
     * 
     * @return the "doing" text.
     */
    public String getDoing() {
        return doing;
    }

    /**
     * Obtain the exception that occurred when the app was doing
     * something.
     * 
     * @return the exception, or null if the problem wasn't caused by
     * an exception.
     */
    public Exception getException() {
        return exception;
    }
}
