package uk.me.gumbley.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


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
    public void verifyTabPermanence() {
        Assert.assertFalse(TabIdentifier.SQL.isTabPermanent());
        Assert.assertTrue(TabIdentifier.OVERVIEW.isTabPermanent());
        
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        Assert.assertEquals(1, permanentTabIdentifiers.size());

        Assert.assertEquals(TabIdentifier.OVERVIEW, permanentTabIdentifiers.get(0));
    }
    
    /**
     * 
     */
    @Test
    public void translateNullArrayOfNames() {
        final List<TabIdentifier> list = TabIdentifier.toTabIdentifiers(null);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }
    
    /**
     * 
     */
    @Test
    public void translateEmptyArrayOfNames() {
        final List<TabIdentifier> list = TabIdentifier.toTabIdentifiers(new String[0]);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }
    
    /**
     * 
     */
    @Test
    public void translateId() {
        Assert.assertNull(TabIdentifier.toTabIdentifier(null));
        Assert.assertNull(TabIdentifier.toTabIdentifier(""));
        Assert.assertNull(TabIdentifier.toTabIdentifier("wow!"));
        
        final TabIdentifier sqlId = TabIdentifier.toTabIdentifier("SQL");
        Assert.assertNotNull(sqlId);
        Assert.assertSame(TabIdentifier.SQL, sqlId);

        final TabIdentifier overviewId = TabIdentifier.toTabIdentifier("OVERVIEW");
        Assert.assertNotNull(overviewId);
        Assert.assertSame(TabIdentifier.OVERVIEW, overviewId);
}

    /**
     * 
     */
    @Test
    public void translateIds() {
        final String[] in = new String[] {
                "SQL", "styrofoam", "OVERVIEW"
        };
        final List<TabIdentifier> out = TabIdentifier.toTabIdentifiers(in);
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
        final List<TabIdentifier> out = TabIdentifier.toTabIdentifiers(in);
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
        Assert.assertNull(TabIdentifier.toTabIdentifier("Overview"));
    }

    /**
     * 
     */
    @Test
    public void sortAndDeDupeNames() {
        final List<TabIdentifier> nothing = TabIdentifier.sortAndDeDupe(null);
        Assert.assertNotNull(nothing);
        Assert.assertEquals(0, nothing.size());
        
        final List<TabIdentifier> nothingMore = TabIdentifier.sortAndDeDupe(Collections.<String>emptyList());
        Assert.assertNotNull(nothingMore);
        Assert.assertEquals(0, nothingMore.size());
        
        final String[] in = new String[] {
                "OVERVIEW", "SQL", "Overview", "SQL", "OVERVIEW"
        };
        final List<TabIdentifier> out = TabIdentifier.sortAndDeDupe(Arrays.asList(in));
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
        final List<TabIdentifier> nothing = TabIdentifier.sortAndDeDupeTabIdentifiers(null);
        Assert.assertNotNull(nothing);
        Assert.assertEquals(0, nothing.size());
        
        final List<TabIdentifier> nothingMore = TabIdentifier.sortAndDeDupeTabIdentifiers(Collections.<TabIdentifier>emptyList());
        Assert.assertNotNull(nothingMore);
        Assert.assertEquals(0, nothingMore.size());
        
        final TabIdentifier[] in = new TabIdentifier[] {
                TabIdentifier.OVERVIEW, TabIdentifier.SQL, TabIdentifier.SQL, TabIdentifier.OVERVIEW
        };
        final List<TabIdentifier> out = TabIdentifier.sortAndDeDupeTabIdentifiers(Arrays.asList(in));
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
        Assert.assertNotNull(TabIdentifier.toDisplayNames(null));
        Assert.assertEquals(0, TabIdentifier.toDisplayNames(null).length);

        Assert.assertNotNull(TabIdentifier.toDisplayNames(new ArrayList<TabIdentifier>()));
        Assert.assertEquals(0, TabIdentifier.toDisplayNames(new ArrayList<TabIdentifier>()).length);

        final TabIdentifier[] tabIds = new TabIdentifier[] {
                TabIdentifier.OVERVIEW, TabIdentifier.SQL
        };
        final String[] tabNames = TabIdentifier.toDisplayNames(Arrays.asList(tabIds));
        Assert.assertNotNull(tabNames);
        Assert.assertEquals(2, tabNames.length);
        Assert.assertEquals("Overview", tabNames[0]);
        Assert.assertEquals("SQL", tabNames[1]);
    }

    /**
     * 
     */
    @Test
    public void translateToTabNames() {
        Assert.assertNotNull(TabIdentifier.toTabNames(null));
        Assert.assertEquals(0, TabIdentifier.toTabNames(null).length);

        Assert.assertNotNull(TabIdentifier.toTabNames(new ArrayList<TabIdentifier>()));
        Assert.assertEquals(0, TabIdentifier.toTabNames(new ArrayList<TabIdentifier>()).length);

        final TabIdentifier[] tabIds = new TabIdentifier[] {
                TabIdentifier.OVERVIEW, TabIdentifier.SQL
        };
        final String[] tabNames = TabIdentifier.toTabNames(Arrays.asList(tabIds));
        Assert.assertNotNull(tabNames);
        Assert.assertEquals(2, tabNames.length);
        Assert.assertEquals("OVERVIEW", tabNames[0]);
        Assert.assertEquals("SQL", tabNames[1]);
    }

    /**
     * 
     */
    @Test
    public void thereArePermanentTabs() {
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        Assert.assertNotNull(permanentTabIdentifiers);
        Assert.assertTrue(permanentTabIdentifiers.size() > 0);
    }
    
}
