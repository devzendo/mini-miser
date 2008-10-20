package uk.me.gumbley.minimiser.persistence.dao.impl;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import uk.me.gumbley.minimiser.persistence.dao.SequenceDao;

/**
 * A SequenceDao using SimpleJdbcTemplate.
 * 
 * @author matt
 *
 */
public final class JdbcTemplateSequenceDao implements SequenceDao {
    private final SimpleJdbcTemplate jdbcTemplate;

    /**
     * @param template the template used for access
     */
    public JdbcTemplateSequenceDao(final SimpleJdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    /**
     * {@inheritDoc}
     */
    public long getNextSequence() {
        return jdbcTemplate.queryForLong(
            "select next value for sequence Sequence",
            new Object[0]);
    }
}
