package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the transformation of change log in Sections to HTML
 * fragments.
 * 
 * @author matt
 *
 */
public final class TestChangeLogTransformer extends LoggingTestCase {
    private DefaultChangeLogTransformer changeLogTransformer;
    private File testLog;

    /**
     * @throws ParseException never
     * @throws IOException never
     * 
     */
    @Before
    public void getUsualPrerequisites() throws IOException, ParseException {
        testLog = new File("src/test/resources/uk/me/gumbley/minimiser/updatechecker/testchanges.txt");
        Assert.assertTrue(testLog.exists());
        changeLogTransformer = new DefaultChangeLogTransformer();
    }
    
    /**
     * Matches a start tag, some text, and an end tag.
     * @author matt
     *
     */
    public class TagMatcher {
        private final StringBuilder builder;
        private final String endingTag;
        /**
         * Construct a TagMatcher
         * @param startTag the start tag
         * @param endTag the end tag
         */
        public TagMatcher(final String startTag, final String endTag) {
            this.endingTag = endTag;
            builder = new StringBuilder();
            builder.append(startTag);
        }
        /**
         * Performs a JUnit assertion that some input text matches
         * the regex produced by this TagMatcher
         * @param input the input text
         */
        public final void assertMatch(final String input) {
            final String regex = getRegex();
            final Matcher matcher = Pattern.compile(regex).matcher(input);
            if (!matcher.matches()) {
                Assert.fail("Input\n" + input 
                    + "\ndoes not match regex\n"
                    + regex);
            }
        }
        /**
         * Terminate the regex with the end tag, and return it,
         * for use in matching.
         * @return the regex.
         */
        protected final String getRegex() {
            builder.append(endingTag);
            return builder.toString();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String toString() {
            return builder.toString();
        }
        
        /**
         * Insert another TagMatcher that parses an inner tag
         * @param tagMatcher an inner TagMatcher
         * @return this
         */
        public TagMatcher then(final TagMatcher tagMatcher) {
            builder.append(tagMatcher.getRegex());
            return this;
        }
        
        /**
         * @param str a string to append
         * @return this
         */
        public final TagMatcher append(final String str) {
            builder.append(str);
            return this;
        }
        
        /**
         * Surround a string with a start and end tag
         * @param tagString the string for the start and end tags
         * @param str the inner string
         * @return the surrounded string
         */
        public final TagMatcher surround(final String tagString, final String str) {
            builder.append("<");
            builder.append(tagString);
            builder.append(">");
            builder.append(str);
            builder.append("</");
            builder.append(tagString);
            builder.append(">");
            return this;
        }
    }
    
    /**
     * Matches lists
     * @author matt
     *
     */
    public class ListMatcher extends TagMatcher {
        /**
         * 
         */
        public ListMatcher() {
            super("<ul>", "</ul>");
        }
        /**
         * @param text the text surrounded by the li tag
         * @return this
         */
        public ListMatcher element(final String text) {
            surround("li", text);
            return this;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public ListMatcher then(final TagMatcher inner) {
            super.then(inner);
            return this;
        }
    }
    /**
     * Matches HTML enclosures
     * @author matt
     *
     */
    public class HTMLMatcher extends TagMatcher {
        /**
         * 
         */
        public HTMLMatcher() {
            super("<html><body>", "</body></html>");
        }
        /**
         * Match bold text
         * @param str the text
         * @return this
         */
        public HTMLMatcher bold(final String str) {
            surround("b", str);
            return this;
        }
        /**
         * Match emphasised text
         * @param str the text
         * @return this
         */
        public HTMLMatcher emph(final String str) {
            surround("em", str);
            return this;
        }
        /**
         * Match anything
         * @return this
         */
        public HTMLMatcher anything() {
            append(".*?");
            return this;
        }
        /**
         * Match a line break
         * @return this
         */
        public HTMLMatcher linebreak() {
            append("<br>");
            return this;
        }
        /**
         * Match a paragraph break
         * @return this
         */
        public HTMLMatcher parabreak() {
            append("<p>");
            return this;
        }
        /**
         * Match some text
         * @param str text
         * @return this
         */
        public HTMLMatcher text(final String str) {
            append(str);
            return this;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public HTMLMatcher then(final TagMatcher inner) {
            super.then(inner);
            return this;
        }
    }
    
    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void getNoSections() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v78"),
            new ComparableVersion("v90"), testLog);
        new HTMLMatcher().assertMatch(subsection);
        
    }
    
    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void getOneSectionWithComplexList() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v0.9.7"),
            new ComparableVersion("v0.9.7"), testLog);
        final ListMatcher list = new ListMatcher().
            element("now handles left handed flange benders that "
                + "are so long that they stretch onto a separate "
                + "line but are part of the same bullet and so "
                + "don't get line breaks interspersed.").
            element("and bullet points");
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v0.9.7")
            .anything()
            .emph("colon separating witty title not out of order")
            .linebreak()
            .then(list);
            
