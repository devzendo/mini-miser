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
 * A mechanism for reporting serious problems to the user.
 * In the real app, this is a dialog; in tests, a stub.
 * 
 * @author matt
 *
 */
public interface ProblemReporter {
    
    /**
     * Report a problem that has no exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     */
    void reportProblem(final String whileDoing);
    
    /**
     * Report a problem that has an exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     */
    void reportProblem(final String whileDoing, final Exception exception);
}
