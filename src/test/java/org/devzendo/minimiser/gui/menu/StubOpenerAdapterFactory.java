package org.devzendo.minimiser.gui.menu;

import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.opener.OpenerAdapterFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;


/**
 * @author matt
 *
 */
public final class StubOpenerAdapterFactory implements OpenerAdapterFactory {
    /**
     * {@inheritDoc}
     */
    public OpenerAdapter createOpenerAdapter(final String databaseName) {
        return new OpenerAdapter() {
            public void databaseNotFound(final DataAccessResourceFailureException exception) {
            }

            public void reportProgress(final ProgressStage progressStage, final String description) {
            }

            public String requestPassword() {
                return "";
            }
            
            public boolean requestMigration() {
                return false;
            }

            public void migrationNotPossible() {
            }

            public void createdByOtherApplication() {
            }

            public void noApplicationPluginAvailable() {
            }

            public void migrationFailed(final DataAccessException exception) {
            }

            public void seriousProblemOccurred(final DataAccessException exception) {
            }

            public void startOpening() {
            }

            public void stopOpening() {
            }
        };
    }
}
