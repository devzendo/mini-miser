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

package org.devzendo.minimiser.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Assert;

/**
 * Various toolkit methods to help with files in test cases.
 * 
 * @author matt
 *
 */
public final class FileUnittestHelper {
    private FileUnittestHelper() {
        // no instances
    }

    /**
     * Corrupt a (database) by writing over the 2nd kilobyte of data. This
     * will result in an open failure due to checksum mismatch.
     * 
     * @param dbFile the file to munge
     */
    public static void corruptFile(final File dbFile) {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(dbFile, "rw");
            raf.seek(1024);
            raf.writeChars("Let's write all over the database, to see if it'll fail to open!");
            raf.close();
        } catch (final FileNotFoundException e) {
            Assert.fail("Got a file not found!: " + e.getMessage());
        } catch (final IOException e) {
            Assert.fail("Got an IOexception!: " + e.getMessage());
        }
    }
}
