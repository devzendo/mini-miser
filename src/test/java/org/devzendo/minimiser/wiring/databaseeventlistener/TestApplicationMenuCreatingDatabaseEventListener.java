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

package org.devzendo.minimiser.wiring.databaseeventlistener;

import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.junit.Assert;
import org.junit.Test;



/**
 * An Empty ApplicationMenu is populated in the DatabaseDescriptor on
 * database open notification by the OpenDatabaseList.
 *
 * @author matt
 *
 */
public final class TestApplicationMenuCreatingDatabaseEventListener {
    private static final String DATABASE = "db";

    /**
     *
     */
    @Test
    public void emptyApplicationMenuIsAddedToDatabaseDescriptorOnDatabaseOpen() {
        final OpenDatabaseList openDatabaseList = new OpenDatabaseList();

        final ApplicationMenuCreatingDatabaseEventListener adapter = new ApplicationMenuCreatingDatabaseEventListener();
        openDatabaseList.addDatabaseEventObserver(adapter);

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        Assert.assertNull(databaseDescriptor.getApplicationMenu());
        openDatabaseList.addOpenedDatabase(databaseDescriptor);

        Assert.assertNotNull(databaseDescriptor.getApplicationMenu());
    }
}
