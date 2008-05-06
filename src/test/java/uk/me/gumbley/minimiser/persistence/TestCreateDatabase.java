package uk.me.gumbley.minimiser.persistence;

import org.junit.Before;
import org.junit.Test;


public class TestCreateDatabase extends PersistenceTestCase {
    private HibernateAccess hibernateAccess;
    @Before
    public void getHibernateAccess() {
        hibernateAccess = getSpringLoader().getBean("hibernateAccess", HibernateAccess.class);
    }
    
    @Test
    public void testCreateV1Database() {
        
    }
}
