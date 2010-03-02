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

import org.apache.log4j.Logger;

/**
 * The default ProblemReporter that currently calls through to the
 * static methods of ProblemDialog.
 * 
 * @author matt
 *
 */
public final class DefaultProblemReporterImpl implements ProblemReporter {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultProblemReporterImpl.class);
    /**
     * Create the problem reporter
     */
    public DefaultProblemReporterImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(final String whileDoing) {
        LOGGER.error("Error occurred " + whileDoing);
        ProblemDialogHelper.reportProblem(whileDoing);
    }

    /**
     * {@inheritDoc}
     */
    public void reportProblem(
            final String whileDoing,
            final Exception exception) {
        LOGGER.error("Error occurred " + whileDoing, exception);
        ProblemDialogHelper.reportProblem(whileDoing, exception);
    }
}
