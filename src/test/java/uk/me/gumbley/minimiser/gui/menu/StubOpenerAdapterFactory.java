package uk.me.gumbley.minimiser.gui.menu;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;

/**
 * @author matt
 *
 */
public class StubOpenerAdapterFactory implements OpenerAdapterFactory {
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

            public void seriousProblemOccurred(final DataAccessException exception) {
            }

            public void startOpening() {
            }

            public void stopOpening() {
            }
        };
    }
}
