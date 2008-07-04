package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelProvider;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;
import uk.me.gumbley.minimiser.util.PasswordValidator;

public class FileOpenWizardEnterPasswordPage extends MiniMiserWizardPage {
    /**
     * The key in the results map that refers to the password for this database.
     */
    public static final String PASSWORD = "password";
    private JPasswordField passwordField;
    private JLabel helpfulPasswordHints;
    private JLabel passwordLabel;
    
    public FileOpenWizardEnterPasswordPage() {
        initComponents();
    }

    private void initComponents() {
        final JPanel sizedPanel = createNicelySizedPanel();
        sizedPanel.setLayout(new BorderLayout(20, 20));
        
        final JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());

        final JPanel passwordEntryPanel = new JPanel();
        final GridBagLayout gridBagLayout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        passwordEntryPanel.setLayout(gridBagLayout);

        helpfulPasswordHints = new JLabel(getComment());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        gridBagLayout.setConstraints(helpfulPasswordHints, constraints);
        passwordEntryPanel.add(helpfulPasswordHints);

        passwordLabel = new JLabel("Password");
        constraints.weightx = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(passwordLabel, constraints);
        passwordEntryPanel.add(passwordLabel);
        
        passwordField = new JPasswordField(PasswordValidator.MAX_PASSWORD_LENGTH);
        passwordField.setName(PASSWORD);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        gridBagLayout.setConstraints(passwordField, constraints);
        passwordEntryPanel.add(passwordField);
        
        innerPanel.add(passwordEntryPanel, BorderLayout.NORTH);
        sizedPanel.add(innerPanel, BorderLayout.CENTER);
        add(sizedPanel);
    }

    private String getComment() {
        return "<html><br>"
            + "This database is encrypted, and its password must be given before it can be opened.<br><br>"
            + "As a reminder, " + AppName.getAppName() + " passwords must be at least "
            + PasswordValidator.MIN_PASSWORD_LENGTH + " "
            + StringUtils.pluralise("character", PasswordValidator.MIN_PASSWORD_LENGTH)
            + " long, with at least " + PasswordValidator.MIN_DIGITS
            + " " + StringUtils.pluralise("digit", PasswordValidator.MIN_DIGITS) + ".<br>"
            + "They must contain both upper and lower case letters."
            + "</html>";
    }

    /**
     * {@inheritDoc}
     */
    protected String validateContents(final Component component, final Object object) {
        final char[] pwd = passwordField.getPassword();
        final String critique = PasswordValidator.criticisePassword(pwd);
        if (critique != null) {
            return critique;
        }
        return null;
    }

    /**
     * Page description
     * @return the page description
     */
    public static String getDescription() {
        return "Enter password for encrypted database";
    }

}
