package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;

/**
 * A ChangeLogTransformer that gives back its input verbatim, for tests.
 * @author matt
 *
 */
public final class NullChangeLogTransformer implements ChangeLogTransformer {

    /**
     * {@inheritDoc}
     */
    public String readFileSubsection(final String currentVersion, 
            final String latestVersion, final File changeLogFile) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
