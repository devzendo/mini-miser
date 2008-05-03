package uk.me.gumbley.minimiser.persistence;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * @author matt
 *
 */
public final class TestAccessFactory extends PersistenceTestCase {
    private static final String WRONG_PASSWORD = "wrong password";
    private static final String DIRECTORY_FOR_DB = "directory for db";

    /**
     * @throws Exception because the password is bad
     */
    @Test(expected = BadPasswordException.class)
    public void testBadPasswordFailsToLoad() throws Exception {
        // SpringLoader sl = getSpringLoader();
        // AccessFactory aF = sl.getBean("accessFactory", AccessFactory.class);
        // set up mock JDBC layer that throws up
        JdbcAccess jA = EasyMock.createMock(JdbcAccess.class);
        EasyMock.expect(jA.openDatabase(DIRECTORY_FOR_DB, WRONG_PASSWORD))
                .andThrow(new BadPasswordException());
        EasyMock.replay(jA);
        // Now create the real access factory - no hibernate access needed for
        // this test
        HibernateAccess hA = null;
        AccessFactory accessFactory = new AccessFactoryImpl(jA, hA);
        accessFactory.openMigratableDatabase(DIRECTORY_FOR_DB,
            WRONG_PASSWORD);
    }
}
