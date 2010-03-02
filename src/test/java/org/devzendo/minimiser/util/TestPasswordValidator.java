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

import org.apache.log4j.Logger;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the PasswordValidator
 * @author matt
 *
 */
public final class TestPasswordValidator extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestPasswordValidator.class);
    
    /**
     * 
     */
    @Test
    public void testMinimumsAddUp() {
        Assert.assertTrue(PasswordValidator.MIN_DIGITS + PasswordValidator.MIN_LETTERS <= PasswordValidator.MIN_PASSWORD_LENGTH);
    }

    /**
     * 
     */
    @Test
    public void testNullPassword() {
        Assert.assertEquals("You must enter a password", PasswordValidator.criticisePassword(null));
        Assert.assertEquals("You must enter a password", PasswordValidator.criticisePassword(new char[0]));
    }
    
    /**
     * 
     */
    @Test
    public void testImmenseLength() {
        Assert.assertEquals("Your password is too long", xlat("Actually, size DOES matter. Sometimes, these things can be just TOO LONG."));
    }
    
    /**
     * 
     */
    @Test
    public void testTiny() {
        Assert.assertTrue(xlat("boo").startsWith("Your password must be at least"));
    }
    
    /**
     * 
     */
    @Test
    public void testNotEnoughLetters() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PasswordValidator.MIN_LETTERS - 1; i++) {
            sb.append('A');
        }
        for (int i = 0; i < PasswordValidator.MIN_DIGITS; i++) {
            sb.append('1');
        }
        sb.append("-------");
        final String xlat = xlat(sb.toString());
        Assert.assertTrue(xlat.startsWith("Your password must contain at least " + PasswordValidator.MIN_LETTERS + " letter"));
    }

    /**
     * 
     */
    @Test
    public void testNotEnoughNumbers() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PasswordValidator.MIN_LETTERS; i++) {
            sb.append('A');
        }
        for (int i = 0; i < PasswordValidator.MIN_DIGITS - 1; i++) {
            sb.append('1');
        }
        sb.append("-------");
        final String xlat = xlat(sb.toString());
        Assert.assertTrue(xlat.startsWith("Your password must contain at least " + PasswordValidator.MIN_DIGITS + " digit"));
    }

    /**
     * 
     */
    @Test
    public void testNotMixedCase() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PasswordValidator.MIN_LETTERS; i++) {
            sb.append('A');
        }
        for (int i = 0; i < PasswordValidator.MIN_DIGITS; i++) {
            sb.append('1');
        }
        sb.append("-------");
        final String xlat = xlat(sb.toString());
        Assert.assertTrue(xlat.equals("Your password must contain both upper and lower case letters"));
    }
    
    /**
     * 
     */
    @Test
    public void testValidity() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PasswordValidator.MIN_LETTERS - 1; i++) {
            sb.append('A');
        }
        sb.append("a");
        for (int i = 0; i < PasswordValidator.MIN_DIGITS; i++) {
            sb.append('1');
        }
        final String xlat = xlat(sb.toString());
        LOGGER.info("testValidity: " + xlat);
        Assert.assertNull(xlat);
    }
    private String xlat(final String p) {
        return PasswordValidator.criticisePassword(p.toCharArray());
    }
}
