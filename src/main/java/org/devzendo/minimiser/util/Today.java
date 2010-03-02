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

package org.devzendo.minimiser.util;

/**
 * A Today gives today's date, in UK DD/MM/YYYY form, suitable for text
 * comparison against a stored date to see if they are the same. This is not
 * intended to be user-displayable, hence no attempt at making it Localisable.
 * @author matt
 *
 */
public interface Today {
    
    /**
     * @return the UK format date strin for today
     */
    String getUKDateString();
}