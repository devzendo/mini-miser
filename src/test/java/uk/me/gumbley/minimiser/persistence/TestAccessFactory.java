package uk.me.gumbley.minimiser.persistence;

import org.easymock.EasyMock;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;

public class TestAccessFactory extends LoggingTestCase {
    @Test(expected=BadPasswordException.class)
    public void testBadPasswordFailsToLoad() throws Exception {
//        SpringLoader sl = getSpringLoader();
//        AccessFactory aF = sl.getBean("accessFactory", AccessFactory.class);
        
        // set up mock JDBC layer that throws up
        JdbcAccess jA = EasyMock.createMock(JdbcAccess.class);
        EasyMock.expect(jA.openDatabase("directory for db", "wrong password")).andThrow(new BadPasswordException());
        EasyMock.replay(jA);
        
        // Now create the real access factory - no hibernate access needed for this
        // test
        HibernateAccess hA = null;
        AccessFactory accessFactory = new AccessFactoryImpl(jA, hA);
        accessFactory.openMigratableDatabase("", "");
        
    }
}
