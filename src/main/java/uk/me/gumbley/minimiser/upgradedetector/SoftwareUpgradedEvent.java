package uk.me.gumbley.minimiser.upgradedetector;

/**
 * An Upgrade Event that denotes an upgrade installation of the software
 * @author matt
 *
 */
public final class SoftwareUpgradedEvent extends UpgradeEvent {

    private final String previousVersion;

    /**
     * Construct a software upgraded event
     * @param runningVer the running software version
     * @param previousVer the previous version of the software
     */
    public SoftwareUpgradedEvent(final String runningVer, final String previousVer) {
        super(runningVer);
        this.previousVersion = previousVer;
    }

    /**
     * Obtain the version of the software that was running previously
     * @return the previous version
     */
    public String getPreviousVersion() {
        return previousVersion;
    }
}
