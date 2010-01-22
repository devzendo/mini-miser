package org.devzendo.minimiser.gui.menu.actionlisteners.filenew;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.gui.wizard.MiniMiserWizardPage;
import org.devzendo.minimiser.util.PasswordValidator;


/**
 * File New Wizard - security options
 * 
 * @author matt
 */
public final class FileNewWizardSecurityOptionPage extends MiniMiserWizardPage {
    /**
     * The key in the results map that refers to the password for this database.
     */
    public static final String PASSWORD = "password";
    /**
     * The key in the results map that refers to whether thsi database is
     * encrypted or not.
     */
    public static final String ENCRYPTED = "encrypted";
    private static final long serialVersionUID = 2308707685829344793L;
    private static final boolean INITIAL_ENCRYPTED = false;
    private JCheckBox encryptCheckBox;
    private JPasswordField passwordField;
    private JPasswordField passwordReentryField;
    private JLabel passwordLabel;
    private JLabel passwordReentryLabel;
    private JLabel helpfulPasswordHints;

    /**
     * Create the security options wizard page
     */
    public FileNewWizardSecurityOptionPage() {
        initComponents();
    }

    private void initComponents() {
        final JPanel sizedPanel = createNicelySizedPanel();
        sizedPanel.setLayout(new BorderLayout(20, 20));
        
        encryptCheckBox = new JCheckBox("Use encryption to encrypt the new database?", INITIAL_ENCRYPTED);
        encryptCheckBox.setName(ENCRYPTED);
        sizedPanel.add(encryptCheckBox, BorderLayout.NORTH);
        
        final JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());

        final Border titledBorder = BorderFactory.createTitledBorder("Password entry");
        final JPanel passwordEntryPanel = new JPanel();
        passwordEntryPanel.setBorder(titledBorder);
        final GridBagLayout gridBagLayout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        passwordEntryPanel.setLayout(gridBagLayout);
        
        passwordLabel = new JLabel("Password");
        constraints.weightx = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        gridBagLayout.setConstraints(passwordLabel, constraints);
        passwordEntryPanel.add(passwordLabel);
        
        passwordField = new JPasswordField(PasswordValidator.MAX_PASSWORD_LENGTH);
        passwordField.setName(PASSWORD);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(passwordField, constraints);
        passwordEntryPanel.add(passwordField);
        passwordReentryLabel = new JLabel("Re-enter password");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(passwordReentryLabel, constraints);
        passwordEntryPanel.add(passwordReentryLabel);
        
        passwordReentryField = new JPasswordField(PasswordValidator.MAX_PASSWORD_LENGTH);
        // don't need to settName, as the wizard map only needs the correct
        // password once
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(passwordReentryField, constraints);
        passwordEntryPanel.add(passwordReentryField);
        
        helpfulPasswordHints = new JLabel(getComment());
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        gridBagLayout.setConstraints(helpfulPasswordHints, constraints);
        passwordEntryPanel.add(helpfulPasswordHints);
        
        innerPanel.add(passwordEntryPanel, BorderLayout.NORTH);
        sizedPanel.add(innerPanel, BorderLayout.CENTER);
        add(sizedPanel);
        enableDisableControls(INITIAL_ENCRYPTED);
    }

    private String getComment() {
        return "<html><br>"
            + "Good security practice recommends a password that is not easy to guess.<br><br>"
            + "Your password must be at least "
            + PasswordValidator.MIN_PASSWORD_LENGTH + " "
            + StringUtils.pluralise("character", PasswordValidator.MIN_PASSWORD_LENGTH)
            + " long, with at least " + PasswordValidator.MIN_DIGITS
            + " " + StringUtils.pluralise("digit", PasswordValidator.MIN_DIGITS) + ".<br>"
            + "It must contain both upper and lower case letters."
            + "</html>";
    }

    private void enableDisableControls(final boolean enable) {
        passwordLabel.setEnabled(enable);
        passwordField.setEnabled(enable);
        passwordReentryLabel.setEnabled(enable);
        passwordReentryField.setEnabled(enable);
        helpfulPasswordHints.setForeground(enable ? Color.BLACK : Color.LIGHT_GRAY);
    }

    /**
     * {@inheritDoc}
     */
    protected String validateContents(final Component component, final Object object) {
        if (encryptCheckBox.isSelected()) {
            enableDisableControls(true);
        } else {
            enableDisableControls(false);
            // setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
            return null;
        }
        final char[] pwd = passwordField.getPassword();
        final char[] repwd = passwordReentryField.getPassword();
        final String critique = PasswordValidator.criticisePassword(pwd);
        if (critique != null) {
            return critique;
        }
        boolean same = pwd.length == repwd.length;
        if (same) {
            for (int i = 0; i < pwd.length; i++) {
                if (pwd[i] != repwd[i]) {
                    same = false;
                    break;
                }
            }
        }
        if (!same) {
            return "The password and re-entered password do not match";
        }
        return null;
    }

    /**
     * Page description
     * @return the page description
     */
    public static String getDescription() {
        return "Protect with Encryption?";
    }
}
