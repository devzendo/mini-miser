package uk.me.gumbley.minimiser.upgradedetector;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.dialog.welcome.WelcomeDialogHelper;

/**
 * Shows the Welcome Dialog upon detection of a fresh installation.
 * Shows the What's New In This Release Dialog upon detection of an upgrade
 * installation.
 * Is otherwise silent.
 * 
 * @author matt
 *
 */
public final class WelcomeMessageDisplayingListener implements Observer<UpgradeEvent> {

    /**
     */
    public WelcomeMessageDisplayingListener() {
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (observableEvent instanceof FreshInstallEvent) {
            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWelcomeDialog();
                }
            });
        } else if (observableEvent instanceof SoftwareUpgradedEvent) {
            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWhatsNewDialog();
                }
            });
        }
    }
}
