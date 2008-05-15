package uk.me.gumbley.minimiser.persistence.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;

/**
 * A VersionDao using SimpleJdbcTemplate
 * 
 * @author matt
 *
 */
public final class JdbcTemplateVersionDao implements VersionDao {
    private final SimpleJdbcTemplate jdbcTemplate;

    /**
     * @param template the template used for access
     */
    public JdbcTemplateVersionDao(final SimpleJdbcTemplate template) {
        this.jdbcTemplate = template;
    }
    
    /**
     * {@inheritDoc}
     */
    public Version findVersion(final String entity) throws DataAccessException {
        final String sql = "select entity, version from Versions where entity = ?";
        ParameterizedRowMapper<Version> mapper = new ParameterizedRowMapper<Version>() {
            
            // notice the return type with respect to Java 5 covariant return types
            public Version mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                Version version = new Version();
                version.setEntity(rs.getString("entity"));
                version.setVersion(rs.getString("version"));
                return version;
            }
        };
        return jdbcTemplate.queryForObject(sql, mapper, entity);
    }

    /**
     * {@inheritDoc}
     */
    public void persistVersion(final Version version) throws DataAccessException {
        int count = this.jdbcTemplate.queryForInt(
            "select count(0) from Versions where entity = ?",
            new Object[]{version.getEntity()});
        if (count == 0) {
            jdbcTemplate.update(
                "insert into Versions (entity, version) values (?, ?)",
                new Object[] {version.getEntity(), version.getVersion()});
        } else {
            jdbcTemplate.update(
                "update Versions set version = ? where entity = ?",
                new Object[] {version.getVersion(), version.getEntity()});
        }
    }
}
