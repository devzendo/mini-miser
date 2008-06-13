package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.BorderLayout;
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
import org.netbeans.spi.wizard.WizardPage;
import uk.me.gumbley.minimiser.util.PasswordValidator;

public final class FileNewWizardSecurityOptionPage extends WizardPage {
    private boolean encrypted;
    private JCheckBox encryptCheckBox;
    private JPasswordField passwordField;
    private JPasswordField passwordReentryField;
    public FileNewWizardSecurityOptionPage() {
        encrypted = false;
        initComponents();
    }

    private void initComponents() {
        JPanel sizedPanel = FileNewWizard.createNicelySizedPanel();
        sizedPanel.setLayout(new BorderLayout(20, 20));
        
        encryptCheckBox = new JCheckBox("Use encryption to encrypt the new database?", encrypted);
        sizedPanel.add(encryptCheckBox, BorderLayout.NORTH);
        
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());
        
        final Border titledBorder = BorderFactory.createTitledBorder("Password entry");
        final JPanel passwordEntryPanel = new JPanel();
        passwordEntryPanel.setBorder(titledBorder);
        final GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        passwordEntryPanel.setLayout(gridBagLayout);
        
        JLabel passwordLabel = new JLabel("Password");
        constraints.weightx = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        gridBagLayout.setConstraints(passwordLabel, constraints);
        passwordEntryPanel.add(passwordLabel);
        
        passwordField = new JPasswordField(PasswordValidator.MAX_PASSWORD_LENGTH);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(passwordField, constraints);
        passwordEntryPanel.add(passwordField);
        
        JLabel passwordReentryLabel = new JLabel("Re-enter password");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(passwordReentryLabel, constraints);
        passwordEntryPanel.add(passwordReentryLabel);
        
        passwordReentryField = new JPasswordField(PasswordValidator.MAX_PASSWORD_LENGTH);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(passwordReentryField, constraints);
        passwordEntryPanel.add(passwordReentryField);
        
        innerPanel.add(passwordEntryPanel, BorderLayout.NORTH);
        
        sizedPanel.add(innerPanel, BorderLayout.CENTER);
        
        add(sizedPanel);
    }

    protected String validateContents(final Component component, final Object object) {
        if (!encryptCheckBox.isSelected()) {
//            setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
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
    
    public static String getDescription() {
        return "Protect with Encryption?";
    }
}
