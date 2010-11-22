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

import org.devzendo.commonapp.gui.menu.MenuIdentifier;

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
    public static final MenuIdentifier FILE_NEW = new MenuIdentifier("FileNew");
    /**
     *
     */
    public static final MenuIdentifier FILE_OPEN = new MenuIdentifier("FileOpen");
    /**
     *
     */
    public static final MenuIdentifier FILE_CLOSE = new MenuIdentifier("FileClose");
    /**
     *
     */
    public static final MenuIdentifier FILE_CLOSE_ALL = new MenuIdentifier("FileCloseAll");
    /**
     *
     */
    public static final MenuIdentifier FILE_IMPORT = new MenuIdentifier("FileImport");
    /**
     *
     */
    public static final MenuIdentifier FILE_EXPORT = new MenuIdentifier("FileExport");
    /**
     *
     */
    public static final MenuIdentifier FILE_EXIT = new MenuIdentifier("FileExit");

    // Window menu is handled internally by the menu
    // View menu is handled internally by the menu

    /**
     *
     */
    public static final MenuIdentifier TOOLS_OPTIONS = new MenuIdentifier("ToolsOptions");
    /**
     *
     */
    public static final MenuIdentifier HELP_WELCOME = new MenuIdentifier("HelpWelcome");
    /**
     *
     */
    public static final MenuIdentifier HELP_WHATS_NEW = new MenuIdentifier("HelpWhatsNew");
    /**
     *
     */
    public static final MenuIdentifier HELP_ABOUT = new MenuIdentifier("HelpAbout");
    /**
     *
     */
    public static final MenuIdentifier HELP_CONTENTS = new MenuIdentifier("HelpContents");
    /**
     *
     */
    public static final MenuIdentifier HELP_CHECK_FOR_UPDATES = new MenuIdentifier("HelpCheckForUpdates");
}
