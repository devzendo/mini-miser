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

package org.devzendo.minimiser.persistence.dao;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.persistence.DummyAppPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests the Sequence DAO
 * 
 * @author matt
 *
 */
public final class TestSequenceDao extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestSequenceDao.class);

    /**
     * Is the SEQUENCE usable??
     */
    @Test
    public void checkSequenceIncrements() { 
        LOGGER.info(">>> checkSequenceIncrements");
        final String dbName = "checksequenceincrements";
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, "", new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory openedDatabase) {
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
