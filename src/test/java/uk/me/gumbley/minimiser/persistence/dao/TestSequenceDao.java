package uk.me.gumbley.minimiser.persistence.dao;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;


/**
 * Tests the Sequence DAO
 * 
 * @author matt
 *
 */
public final class TestSequenceDao extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestSequenceDao.class);

    /**
     * Is the SEQUENCE usable??
     */
    @Test
    public void checkSequenceIncrements() { 
        LOGGER.info(">>> checkSequenceIncrements");
        final String dbName = "checksequenceincrements";
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, "", new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDatabase openedDatabase) {
                final SequenceDao sequenceDao = openedDatabase.getSequenceDao();
                long last = -1;
                for (int i = 0; i < 40; i++) {
                    final long start = System.currentTimeMillis();
                    final long seq = sequenceDao.getNextSequence();
                    final long stop = System.currentTimeMillis();
                    final long duration = stop - start;
                    LOGGER.info(String.format("Sequence value #%d is %d (took %d ms to generate)",
                        (i + 1), seq, duration));
                    Assert.assertTrue(seq > 0);
                    Assert.assertTrue(seq > last);
                    last = seq;
                }
            }
            
        });
        LOGGER.info("<<< checkSequenceIncrements");
    }
}
