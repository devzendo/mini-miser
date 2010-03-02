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

import org.devzendo.commoncode.string.StringUtils;

/**
 * A simple password validator utility class.
 * 
 * @author matt
 *
 */
public final class PasswordValidator {
    /**
     * 
     */
    public static final int MAX_PASSWORD_LENGTH = 32; // TODO I don't know what the maximum password length in h2 is
    /**
     * 
     */
    public static final int MIN_PASSWORD_LENGTH = 8; // TODO I don't know what the maximum password length in h2 is
    /**
     * 
     */
    public static final int MIN_LETTERS = 6;
    /**
     * 
     */
    public static final int MIN_DIGITS = 2;

    private PasswordValidator() {
        // nop
    }
    
    /**
     * Validate the password
     * @param pwd the chars of the password
     * @return a criticism, or null if OK.
     */
    public static String criticisePassword(final char[] pwd) {
        if (pwd == null || pwd.length == 0) {
            return "You must enter a password";
        }
        if (pwd.length > PasswordValidator.MAX_PASSWORD_LENGTH) {
            return "Your password is too long";
        }
        if (pwd.length < PasswordValidator.MIN_PASSWORD_LENGTH) {
            return "Your password must be at least "
                + PasswordValidator.MIN_PASSWORD_LENGTH + " "
                + StringUtils.pluralise("character", PasswordValidator.MIN_PASSWORD_LENGTH)
                + " long, with at least " + PasswordValidator.MIN_DIGITS
                + " " + StringUtils.pluralise("digit", PasswordValidator.MIN_DIGITS);
        }
        return analysePassword(pwd);
    }

    private static String analysePassword(final char[] pwd) {
        int digits = 0;
        int letters = 0;
        int upper = 0;
        int lower = 0;
        for (int i = 0; i < pwd.length; i++) {
            if (Character.isDigit(pwd[i])) {
                digits++;
            } else {
                if (Character.isLetter(pwd[i])) {
                    letters++;
                    if (Character.isUpperCase(pwd[i])) {
                        upper++;
                    }
                    if (Character.isLowerCase(pwd[i])) {
                        lower++;
                    }
                }
            }
        }
        if (letters < PasswordValidator.MIN_LETTERS) {
            return "Your password must contain at least "
                + PasswordValidator.MIN_LETTERS + " "
                + StringUtils.pluralise("letter", PasswordValidator.MIN_LETTERS);
        }
        if (digits < PasswordValidator.MIN_DIGITS) {
            return "Your password must contain at least "
            + PasswordValidator.MIN_DIGITS + " "
            + StringUtils.pluralise("digit", PasswordValidator.MIN_DIGITS);
        }
        if (lower == 0 || upper == 0) {
            return "Your password must contain both upper and lower case letters";
        }
        return null;
    }
}
