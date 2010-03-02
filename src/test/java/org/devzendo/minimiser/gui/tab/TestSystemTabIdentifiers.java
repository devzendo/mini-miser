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

package org.devzendo.minimiser.gui.tab;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the System TabIdentifiers.
 *
 * @author matt
 *
 */
public final class TestSystemTabIdentifiers {
    /**
     *
     */
    @Test
    public void verifyTabPermanence() {
        Assert.assertFalse(SystemTabIdentifiers.CATEGORIES.isTabPermanent());
        Assert.assertFalse(SystemTabIdentifiers.SQL.isTabPermanent());
        Assert.assertTrue(SystemTabIdentifiers.OVERVIEW.isTabPermanent());
        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers
                .getPermanentTabIdentifiers();
        Assert.assertEquals(1, permanentTabIdentifiers.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW,
            permanentTabIdentifiers.get(0));
    }

    /**
     *
     */
    @Test
    public void canBeObtainedByName() {
        Assert.assertSame(SystemTabIdentifiers.CATEGORIES, SystemTabIdentifiers
            .valueOf("CATEGORIES"));
        Assert.assertSame(SystemTabIdentifiers.SQL, SystemTabIdentifiers
            .valueOf("SQL"));
        Assert.assertSame(SystemTabIdentifiers.OVERVIEW, SystemTabIdentifiers
            .valueOf("OVERVIEW"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotObtainANonexistantName() {
        SystemTabIdentifiers.valueOf("Mr. Bojangles");
    }
}
