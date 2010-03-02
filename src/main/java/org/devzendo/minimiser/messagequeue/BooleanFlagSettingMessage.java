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

package org.devzendo.minimiser.messagequeue;

import org.devzendo.minimiser.prefs.BooleanFlag;



/**
 * A message allowing the setting of boolean flags in Prefs, given a BooleanFlag
 * identifier. 
 * 
 * @author matt
 *
 */
public final class BooleanFlagSettingMessage extends AbstractMessage {
    private boolean flagValue = false;
    private BooleanFlag booleanFlagName;
    private final String choiceExplanationText;

    /**
     * @param subject the message subject
     * @param content the message content
     * @param flagName the boolean flag to set/clear
     * @param choiceExplanation the text that's displayed in the message's
     * checkbox by the renderer
     */
    public BooleanFlagSettingMessage(final String subject, final Object content,
            final BooleanFlag flagName, final String choiceExplanation) {
        super(subject, content);
        this.booleanFlagName = flagName;
        this.choiceExplanationText = choiceExplanation;
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     * @param flagName the boolean flag to set/clear
     * @param choiceExplanation the text that's displayed in the message's
     * checkbox by the renderer
     */
    public BooleanFlagSettingMessage(final String subject, final Object content,
            final Importance importance, final BooleanFlag flagName, final String choiceExplanation) {
        super(subject, content, importance);
        this.booleanFlagName = flagName;
        this.choiceExplanationText = choiceExplanation;
    }

    /**
     * Store the user's choce for the boolean flag as uncommitted.  
     * @param value true or false
     */
    public void setBooleanFlagValue(final boolean value) {
        this.flagValue = value;
    }

    /**
     * Obtain the user's choice for the boolean flag
     * @return true or false
     */
    public boolean isBooleanFlagSet() {
        return flagValue;
    }

    /**
     * Obtain the BooleanFlag for this message
     * @return the BooleanFlag that will be set by the removal of this message
     */
    public BooleanFlag getBooleanFlagName() {
        return booleanFlagName;
    }

    /**
     * Get the choice explanation text
     * @return the text that's displayed in the message's
     * checkbox by the renderer
     */
    public String getChoiceExplanationText() {
        return choiceExplanationText;
    }
}
