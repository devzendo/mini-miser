package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.minimiser.gui.wizard.MiniMiserWizardPage;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.Prefs;


/**
 * Initialises the MiniMiserWizardPane toolkit. This needs to have
 * the left-hand graphic set, and the size used for the main
 * panel cached, as it's jarring to create the panel with a
 * filechooser every time, and have it resize to contain it.
 * 
 * @author matt
 *
 */
public final class WizardPageInitialisingLifecycle implements Lifecycle {

    private final Prefs mPrefs;

    /**
     * @param prefs the preferences
     */
    public WizardPageInitialisingLifecycle(final Prefs prefs) {
        mPrefs = prefs;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                MiniMiserWizardPage.setLHGraphic();
                MiniMiserWizardPage.getPanelDimension(mPrefs);
            }
        });
    }
}
