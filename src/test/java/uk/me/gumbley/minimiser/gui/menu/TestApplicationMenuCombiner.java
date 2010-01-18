package uk.me.gumbley.minimiser.gui.menu;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * Tests for the combining of global application menu and database-specific application menu.
 *
 * @author matt
 *
 */
public final class TestApplicationMenuCombiner {
    private ApplicationMenu mGlobalApplicationMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private ApplicationMenuCombiner mApplicationMenuCombiner;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mGlobalApplicationMenu = new ApplicationMenu();
        mOpenDatabaseList = new OpenDatabaseList();
        mApplicationMenuCombiner = new ApplicationMenuCombiner(mGlobalApplicationMenu, mOpenDatabaseList);
    }

    /**
     *
     */
    @Test
    public void emptyApplicationMenus() {
        Assert.assertNull(mOpenDatabaseList.getCurrentDatabase());

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        Assert.assertEquals(0, menu.getCustomMenus().size());
        for (final ApplicationMenu.SystemMenu systemMenu : ApplicationMenu.SystemMenu.values()) {
            final List<JComponent> systemMenuList = menu.getMenu(systemMenu);
            Assert.assertEquals(0, systemMenuList.size());
        }
    }

    /**
     *
     */
    @Test
    public void customMenusFromGlobalAreOrderedFirst() {
        mGlobalApplicationMenu.addCustomMenu(new JMenu("GlobalCustom"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addCustomMenu(new JMenu("DatabaseCustom"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);
        Assert.assertNotNull(mOpenDatabaseList.getCurrentDatabase());

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JMenu> customMenus = menu.getCustomMenus();
        Assert.assertEquals(2, customMenus.size());
        Assert.assertEquals("GlobalCustom", customMenus.get(0).getText());
        Assert.assertEquals("DatabaseCustom", customMenus.get(1).getText());
    }

    private void setCurrentDatabaseApplicationMenu(
            final ApplicationMenu applicationMenu) {
        final DatabaseDescriptor descriptor = new DatabaseDescriptor("FOO");
        descriptor.setAttribute(AttributeIdentifier.ApplicationMenu, applicationMenu);
        mOpenDatabaseList.addOpenedDatabase(descriptor);
    }

    /**
     *
     */
    @Test
    public void separatorsBetweenGlobalAndDatabaseAreCoalesced() {
        mGlobalApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JSeparator());
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JSeparator());
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(1, fileMenu.size());
        Assert.assertTrue(fileMenu.get(0) instanceof JSeparator);
    }

    /**
     *
     */
    @Test
    public void menuItemsFromGlobalAreOrderedFirst() {
        mGlobalApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("GlobalMenuItem"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("DatabaseMenuItem"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(2, fileMenu.size());
        Assert.assertEquals("GlobalMenuItem", ((JMenuItem) fileMenu.get(0)).getText());
        Assert.assertEquals("DatabaseMenuItem", ((JMenuItem) fileMenu.get(1)).getText());

    }

    /**
     *
     */
    @Test
    public void closingDatabaseJustYieldsGlobal() {
        mGlobalApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("GlobalMenuItem"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("DatabaseMenuItem"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);
        final DatabaseDescriptor currentDatabase = mOpenDatabaseList.getCurrentDatabase();
        Assert.assertNotNull(currentDatabase);
        mOpenDatabaseList.removeClosedDatabase(currentDatabase);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(1, fileMenu.size());
        Assert.assertEquals("GlobalMenuItem", ((JMenuItem) fileMenu.get(0)).getText());
    }
}
