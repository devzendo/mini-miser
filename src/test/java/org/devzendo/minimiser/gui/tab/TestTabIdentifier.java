package org.devzendo.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests for TabIdentifier
 * @author matt
 *
 */
public final class TestTabIdentifier extends LoggingTestCase {
    /**
     *
     */
    @Test
    public void verifyTabSystemflag() {
        Assert.assertTrue(TabIdentifier.SQL.isSystemTab());
        Assert.assertTrue(TabIdentifier.OVERVIEW.isSystemTab());
        Assert.assertTrue(TabIdentifier.CATEGORIES.isSystemTab());
    }

    /**
     *
     */
    @Test
    public void systemTabsCompareEarlierThanPluginTabs() {
        final TabIdentifier system = new TabIdentifier("FOO", "Foo", true, 'F', true);
        final TabIdentifier plugin = new TabIdentifier("FOO", "Foo", true, 'F', false);
        Assert.assertTrue(system.compareTo(plugin) == -1);
        Assert.assertTrue(plugin.compareTo(system) == 1);
    }

    /**
     *
     */
    @Test
    public void tabsDoNotCompareOnTabNames() {
        final TabIdentifier one = new TabIdentifier("A", "Test", true, 'A', true);
        final TabIdentifier two = new TabIdentifier("B", "Test", true, 'A', true);
        Assert.assertTrue(one.compareTo(two) == 0);
        Assert.assertTrue(two.compareTo(one) == 0);
    }

    /**
     *
     */
    @Test
    public void pluginTabsCompareAlphabeticallyOnDisplayNames() {
        final TabIdentifier alpha = new TabIdentifier("A", "Alpha", true, 'F');
        final TabIdentifier bravo = new TabIdentifier("A", "Bravo", true, 'F');
        Assert.assertTrue(alpha.compareTo(bravo) == -1);
        Assert.assertTrue(bravo.compareTo(alpha) == 1);
    }

    /**
     *
     */
    @Test
    public void systemTabsCompareAlphabeticallyOnDisplayNames() {
        final TabIdentifier alpha = new TabIdentifier("X", "Alpha", true, 'F', true);
        final TabIdentifier bravo = new TabIdentifier("X", "Bravo", true, 'F', true);
        Assert.assertTrue(alpha.compareTo(bravo) == -1);
        Assert.assertTrue(bravo.compareTo(alpha) == 1);
    }

    /**
     *
     */
    @Test
    public void tabsWithEqualDisplayNamesCompareOnMnemonics() {
        final TabIdentifier one = new TabIdentifier("TEST", "Test", true, 'A', true);
        final TabIdentifier two = new TabIdentifier("TEST", "Test", true, 'B', true);
        Assert.assertTrue(one.compareTo(two) == -1);
        Assert.assertTrue(two.compareTo(one) == 1);
    }

    /**
     *
     */
    @Test
    public void tabsWithSameTabNamesAreEquivalent() {
        final TabIdentifier one = new TabIdentifier("TEST", "Bo!", false, 'Z', false);
        final TabIdentifier two = new TabIdentifier("TEST", "Test", true, 'B', true);
        Assert.assertEquals(one, two);
        Assert.assertEquals(two, one);
    }

    /**
     *
     */
    @Test
    public void tabsWithOnlyDifferentTabNamesAreDifferent() {
        final TabIdentifier one = new TabIdentifier("TESTY", "Test", true, 'B', true);
        final TabIdentifier two = new TabIdentifier("TEST", "Test", true, 'B', true);
        Assert.assertFalse(one.equals(two));
        Assert.assertFalse(two.equals(one));
    }

    /**
     *
     */
    @Test
    public void verifyTabPermanence() {
        Assert.assertFalse(TabIdentifier.SQL.isTabPermanent());
        Assert.assertTrue(TabIdentifier.OVERVIEW.isTabPermanent());

        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers.getPermanentTabIdentifiers();
        Assert.assertEquals(1, permanentTabIdentifiers.size());

        Assert.assertEquals(TabIdentifier.OVERVIEW, permanentTabIdentifiers.get(0));
    }

    /**
     *
     */
    @Test
    public void verifyTabMnemonics() {
        for (final TabIdentifier tabId : TabIdentifier.values()) {
            final char mnemonic = tabId.getMnemonic();
            Assert.assertTrue(Character.isLetterOrDigit(mnemonic));
        }
    }

    /**
     *
     */
    @Test
    public void translateNullArrayOfNames() {
        final List<TabIdentifier> list = TabIdentifierToolkit.toTabIdentifiersFromTabNames(null);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    /**
     *
     */
    @Test
    public void translateEmptyArrayOfNames() {
        final List<TabIdentifier> list = TabIdentifierToolkit.toTabIdentifiersFromTabNames(new String[0]);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    /**
     *
     */
    @Test
    public void translateName() {
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName(null));
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName(""));
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName("wow!"));

        final TabIdentifier sqlId = TabIdentifierToolkit.toTabIdentifierFromTabName("SQL");
        Assert.assertNotNull(sqlId);
        Assert.assertSame(TabIdentifier.SQL, sqlId);

