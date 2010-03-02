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

import java.io.IOException;

/**
 * Provides feedback to the user on the progress of the update check.
 * @author matt
 *
 */
public interface UpdateProgressAdapter {
    /**
     * Called if the check for updates is disallowed via prefs. Only the
     * finished method should be called on the adapter after this. This callback
     * indicates an error since update checks should never be triggered
     * if disallowed.
     */
    void updateCheckDisallowed();

    /**
     * Called if a check has already been made today. Only the finished method 
     * will be called on the adapter after this. This is not an error.
     */
    void alreadyCheckedToday();

    /**
     * Called when the conditions for an update check to proceed have been met.
     * You will have subsequent calls after receiving this.
     */
    void checkStarted();

    /**
     * A communications exception has occurred attempting to retrieve remote
     * data. Only the finished method call will be made after receiving this.
     * @param exception details of the exception
     */
    void commsFailure(IOException exception);

    /**
     * The application plugin has not reported its version number,
     * so no check is being performed. This is an error. Only the
     * finished method call will be made after receiving this.
     */
    void noApplicationVersionDeclared();
    
    /**
     * The application plugin has not reported an update URL. This
     * is an error. Only the finished method will be made after
     * receiving this.
     */
    void noUpdateURLDeclared();
    
    /**
     * The remote version is the same as that installed locally. Only the
     * finished method call after this.
     */
    void noUpdateAvailable();

    /**
     * There is a different remote version than that installed. Only the
     * finished method call happens after this. You should receive the message
     * on the message queue informing you of the update's contents. If you get
     * this, then all remote access and change log transformation succeeded. 
     */
    void updateAvailable();

    /**
     * The retrieved change log could not be transformed. Only the finished 
     * method call will be made after this.
     * @param exception details of the exception
     */
    void transformFailure(IOException exception);

    /**
     * The retrieved change log could not be transformed. Only the finished 
     * method call will be made after this.
     * @param exception details of the exception
     */
    void transformFailure(ParseException exception);
    
    /**
     * Always called as the final act of the update checker, after all above
     * methods have been called, after any message has been put on the
     * MessageQueue and any Prefs entries updated.
     */
    void finished();
}
