/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.persistence.dao.impl;

import org.devzendo.minimiser.persistence.dao.SequenceDao;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

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
