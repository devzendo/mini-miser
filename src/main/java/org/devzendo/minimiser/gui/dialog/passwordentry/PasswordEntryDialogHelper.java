package org.devzendo.minimiser.gui.dialog.passwordentry;

import java.awt.Frame;
import java.util.concurrent.CountDownLatch;

import uk.me.gumbley.commoncode.gui.GUIUtils;

/**
 * Password Entry Dialog Helper that is called on some non-EDT thread to
 * prompt for a password, launches the dialog on the EDT, then returns the
 * entered password (or empty string, if user cancelled) to the calling
 * thread.
 * 
 * TODO rework this to use the GUIValueObtainer
 *  
 * @author matt
 *
 */
public final class PasswordEntryDialogHelper {
    private Object passwordLock = new Object();
    private char[] password = new char[0];
    private CountDownLatch passwordSetLatch;

    /**
     * Construct the helper 
     */
    public PasswordEntryDialogHelper() {
    }
    
    /**
     * Obtain the password.
     * @param frame the parent Frame over which to create this dialog.
     * @param dbName the name of the database in question.
     * @return the entered password, or an empty string, if the user cancelled.
     */
    public String promptForPassword(final Frame frame, final String dbName) {
        passwordSetLatch = new CountDownLatch(1);
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                final PasswordEntryDialog dialog = new PasswordEntryDialog(frame, dbName);
                dialog.pack();
                dialog.setLocationRelativeTo(frame);
                dialog.setVisible(true);
                synchronized (passwordLock) {
                    password = dialog.getPassword();
                }
                passwordSetLatch.countDown();
            }
        });
        try {
            passwordSetLatch.await();
        } catch (final InterruptedException e) {
            return "";
        }
        synchronized (passwordLock) {
            return password.length == 0 ? "" : new String(password);
        }
    }
}
