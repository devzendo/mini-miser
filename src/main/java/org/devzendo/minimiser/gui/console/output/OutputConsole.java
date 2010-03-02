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

package org.devzendo.minimiser.gui.console.output;

/**
 * An OutputConsole is essentially a wrapper around a Log4j logger.
 * 
 * @author matt
 *
 */
public interface OutputConsole {
    /**
     * Output some debugging object. In the context of an OutputConsole this
     * might just be echoing the lines entered via an InputConsole.
     * @param obj the object to debug
     */
    void debug(Object obj);
    
    /**
     * Output some normal, expected output.
     * @param obj the object to output
     */
    void info(Object obj);
    
    /**
     * Output a warning about something
     * @param obj the thing to warn about.
     */
    void warn(Object obj);
    
    /**
     * Output an error about something
     * @param obj the thing to raise as an error.
     */
    void error(Object obj);
}
