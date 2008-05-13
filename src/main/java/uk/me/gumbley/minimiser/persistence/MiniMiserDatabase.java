package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.persistence.dao.VersionDao;

public interface MiniMiserDatabase {
    
    VersionDao getVersionDao();
    void close();
}
