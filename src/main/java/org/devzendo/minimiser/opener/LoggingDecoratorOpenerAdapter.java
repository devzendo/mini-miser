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

package org.devzendo.minimiser.opener;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;


/**
 * An OpenerAdapter that decorates an existing OpenerAdapter,
 * logging all calls made prior to passing them on to the decorated
 * OpenerAdapter.
 * 
 * @author matt
 *
 */
public final class LoggingDecoratorOpenerAdapter implements OpenerAdapter {
    private static final Logger LOGGER = Logger
            .getLogger(LoggingDecoratorOpenerAdapter.class);
    private final OpenerAdapter mOpenerAdapter;

    /**
     * Decorate a given OpenerAdapter
     * @param adapter the OpenerAdapter to decorate.
     */
    public LoggingDecoratorOpenerAdapter(final OpenerAdapter adapter) {
        mOpenerAdapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    public void databaseNotFound(final DataAccessResourceFailureException exception) {
        LOGGER.warn("Database not found: " + exception.getMessage());
        mOpenerAdapter.databaseNotFound(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void reportProgress(final ProgressStage progressStage, final String description) {
        LOGGER.info("Progress: " + progressStage + ": " + description);
        mOpenerAdapter.reportProgress(progressStage, description);
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestMigration() {
        LOGGER.info("Requesting migration");
        final boolean requestMigration = mOpenerAdapter.requestMigration();
        LOGGER.info("Result of migration request: " + requestMigration);
        return requestMigration;
    }
    
    /**
     * {@inheritDoc}
     */
    public void migrationNotPossible() {
        LOGGER.warn("Migration not possible");
        mOpenerAdapter.migrationNotPossible();
    }

    /**
     * {@inheritDoc}
     */
    public void createdByOtherApplication() {
        LOGGER.warn("Could not open database created by another application");
        mOpenerAdapter.createdByOtherApplication();
    }

    /**
     * {@inheritDoc}
     */
    public void noApplicationPluginAvailable() {
        LOGGER.warn("No application plugin available; cannot check which application created this database");
        mOpenerAdapter.noApplicationPluginAvailable();
    }

    /**
     * {@inheritDoc}
     */
    public void migrationFailed(final DataAccessException exception) {
        LOGGER.warn("Migration failure: " + exception.getMessage());
        mOpenerAdapter.migrationFailed(exception);
    }

    /**
     * {@inheritDoc}
     */
    public String requestPassword() {
        LOGGER.info("Requesting password");
        final String requestPassword = mOpenerAdapter.requestPassword();
        LOGGER.info("Result of password request: " + StringUtils.maskSensitiveText(requestPassword));
        return requestPassword;
    }

    /**
     * {@inheritDoc}
     */
    public void seriousProblemOccurred(final DataAccessException exception) {
        LOGGER.warn("Serious problem occurred: " + exception.getMessage());
        mOpenerAdapter.seriousProblemOccurred(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void startOpening() {
        LOGGER.info("Start opening");
        mOpenerAdapter.startOpening();
    }

    /**
     * {@inheritDoc}
     */
    public void stopOpening() {
        LOGGER.info("Stop opening");
        mOpenerAdapter.stopOpening();
    }
}
