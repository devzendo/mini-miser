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

package org.devzendo.minimiser.version;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ComparableVersion allows equality and comparison between software version
 * numbers of the form x.y.z with an optional -classifier. They can optionally
 * start with a v. They can have an arbitrary number of digits, so 1 is as
 * valid as 1.2.33.4.555.666.
 * 
 * @author matt
 *
 */
public final class ComparableVersion implements Comparable<ComparableVersion> {
    /**
     * A regex that matches a version number
     */
    public static final String VERSION_REGEX = "[vV]?(\\d+(?:\\.\\d+)*)(-?)(\\S+)?";
    private static final String ANCHORED_VERSION_REGEX = "^" + VERSION_REGEX + "$";
    private final String version;
    private final String versionNumbers;
    private final String classifier;
    private final List<Integer> versionNumberList;

    /**
     * Construct a ComparableVersion from a version string
     * @param v the version string
     */
    public ComparableVersion(final String v) {
        version = getSimplyValidatedTrimmedVersion(v);
        final Matcher versionMatcher = Pattern.compile(ANCHORED_VERSION_REGEX).matcher(version);
        if (!versionMatcher.matches()) {
            throw new IllegalArgumentException("Version '" + version + "' is not a valid version");
        }
        versionNumbers = versionMatcher.group(1);
        final String hyphen = versionMatcher.group(2) == null ? "" : versionMatcher.group(2);
        classifier = versionMatcher.group(3) == null ? "" : versionMatcher.group(3);
        if (classifier.indexOf(".") != -1) {
            // the classifier regex should be non-whitespace but not dots
            throw new IllegalArgumentException("Version '" + version + "' does not have a valid classifier '" + classifier + "'");
        }
        if (hyphen.length() != 0 && classifier.length() == 0) {
            throw new IllegalArgumentException("Version '" + version + "' does not have a classifier following a hyphen");
        }
        final String[] vNumbers = versionNumbers.split("\\.");
        versionNumberList = new ArrayList<Integer>(vNumbers.length);
        for (String vNumber : vNumbers) {
            versionNumberList.add(Integer.parseInt(vNumber));
        }
    }

    private String getSimplyValidatedTrimmedVersion(final String v) {
        if (v == null) {
            throw new IllegalArgumentException("Null version not allowed");
        }
        final String trimmed = v.trim();
        if (trimmed.length() == 0) {
            throw new IllegalArgumentException("Empty version not allowed");
        }

        return trimmed;
    }

    /**
     * Obtain the version number. For input "  v1.0.0-SNAPSHOT ", return
     * "1.0.0-SNAPSHOT"
     * 
     * @return the full, trimmed version number
     */
    public String getVersion() {
        return version;
    }

    /**
     * Obtain any classifier. For input " v1.0.0-SNAPSHOT ", return "SNAPSHOT".
     * @return the classifier, may be an empty string
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * Obtain the version numbers. For input " v2.6.2-BETA", return "2.6.2"
     * @return the version number.
     */
    public String getVersionNumberString() {
        return versionNumbers;
    }
    
    /**
     * Obtain the individual ordered integers that make up the version number.
     * For input "v1.2.3", return 1, 2 and 3. 
     * @return the numbers
     */
    public List<Integer> getVersionNumberIntegers() {
        return new ArrayList<Integer>(versionNumberList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
        result = prime * result + ((versionNumberList == null) ? 0 : versionNumberList.hashCode());
        return result;
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
        return compareTo((ComparableVersion) obj) == 0;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final ComparableVersion obj) {
        final int elementSignum = compareElementForElement(obj);
        if (elementSignum != 0) {
            return elementSignum;
        }
        // version numbers are identical, but shorter numbers of elements mean
        // earlier releases (e.g. 1.0 is earlier than 1.0.1)
        // TODO: I don't think these size comparisons work - compareElementForElement
        // will have catered for this by right-zero-padding?
        if (this.versionNumberList.size() < obj.versionNumberList.size()) {
            return -1;
        }
        if (this.versionNumberList.size() > obj.versionNumberList.size()) {
            return 1;
        }
        // version numbers are identical. classifiers always indicate earlier
        // releases than non-classifiers
        if (this.classifier.length() > 0 && obj.classifier.length() == 0) {
            return -1;
        }
        if (this.classifier.length() == 0 && obj.classifier.length() > 0) {
            return 1;
        }
        // they have equal version numbers and both have a classifier.
        // I could go into snapshot < alpha < beta < rc and all that guff...? 
        return 0;
    }

    /**
     * compare version numbers by padding each out to same width, filling
     * short ones with zeros, then comparing element-for-element.
     * @param obj another one of these
     * @return a signum value
     */
    private int compareElementForElement(final ComparableVersion obj) {
        final int maxVersionElements = Math.max(this.versionNumberList.size(), obj.versionNumberList.size());
        for (int i = 0; i < maxVersionElements; i++) {
            final Integer thisVersionElement = i < this.versionNumberList.size() ?
                                            this.versionNumberList.get(i) :
                                            new Integer(0);
            final Integer thatVersionElement = i < obj.versionNumberList.size() ?
                                            obj.versionNumberList.get(i) :
                                            new Integer(0);
            final int elementSignum = thisVersionElement.compareTo(thatVersionElement);
            if (elementSignum != 0) {
                return elementSignum;
            }
        }
        return 0;
    }

    /**
     * Is the version within the range indicated, inclusively?
     * @param version the version to test for
     * @param leftBound the left hand side of the range
     * @param rightBound the right hand side of the range
     * @return true if leftBound <= version <= rightBound
     */
    public static boolean inRange(final ComparableVersion version, final ComparableVersion leftBound, final ComparableVersion rightBound) {
        final int fromSignum = version.compareTo(leftBound);
        final int toSignum = version.compareTo(rightBound);
        if ((fromSignum == 0 || fromSignum == 1)
                &&
            (toSignum == 0 || toSignum == -1)) {
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (classifier.length() == 0) {
            return "v" + versionNumbers;
        }
        return "v" + versionNumbers + "-" + classifier;
    }
}