        final TabIdentifier overviewId = TabIdentifierToolkit.toTabIdentifierFromTabName("OVERVIEW");
        Assert.assertNotNull(overviewId);
        Assert.assertSame(TabIdentifier.OVERVIEW, overviewId);
    }

    /**
     *
     */
    @Test
    public void translateDisplayName() {
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName(null));
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName(""));
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName("wow!"));

        final TabIdentifier sqlId = TabIdentifierToolkit.toTabIdentifierFromDisplayName("SQL");
        Assert.assertNotNull(sqlId);
        Assert.assertSame(TabIdentifier.SQL, sqlId);

        final TabIdentifier overviewId = TabIdentifierToolkit.toTabIdentifierFromDisplayName("Overview");
        Assert.assertNotNull(overviewId);
        Assert.assertSame(TabIdentifier.OVERVIEW, overviewId);

        final TabIdentifier categoriesId = TabIdentifierToolkit.toTabIdentifierFromDisplayName("Categories");
        Assert.assertNotNull(categoriesId);
        Assert.assertSame(TabIdentifier.CATEGORIES, categoriesId);
    }

    /**
     *
     */
    @Test
    public void translateIds() {
        final String[] in = new String[] {
                "SQL", "styrofoam", "OVERVIEW"
        };
        final List<TabIdentifier> out = TabIdentifierToolkit.toTabIdentifiersFromTabNames(in);
        Assert.assertNotNull(out);
        Assert.assertEquals(2, out.size());
        Assert.assertEquals(TabIdentifier.SQL, out.get(0));
        Assert.assertEquals(TabIdentifier.OVERVIEW, out.get(1));
    }

    /**
     *
     */
    @Test
    public void translateIdsNoSortNoDeDupe() {
        final String[] in = new String[] {
                "OVERVIEW", "SQL", "Overview", "SQL", "OVERVIEW"
        };
        final List<TabIdentifier> out = TabIdentifierToolkit.toTabIdentifiersFromTabNames(in);
        Assert.assertNotNull(out);
        Assert.assertEquals(4, out.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, out.get(0));
        Assert.assertEquals(TabIdentifier.SQL, out.get(1));
        Assert.assertEquals(TabIdentifier.SQL, out.get(2));
        Assert.assertEquals(TabIdentifier.OVERVIEW, out.get(3));
    }

    /**
     *
     */
    @Test
    public void dontBotherTranslatingNamesWithModifiedCase() {
        Assert.assertNull(TabIdentifierToolkit.toTabIdentifierFromTabName("Overview"));
    }

    /**
     *
     */
    @Test
    public void sortAndDeDupeNames() {
        final List<TabIdentifier> nothing = TabIdentifierToolkit.sortAndDeDupe(null);
        Assert.assertNotNull(nothing);
        Assert.assertEquals(0, nothing.size());

        final List<TabIdentifier> nothingMore = TabIdentifierToolkit.sortAndDeDupe(Collections.<String>emptyList());
        Assert.assertNotNull(nothingMore);
        Assert.assertEquals(0, nothingMore.size());

        final String[] in = new String[] {
                "OVERVIEW", "SQL", "Overview", "SQL", "OVERVIEW"
        };
        final List<TabIdentifier> out = TabIdentifierToolkit.sortAndDeDupe(Arrays.asList(in));
        Assert.assertNotNull(out);
        Assert.assertEquals(2, out.size());
        Assert.assertSame(TabIdentifier.SQL, out.get(0));
        Assert.assertSame(TabIdentifier.OVERVIEW, out.get(1));
    }

    /**
     *
     */
    @Test
    public void sortAndDeDupeTabIdentifiers() {
        final List<TabIdentifier> nothing = TabIdentifierToolkit.sortAndDeDupeTabIdentifiers(null);
        Assert.assertNotNull(nothing);
        Assert.assertEquals(0, nothing.size());

        final List<TabIdentifier> nothingMore = TabIdentifierToolkit.sortAndDeDupeTabIdentifiers(Collections.<TabIdentifier>emptyList());
        Assert.assertNotNull(nothingMore);
        Assert.assertEquals(0, nothingMore.size());

        final TabIdentifier[] in = new TabIdentifier[] {
                TabIdentifier.OVERVIEW, TabIdentifier.SQL, TabIdentifier.SQL, TabIdentifier.OVERVIEW
        };
        final List<TabIdentifier> out = TabIdentifierToolkit.sortAndDeDupeTabIdentifiers(Arrays.asList(in));
        Assert.assertNotNull(out);
        Assert.assertEquals(2, out.size());
        Assert.assertSame(TabIdentifier.SQL, out.get(0));
        Assert.assertSame(TabIdentifier.OVERVIEW, out.get(1));
    }

    /**
     *
     */
    @Test
    public void translateToDisplayNames() {
        Assert.assertNotNull(TabIdentifierToolkit.toDisplayNames(null));
        Assert.assertEquals(0, TabIdentifierToolkit.toDisplayNames(null).length);

        Assert.assertNotNull(TabIdentifierToolkit.toDisplayNames(new ArrayList<TabIdentifier>()));
        Assert.assertEquals(0, TabIdentifierToolkit.toDisplayNames(new ArrayList<TabIdentifier>()).length);

        final TabIdentifier[] tabIds = new TabIdentifier[] {
                TabIdentifier.OVERVIEW, TabIdentifier.SQL
        };
        final String[] tabNames = TabIdentifierToolkit.toDisplayNames(Arrays.asList(tabIds));
        Assert.assertNotNull(tabNames);
        Assert.assertEquals(2, tabNames.length);
        Assert.assertEquals("Overview", tabNames[0]);
        Assert.assertEquals("SQL", tabNames[1]);
    }

    /**
     *
     */
    @Test
    public void thereArePermanentTabs() {
        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers.getPermanentTabIdentifiers();
        Assert.assertNotNull(permanentTabIdentifiers);
        Assert.assertTrue(permanentTabIdentifiers.size() > 0);
    }

}
