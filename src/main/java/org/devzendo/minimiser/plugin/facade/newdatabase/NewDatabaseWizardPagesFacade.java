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

package org.devzendo.minimiser.plugin.facade.newdatabase;

import java.util.List;

import org.netbeans.spi.wizard.WizardPage;

/**
 * Facade provided to allow the File|New wizard to be populated
 * with plugin-supplied pages.
 * 
 * @author matt
 *
 */
public interface NewDatabaseWizardPagesFacade {
    /**
     * Obtain any plugin-specific wizard pages that are shown at
     * the end of the framework-supplied pages (database location,
     * encryption details).
     * 
     * @return a list of wizard pages, can be empty or null
     */
    List<WizardPage> getWizardPages();
}
