package uk.me.gumbley.minimiser.upgradedetector;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.dialog.welcome.WelcomeDialogHelper;
import uk.me.gumbley.minimiser.startupqueue.StartupQueue;

/**
 * Shows the Welcome Dialog upon detection of a fresh installation.
 * Shows the What's New In This Release Dialog upon detection of an upgrade
 * installation.
 * Is otherwise silent.
 * Shows the dialog via the startup queue, so it'll happen at the end of
 * startup. Otherwise, the dialog creates a jarring clash with any open
 * encrypted databases, which prompt for their password.
 * 
 * @author matt
 *
 */
public final class WelcomeMessageDisplayingListener implements Observer<UpgradeEvent> {

    private final StartupQueue startupQueue;

    /**
     * Construct the listener.
     * @param sq the Startup Queue
     */
    public WelcomeMessageDisplayingListener(final StartupQueue sq) {
        this.startupQueue = sq;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (observableEvent instanceof FreshInstallEvent) {
            placeSwingRunnableOnStartupQueue(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWelcomeDialog();
                }
            });
        } else if (observableEvent instanceof SoftwareUpgradedEvent) {
            placeSwingRunnableOnStartupQueue(new Runnable() {
                public void run() {
                    WelcomeDialogHelper.showWhatsNewDialog();
                }
            });
        }
    }
    
    private void placeSwingRunnableOnStartupQueue(final Runnable showDialog) {
        startupQueue.addRunnable(new Runnable() {
            public void run() {
                GUIUtils.invokeLaterOnEventThread(showDialog);
            }
        });
    }
}
