package uk.me.gumbley.minimiser.persistence;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.persistence.dao.SequenceDao;


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
                final long seq1 = sequenceDao.getNextSequence();
                LOGGER.info(String.format("Sequence value is initially %d", seq1));
                Assert.assertTrue(seq1 != 0);
                //
                final long seq2 = sequenceDao.getNextSequence();
                LOGGER.info(String.format("Sequence value is then %d", seq2));
                Assert.assertTrue(seq1 < seq2);
            }
            
        });
        LOGGER.info("<<< checkSequenceIncrements");
    }
}
