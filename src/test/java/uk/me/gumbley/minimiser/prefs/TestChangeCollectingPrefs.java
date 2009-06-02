package uk.me.gumbley.minimiser.prefs;

import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests the ChangeCollectingPrefs.
 * 
 * Each area that the ChangeCollectingPrefs collects changes for is tested
 * separately here; there are no tests for combinations (yet)
 * 
 * @author matt
 *
 */
public final class TestChangeCollectingPrefs {
    
    /**
     * 
     */
    @Test
    public void noTabHidingCausesNoChanges() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);
        ccp.commit();
        
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void tabHidingWithoutPriorReadThrows() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        mockPrefs.setTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial
        // setting - but don't do that here...
        // don't do ccp.isTabHidden("SQL");
        
        // Now change it
        ccp.setTabHidden("SQL");
        // b o o m
    }

    /**
     * 
     */
    @Test
    public void tabHidingAfterPriorReadCausesChanges() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        mockPrefs.setTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it
        ccp.setTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void tabRepeatedTogglingFromClearedAfterPriorReadCausesNoChanges() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it twice - no change should be made to the underlying prefs
        ccp.setTabHidden("SQL");
        ccp.clearTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void tabRepeatedTogglingFromHiddenAfterPriorReadCausesNoChanges() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it twice - no change should be made to the underlying prefs
        ccp.clearTabHidden("SQL");
        ccp.setTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void clearingHiddenTabAfterPriorReadCausesChanges() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        mockPrefs.clearTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it
        ccp.clearTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void clearingAlreadyClearedTabAfterPriorReadCausesNoChange() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it
        ccp.clearTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void hidingAlreadyHiddenTabAfterPriorReadCausesNoChange() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now change it
        ccp.setTabHidden("SQL");
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test
    public void makingNoChangeToAlreadyHiddenTabAfterPriorReadCausesNoChange() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);

        // The data has to be read through ccp first to create its initial setting
        ccp.isTabHidden("SQL");
        
        // Now don't make any changes
        ccp.commit();
        EasyMock.verify(mockPrefs);
    }
    /**
     * 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void changingPrefsOptionThatsNotUsedByToolsOptionsThrows() {
        final Prefs mockPrefs = EasyMock.createStrictMock(Prefs.class);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingPrefs ccp = new ChangeCollectingPrefs(mockPrefs);
        ccp.clearLastActiveFile(); // for example
    }
}
