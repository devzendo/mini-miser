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
package org.devzendo.minimiser.gui.menu;

/**
 * Identifiers for the various menu items.
 * 
 * @author matt
 *
 */
public class SystemMenuIdentifiers {
    /**
     *
     */
    public static MenuIdentifier FileNew = new MenuIdentifier("FileNew");
    /**
     *
     */
    public static MenuIdentifier FileOpen = new MenuIdentifier("FileOpen");
    /**
     *
     */
    public static MenuIdentifier FileClose = new MenuIdentifier("FileClose");
    /**
     *
     */
    public static MenuIdentifier FileCloseAll = new MenuIdentifier("FileCloseAll");
    /**
     *
     */
    public static MenuIdentifier FileImport = new MenuIdentifier("FileImport");
    /**
     *
     */
    public static MenuIdentifier FileExport = new MenuIdentifier("FileExport");
    /**
     *
     */
    public static MenuIdentifier FileExit = new MenuIdentifier("FileExit");

    // Window menu is handled internally by the menu
    // View menu is handled internally by the menu

    /**
     *
     */
    public static MenuIdentifier ToolsOptions = new MenuIdentifier("ToolsOptions");
    /**
     *
     */
    public static MenuIdentifier HelpWelcome = new MenuIdentifier("HelpWelcome");
    /**
     *
     */
    public static MenuIdentifier HelpWhatsNew = new MenuIdentifier("HelpWhatsNew");
    /**
     *
     */
    public static MenuIdentifier HelpAbout = new MenuIdentifier("HelpAbout");
    /**
     *
     */
    public static MenuIdentifier HelpContents = new MenuIdentifier("HelpContents");
    /**
     *
     */
    public static MenuIdentifier HelpCheckForUpdates = new MenuIdentifier("HelpCheckForUpdates");
}
