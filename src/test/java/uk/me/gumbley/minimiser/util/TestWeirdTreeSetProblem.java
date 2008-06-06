package uk.me.gumbley.minimiser.util;

import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.util.DelayedExecutor.Executable;
import uk.me.gumbley.minimiser.util.DelayedExecutor.ExecutableComparator;


public class TestWeirdTreeSetProblem extends LoggingTestCase {
    private static final int ENTRIES = 100;
    private static final Logger LOGGER = Logger
            .getLogger(TestWeirdTreeSetProblem.class);
    @Test
    public void testTreeSetSaysContainedWhenItDoesnt() {
        TreeSet<Executable> treeSet = new TreeSet<Executable>(new ExecutableComparator());
        for (int i=0; i<ENTRIES; i++) {
            String key = "test" + i;
            Executable executable = new Executable(key, 1, null);
            if (treeSet.contains(executable)) {
                final String message = "TreeSet contains " + key + " when it shouldn't";
                LOGGER.error(message);
                Assert.fail(message);
            }
            LOGGER.info("Adding " + executable);
            treeSet.add(executable);
        }
        Assert.assertTrue(ENTRIES == treeSet.size());
    }
    
    @Test
    public void testEquality() {
        Executable[] executables = new Executable[ENTRIES];
        for (int i=0; i<ENTRIES; i++) {
            executables[i] = new Executable("test" + i, 1, null);
        }
        for (int i=0; i<ENTRIES; i++) {
            for (int j=0; j< ENTRIES; j++) {
                if (i == j) {
                    continue;
                }
                if (executables[i].equals(executables[j])) {
                    String message = executables[i] + " == " + executables[j] + " which is wrong";
                    LOGGER.error(message);
                    Assert.fail(message);
                }
            }
        }
    }
}
