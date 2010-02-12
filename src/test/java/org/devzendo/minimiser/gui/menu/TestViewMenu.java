package org.devzendo.minimiser.gui.menu;

import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;

import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests written after the development of ViewMenu - it wasn't done with TDD.
 *
 * @author matt
 *
 */
public final class TestViewMenu {

    private ViewMenu mViewMenu;
    private OpenDatabaseList mOpenDatabaseList;

    /**
     * @throws IOException
     */
    @Before
    public void getPrerequisites() throws IOException {
        final MenuWiring menuWiring = new MenuWiring();
        mOpenDatabaseList = new OpenDatabaseList();
        final OpenTabList openTabList = new OpenTabList();
        final Prefs prefs = createPrefs();
        mViewMenu = new ViewMenu(menuWiring, mOpenDatabaseList, openTabList, prefs);
    }

    private Prefs createPrefs() throws IOException {
        final Prefs prefs = TestPrefs.createUnitTestPrefsFile();
        final File prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        return prefs;
    }

    /**
     *
     */
    @Test
    public void menuDisabledIfNoDatabases() {
        mViewMenu.rebuildMenuGroup();
        Assert.assertFalse(mViewMenu.getJMenu().isEnabled());
    }

    /**
     *
     */
    @Test
    public void nonPermanentSystemTabIdentifiersAllPresent() {
        mOpenDatabaseList.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        mViewMenu.rebuildMenuGroup();
        final JMenu jMenu = mViewMenu.getJMenu();
        Assert.assertTrue(jMenu.isEnabled());
        final int numberOfNonPermanentTabIdentifiers =
            SystemTabIdentifiers.values().length
            - SystemTabIdentifiers.getPermanentTabIdentifiers().size();
        Assert.assertEquals(numberOfNonPermanentTabIdentifiers, jMenu.getMenuComponentCount());
    }

    @Test
    public void userTabIdentifierPresent() {
        Assert.fail("unfinished");
    }

    @Test
    public void globalTabIdentifierPresent() {
        Assert.fail("unfinished");
    }
}
