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

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the escaping mechanism used in the RecentFilesList.
 * 
 * @author matt
 *
 */
public final class TestDbPairEncapsulator {
    /**
     * 
     */
    @Test
    public void testEscapeWhenNoQuotes() {
        Assert.assertEquals("\"one\",\"path\"", DatabasePairEncapsulator.escape("one", "path"));
    }
    
    /**
     * 
     */
    @Test
    public void testUnescapeWhenNoQuotes() {
        final DatabasePair unescaped = DatabasePairEncapsulator.unescape("\"one\",\"two\"");
        Assert.assertEquals("one", unescaped.getName());
        Assert.assertEquals("two", unescaped.getPath());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnescapeFailure() {
        DatabasePairEncapsulator.unescape("\"f\\\"oo\",\"/tmp/\\\"quote"); // no end quote
    }
    
    /**
     * 
     */
    @Test
    public void testRoundTripWithQuotes() {
        final StringBuilder name = new StringBuilder();
        name.append('f');
        name.append('"');
        name.append("oo");
        final StringBuilder path = new StringBuilder();
        path.append("/tmp/");
        path.append('"');
        path.append("quote");
        final String escaped = DatabasePairEncapsulator.escape(name.toString(), path.toString());
        Assert.assertEquals("\"f\\\"oo\",\"/tmp/\\\"quote\"", escaped);

        final DatabasePair unescaped = DatabasePairEncapsulator.unescape(escaped);
        Assert.assertEquals(name.toString(), unescaped.getName());
        Assert.assertEquals(path.toString(), unescaped.getPath());
    }
}