        matcher.assertMatch(subsection);
    }
    
    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void getOneSectionWithSimpleList() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v0.9.8"),
            new ComparableVersion("v0.9.8"), testLog);
        final ListMatcher list = new ListMatcher().
            element("with a").
            element("simple list");
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v0.9.8")
            .anything()
            .emph("another witty title")
            .linebreak()
            .text("Fixed some bugs")
            .linebreak()
            .then(list)
            .text("and some text");
            
        matcher.assertMatch(subsection);
    }

    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void getOneSectionWithSimpleListOfOne() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v0.9"),
            new ComparableVersion("v0.9"), testLog);
        final ListMatcher list = new ListMatcher().
            element("a list of one is valid");
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v0.9")
            .linebreak()
            .text("thought I'd release again")
            .linebreak()
            .then(list);
            
        matcher.assertMatch(subsection);
    }

   /**
    * @throws IOException never
    * @throws ParseException never
    */
   @Test
   public void getOneSectionWithOnlySimpleListOfOne() throws IOException, ParseException {
       final String subsection = changeLogTransformer.readFileSubsection(
           new ComparableVersion("v0.7"),
           new ComparableVersion("v0.7"), testLog);
       final ListMatcher list = new ListMatcher().
           element("singleton list");
       final HTMLMatcher matcher = new HTMLMatcher()
           .bold("v0.7")
           .anything()
           .emph("more bug fixes")
           .linebreak()
           .then(list);
           
       matcher.assertMatch(subsection);
   }
    
    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void getTwoSections() throws IOException, ParseException {
        final String subsections = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v0.5-alpha"),
            new ComparableVersion("v0.5"), testLog);
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v0.5")
            .anything()
            .text("1/01/1970")
            .anything()
            .emph("dawn of a new epoch")
            .linebreak()
            .parabreak()
            .bold("v0.5-alpha")
            .anything()
            .text("25/12/2009")
            .anything()
            .emph("separators are removed")
            .linebreak();
            
        matcher.assertMatch(subsections);
    }
    
    /**
     * @throws IOException never
     * @throws ParseException never
     */
    @Test
    public void versionIsBold() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v0.9"),
            new ComparableVersion("v0.9"), testLog);
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v0.9")
            .linebreak()
            .text("thought I'd release again")
            .anything();
            
        matcher.assertMatch(subsection);
    }
    
    /**
     * @throws IOException on error
     * @throws ParseException on error
     */
    @Test
    public void titleIsFollowedByLineBreak() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v1.0.0"),
            new ComparableVersion("v1.0.0"), testLog);
        final HTMLMatcher matcher = new HTMLMatcher()
            .bold("v1.0.0")
            .text(" - ")
            .emph("witty title goes here")
            .linebreak()
            .anything();
            
        matcher.assertMatch(subsection);
    }
}
