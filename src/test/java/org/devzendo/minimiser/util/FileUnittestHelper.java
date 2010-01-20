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
