package uk.me.gumbley.minimiser.updatechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * A ChangeLogTransformer that gives back its input verbatim, for tests.
 * @author matt
 *
 */
public final class NullChangeLogTransformer implements ChangeLogTransformer {

    private IOException readException = null;

    /**
     * {@inheritDoc}
     */
    public String readFileSubsection(final ComparableVersion currentVersion, 
            final ComparableVersion latestVersion, final File changeLogFile) throws IOException {
        if (readException != null) {
            throw readException;
        }
        
        final StringBuilder sb = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(changeLogFile));
        try {
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return sb.toString();
    }
    
    /**
     * Drive a transformation failure
     */
    public void injectReadFailure() {
        readException  = new IOException("Injected fault");
    }

    /**
     * {@inheritDoc}
     */
    public String readAllStream(final InputStream changeLogFile) throws IOException,
            ParseException {
        return null;
    }
}
