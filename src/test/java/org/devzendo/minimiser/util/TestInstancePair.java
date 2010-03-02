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

import org.devzendo.minimiser.util.InstanceClasses.A;
import org.devzendo.minimiser.util.InstanceClasses.BaseInterface;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the InstancePair.
 * 
 * @author matt
 *
 */
public final class TestInstancePair {
    private InstancePair<BaseInterface> mInstancePair;

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void classMustBeSupplied() {
        mInstancePair = new InstancePair<BaseInterface>(null, new A("foo"));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void instanceMustBeSupplied() {
        mInstancePair = new InstancePair<BaseInterface>(A.class, null);
    }
    
    /**
     * 
     */
    @Test
    public void canRetrieveCorrectly() {
        final A a = new A("foo");
        mInstancePair = new InstancePair<BaseInterface>(A.class, a);
        Assert.assertEquals(A.class, mInstancePair.getClassOfInstance());
        Assert.assertSame(a, mInstancePair.getInstance());
    }
}
