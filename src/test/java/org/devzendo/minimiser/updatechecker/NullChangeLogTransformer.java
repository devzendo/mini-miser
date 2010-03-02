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

package org.devzendo.minimiser.updatechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.devzendo.minimiser.version.ComparableVersion;


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
