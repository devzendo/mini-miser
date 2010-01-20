package org.devzendo.minimiser.persistence.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


/**
 * A VersionsDao using SimpleJdbcTemplate
 *
 * @author matt
 *
 */
public final class JdbcTemplateVersionsDao implements VersionsDao {
    private final SimpleJdbcTemplate jdbcTemplate;

    /**
     * @param template the template used for access
     */
    public JdbcTemplateVersionsDao(final SimpleJdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    /**
     * {@inheritDoc}
     */
    public Version findVersion(final String pluginName, final String entity) throws DataAccessException {
        final String sql = "SELECT plugin, entity, isapplication, version FROM Versions WHERE plugin = ? AND entity = ?";
        final ParameterizedRowMapper<Version> mapper = new ParameterizedRowMapper<Version>() {

            // notice the return type with respect to Java 5 covariant return types
            public Version mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                final Version version = new Version();
                version.setPluginName(rs.getString("plugin"));
                version.setEntity(rs.getString("entity"));
                version.setIsApplication(rs.getBoolean("isapplication"));
                version.setVersion(rs.getString("version"));
                return version;
            }
        };
        return jdbcTemplate.queryForObject(sql, mapper, pluginName, entity);
    }

    /**
     * {@inheritDoc}
     */
    public void persistVersion(final Version version) throws DataAccessException {
        final int count = this.jdbcTemplate.queryForInt(
            "SELECT COUNT(0) FROM Versions WHERE plugin = ? AND entity = ?",
            new Object[]{version.getPluginName(), version.getEntity()});
        if (count == 0) {
            jdbcTemplate.update(
                "INSERT INTO Versions (plugin, entity, isapplication, version) VALUES (?, ?, ?, ?)",
                new Object[] {version.getPluginName(), version.getEntity(), version.isApplication(), version.getVersion()});
        } else {
            jdbcTemplate.update(
                "UPDATE Versions SET version = ? WHERE plugin = ? AND entity = ?",
                new Object[] {version.getVersion(), version.getPluginName(), version.getEntity()});
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(final String plugin, final String entity) throws DataAccessException {
        final int count = this.jdbcTemplate.queryForInt(
            "SELECT COUNT(0) FROM Versions WHERE plugin = ? AND entity = ?",
            new Object[]{plugin, entity});
        return count == 1;
    }
}
