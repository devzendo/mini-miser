package uk.me.gumbley.minimiser.pluginmanager;

/**
 * An ApplicationPlugin extends the basic plugin to
 * provide details of the main application. There must be only
 * one ApplicationPlugin loaded by the PluginManager for a
 * given application. The framework will report a fatal error
 * if one is not present.
 * <p/>
 * Most calls here are made from the About or Problem Reporter
 * dialogs.
 * <ul>
 * <li> getDevelopersContactDetails() is called whenever this info
 *      is displayed to the user, e.g. in the About or Problem
 *      Reporter dialogs.
 * <li> getUpdateSiteURL() is called whenever update availability
 *      checks are being performed, if this facility has been
 *      enabled
 * <li> getShortLicenseDetails(),
 *      getFullLicenseDetailsResourcePath(),
 *      getAboutDetailsResourcePath() and
 *      getChangeLogResourcePath()
 *      are called by the About dialog.
 * </ul>
 * 
 * @author matt
 *
 */
public interface ApplicationPlugin extends Plugin {
    /**
     * How can users of this plugin contact the developers?
     * 
     * This may be a personal address, or mailing list. It is
     * displayed in the About dialog and in the Problem Reporter
     * dialog.
     * 
     * @return an email address, or could be a URL.
     */
    String getDevelopersContactDetails();
    
    /**
     * Where is the update site for this plugin?
     * 
     * The files 'version.txt' and 'changelog.txt' should be
     * available at this URL.
     * 
     * @return the Base URL of the update site for this plugin 
     */
    String getUpdateSiteBaseURL();
 
    /**
     * Obtain a path to a resource file that contains the text
     * shown in the About dialog.
     * 
     * This resource can be a .txt or .html/.htm file; it will be
     * shown in a text or HTML rendered component accordingly.
     * 
     * If left null or blank, an empty tab will be shown in the
     * About tab of the About dialog. If the resource cannot be
     * found, the tab will indicate this.
     * 
     * e.g. META-INF/minimiser/myapp/AboutMyApplication.html
     * 
     * @return a resource path
     */
    String getAboutDetailsResourcePath();
    
    /**
     * Obtain a path to a resource file that contains the text
     * shown in the "What's new in this release?" dialog - i.e.
     * the change log.
     * 
     * This resource is a .txt file in wiki-like markup syntax
     * as described inthe framework user guide; it will be
     * transformed and rendered as HTML accordingly.
     * 
     * If left null or blank, or if the resource cannot be
     * found, the tab will indicate this.
     * 
     * e.g. META-INF/minimiser/myapp/changelog.txt
     * 
     * @return a resource path
     */
    String getChangeLogResourcePath();
    
    /**
     * Obtain a short description of the copyright/license details
     * of this plugin. Used by the About dialog.
     * 
     * e.g. (C) 2009 Algebraic, Inc.
     * or GPL2 2009 The FrooBar project
     * 
     * @return short copyright/license text.
     */
    String getShortLicenseDetails();
    
    /**
     * Obtain a path to a resource file that contains the copyright
     * or license text of this plugin. Used by the License tab
     * of the About dialog.
     * 
     * This resource can be a .txt or .html/.htm file; it will be
     * shown in a text or HTML rendered component accordingly.
     * 
     * If left null or blank, an empty tab will be shown in the
     * About dialog. If the resource cannot be found, the tab will
     * indicate this.
     * 
     * If you use the GPL version 2, you may use the framework's
     * copy of this text by returning "COPYING.txt" here.
     * 
     * e.g. META-INF/minimiser/myapp/ApachePublicLicense2.html
     * 
     * @return a resource path
     */
    String getFullLicenceDetailsResourcePath();
    
    /**
     * Obtain a path to a resource file that will be shown in the
     * background of the intro panel (i.e. the panel that shows the
     * 'Create new database', 'Open existing database' and 'exit'
     * buttons.
     * 
     * e.g. META-INF/minimiser/myapp/myimage.png
     * 
     * @return a resource path
     * @return
     */
    String getIntroPanelBackgroundGraphicResourcePath();
}
