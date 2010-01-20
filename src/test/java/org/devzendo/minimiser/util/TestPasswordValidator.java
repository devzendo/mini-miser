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
