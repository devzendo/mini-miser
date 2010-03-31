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

/**
 * A TabParameter is set in a TabIdentifier, and when this tabIdentifier's Tab is loaded
 * by the TabFactory, the TabParameter is made available in a BeanFactory so that it
 * can be passed in to the Tab constructor.
 *
 * TabParameter is only a marker interface to provide some type safety, rather than just
 * using Object for tab parameters.
 *
 * The TapParameter forms part of the equality test for TabIdentifiers, so you must
 * implement equals and hashCode.
 *
 * @author matt
 *
 */
public interface TabParameter {
}
