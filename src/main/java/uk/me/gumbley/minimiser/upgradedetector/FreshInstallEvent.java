package uk.me.gumbley.minimiser.upgradedetector;

/**
 * An Upgrade Event that denotes a fresh installation of the software
 * @author matt
 *
 */
public final class FreshInstallEvent extends UpgradeEvent {

    /**
     * Construct a fresh install event
     * @param runningVer the running software version
     */
    public FreshInstallEvent(final String runningVer) {
        super(runningVer);
    }
}
