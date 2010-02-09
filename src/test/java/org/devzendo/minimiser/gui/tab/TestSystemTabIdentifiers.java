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
