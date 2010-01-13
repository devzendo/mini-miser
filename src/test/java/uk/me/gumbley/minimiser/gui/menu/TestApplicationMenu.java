package uk.me.gumbley.minimiser.gui.menu;

import javax.swing.JMenu;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the validation of the ApplicationMenu.
 *
 * @author matt
 *
 */
public final class TestApplicationMenu {
    private ApplicationMenu mAppMenu;
    @Before
    public void getPrerequisites() {
        mAppMenu = new ApplicationMenu();
    }

    @Test(expected=IllegalStateException.class)
    public void fileCannotBeAddedAsCustomMenu() {
        final JMenu customMenu = new JMenu("File");
        mAppMenu.addCustomMenu(customMenu);
    }
}
