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
package org.devzendo.minimiser.gui.tab;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A TabParameter used for testst that contains an Integer.
 *
 * @author matt
 *
 */
public final class IntegerTabParameter implements TabParameter {
    private final Integer mValue;

    /**
     * @param value the value of the TabParameter
     */
    public IntegerTabParameter(final int value) {
        mValue = new Integer(value);
    }

    /**
     * @return the value
     */
    public Integer getValue() {
        return mValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final IntegerTabParameter castObj = (IntegerTabParameter) obj;
        return new EqualsBuilder()
            .append(this.mValue, castObj.mValue)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // pick 2 hard-coded, odd, >0 ints as args
        return new HashCodeBuilder(1, 31)
            .append(mValue)
            .toHashCode();
    }
}