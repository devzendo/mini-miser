package uk.me.gumbley.minimiser.persistence;

import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;

/**
 * Tests that the minimiser database descriptor stores a database as an
 * attribute.
 * 
 * @author matt
 *
 */
public final class TestMiniMiserDatabaseDescriptor extends PersistenceUnittestCase {
    /**
     * Tests that the minimiser database descriptor stores a database as an
     * attribute. 
     */
    @Test
    public void miniMiserDatabaseDescriptorStoresDatabaseAsAttribute() {
        final MiniMiserDatabase mmd = new MiniMiserDatabase() {
            public void close() {
                // TODO Auto-generated method stub
            }

            public VersionDao getVersionDao() {
                // TODO Auto-generated method stub
                return null;
            }

            public boolean isClosed() {
                // TODO Auto-generated method stub
                return false;
            }
        };
        
        final MiniMiserDatabaseDescriptor mmdd = new MiniMiserDatabaseDescriptor("foo", "/tmp/foo", mmd);
        Assert.assertSame(mmd, mmdd.getDatabase());
    }
}
