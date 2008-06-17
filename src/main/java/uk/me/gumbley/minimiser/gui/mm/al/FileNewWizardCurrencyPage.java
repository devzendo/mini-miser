package uk.me.gumbley.minimiser.gui.mm.al;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import uk.me.gumbley.minimiser.gui.StripyListCellRenderer;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;

/**
 * File New Wizard - choose currency
 * @author matt
 *
 */
public class FileNewWizardCurrencyPage extends MiniMiserWizardPage {
    public static final String CURRENCY = "currency";
    private static final long serialVersionUID = -9021172845859544894L;
    private JList currencyList;

    /**
     * Construct the currency page
     */
    public FileNewWizardCurrencyPage() {
        initComponents();
    }

    private void initComponents() {
        JPanel sizedPanel = createNicelySizedPanel();
        sizedPanel.setLayout(new BorderLayout());
        
        JTextArea label = new JTextArea(getText());
        label.setEditable(false);
        sizedPanel.add(label, BorderLayout.NORTH);
        
        Vector<String> currencies = new Vector<String>();
        // TODO sex this up with flags
        currencies.add("UK Sterling £");
        currencies.add("US Dollar $");
        currencies.add("EU Euro €");
        currencies.add("HK Dollar $");
        currencies.add("THB Baht B");
        currencyList = new JList(currencies);
        currencyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        currencyList.setName(CURRENCY);
        currencyList.setCellRenderer(new StripyListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(currencyList);
        sizedPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(sizedPanel);
    }

    private String getText() {
        return
          "Please choose the currency you will use most frequently in this\n"
        + "database. Each account you add can have its own currency, and the\n"
        + "currency you choose here will be used as the initial choice for each\n"
        + "account's currency.\n";
    }

    /**
     * @return wizard page description for the LH area
     */
    public static String getDescription() {
        return "Choose default currency";
    }
    
    /**
     * {@inheritDoc}
     */
    protected String validateContents(final Component component, final Object object) {
        if (currencyList.getSelectedIndex() == -1) {
            return "You must select a currency";
        } else {
            return null;
        }
    }
        
}
