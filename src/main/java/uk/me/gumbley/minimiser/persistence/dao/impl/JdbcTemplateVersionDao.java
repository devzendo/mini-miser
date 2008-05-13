package uk.me.gumbley.minimiser.persistence.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;

public class JdbcTemplateVersionDao implements VersionDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateVersionDao(final JdbcTemplate template) {
        this.jdbcTemplate = template;
        
    }
    public Version findVersion(String entity) throws DataAccessException {
        // WOZERE write the finder using JdbcTemplate
        return null;
    }

    public void persistVersion(Version version) throws DataAccessException {
        // TODO Auto-generated method stub
    }
}
