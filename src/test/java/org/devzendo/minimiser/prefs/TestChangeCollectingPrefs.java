/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.prefs;

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);
        ccp.commit();
        
        EasyMock.verify(mockPrefs);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void tabHidingWithoutPriorReadThrows() {
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        mockPrefs.setTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        mockPrefs.setTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        mockPrefs.clearTabHidden(EasyMock.eq("SQL"));
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.FALSE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.expect(mockPrefs.isTabHidden("SQL")).andReturn(Boolean.TRUE);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);

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
        final MiniMiserPrefs mockPrefs = EasyMock.createStrictMock(MiniMiserPrefs.class);
        EasyMock.replay(mockPrefs);
        
        final ChangeCollectingMiniMiserPrefs ccp = new ChangeCollectingMiniMiserPrefs(mockPrefs);
        ccp.clearLastActiveFile(); // for example
    }
}
