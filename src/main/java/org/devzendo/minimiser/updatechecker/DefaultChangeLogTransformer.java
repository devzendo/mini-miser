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

package org.devzendo.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.updatechecker.ChangeLogSectionParser.Section;
import org.devzendo.minimiser.version.ComparableVersion;


/**
 * Transforms change log details into HTML for display.
 * 
 * @author matt
 *
 */
public final class DefaultChangeLogTransformer implements ChangeLogTransformer {
    private static final String SEPARATOR = " - ";

    private static final Logger LOGGER = Logger.getLogger(DefaultChangeLogTransformer.class);
    
    private final Matcher bulletMatcher = Pattern.compile("^\\s*\\*\\s*(.*)$").matcher("");
    private final Matcher bulletItemContinuationMatcher = Pattern.compile("^\\s+([^\\*].*)$").matcher("");
    

    /**
     * {@inheritDoc}
     * @throws ParseException 
     */
    public String readFileSubsection(final ComparableVersion currentVersion, 
            final ComparableVersion latestVersion, final File changeLogFile) throws IOException, ParseException {
        final ChangeLogSectionParser parser = new ChangeLogSectionParser(changeLogFile);
        final List<Section> versionSections = parser.getVersionSections(currentVersion, latestVersion);
        LOGGER.debug("Transforming " + versionSections.size() + " sections ["
            + currentVersion.toString() + ", " + latestVersion.toString() + "] to HTML");
        return transformSectionsToHTML(versionSections);
    }


    private String transformSectionsToHTML(final List<Section> versionSections) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        for (int i = 0; i < versionSections.size(); i++) {
            final Section section  = versionSections.get(i);
            sb.append(addSection(section));
            if (i != versionSections.size() - 1) {
                sb.append("<p>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String readAllStream(final InputStream changeLogFile) throws IOException,
            ParseException {
        final ChangeLogSectionParser parser = new ChangeLogSectionParser(changeLogFile);
        final List<Section> versionSections = parser.getAllVersionSections();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Transforming all " + versionSections.size() + " sections to HTML");
            for (Section section : versionSections) {
                LOGGER.debug("Section: " + section);
            }
        }
        return transformSectionsToHTML(versionSections);
    }
    
    private StringBuilder addSection(final Section section) {
        final StringBuilder sb = new StringBuilder();
        
        // version text
        sb.append(surround("b", section.getVersion().toString()));
        
        // date (optional)
        if (section.getDateText().length() != 0) {
            sb.append(SEPARATOR);
            sb.append(section.getDateText());
        }
        
        // title (optional)
        if (section.getTitleText().length() != 0) {
            sb.append(SEPARATOR);
            sb.append(surround("em", section.getTitleText()));
        }
        
        sb.append("<br>");
        
        sb.append(transformedInformationText(section.getInformationText()));
        
        return sb;
    }

    /**
     * Split the information text up into lines followed by line
     * breaks, transforming bullet points and their continuations
     * into unnumbered lists and their items.
     * <p>
     * Yes, this routine is too complex.
     * 
     * @param informationText the info text, interspersed with
     * carriage returns/line feeds
     * @return a HTML representation of it.
     */
    private String transformedInformationText(final String informationText) {
        return transformInformationTextLines(informationText.split("[\\r\\n]+"));
    }

    private String transformInformationTextLines(final String[] informationLines) {
        final StringBuilder sb = new StringBuilder();
        boolean inList = false;
        for (int i = 0; i < informationLines.length; i++) {
            String line = informationLines[i];
            LOGGER.debug("Input line '" + line + "'");
            
            boolean listContinuation = false;
            bulletMatcher.reset(line);
            if (bulletMatcher.lookingAt()) {
                line = bulletMatcher.group(1);
                if (inList) {
                    // terminate previous li
                    LOGGER.debug("in list - bullet - adding /li to terminate previous item");
                    sb.append("</li>");
                    // note check when adding <br> - no line break necessary
                } else {
                    inList = true;
                    sb.append("<ul>");
                }
            } else {
                if (inList) {
                    // handle bullet item continuations -
                    // these start with wsp
                    bulletItemContinuationMatcher.reset(line);
                    if (bulletItemContinuationMatcher.lookingAt()) {
                        LOGGER.debug("bullet item continuation");
                        listContinuation = true;
                        sb.append(" ");
                        sb.append(bulletItemContinuationMatcher.group(1));
                    } else {
                        // terminate previous li
                        LOGGER.debug("in list - nonbullet - adding /li to terminate previous item");
                        sb.append("</li>");
                        // note check when adding <br> - no line break necessary
                        inList = false;
                        LOGGER.debug("no bullet match - end of ul");
                        sb.append("</ul>");
                    }
                }
            }
            if (!listContinuation) {
                if (inList) {
                    LOGGER.debug("in list - adding li");
    
                    sb.append("<li>");
                }
                LOGGER.debug("adding '" + line + "'");
                sb.append(line);
                // the /li is added in two places above and one
                // below to handle
                // the case where we have processed a bullet, and
                // it's followed by a list continuation
            }
            if (i == informationLines.length - 1) {
                LOGGER.debug("last line");
                // was last line the last line of a bullet?
                if (inList) {
                    // terminate previous li
                    LOGGER.debug("in list - last line - adding /li to terminate previous item");
                    sb.append("</li>");
                    LOGGER.debug("end of ul");
                    sb.append("</ul>");
                }
            } else {
                if (!inList) {
                    LOGGER.debug("adding line break");
                    sb.append("<br>");
                }
            }
        }
        final String string = sb.toString();
        LOGGER.debug("Returned string is [" + string + "]");
        return string;
    }

    private String surround(final String tag, final String text) {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }
}
