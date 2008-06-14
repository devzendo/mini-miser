package uk.me.gumbley.minimiser.gui.wizard;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardPage;

import uk.me.gumbley.minimiser.common.AppName;

/**
 * A Wizard Page of the largest size we use - that's big enough to contain a
 * JFileChooser.
 * 
 * @author matt
 *
 */
public abstract class MiniMiserWizardPage extends WizardPage {
    private final static Logger LOGGER = Logger.getLogger(MiniMiserWizardPage.class);
    private static Dimension pageDimension = null;
    
    public MiniMiserWizardPage() {
        getPanelDimension();
    }

    public static void setLHGraphic() {
        final String key = "wizard.sidebar.image";
        final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("WizardCoinsVeryLightGrey.jpg");
        BufferedImage image;
        try {
            image = ImageIO.read(resourceAsStream);
            UIManager.put(key, image);
        } catch (final IOException e) {
            LOGGER.warn("Couldn't read coins image: " + e.getMessage(), e);
        }
    }
    public JPanel createNicelySizedPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(getPanelDimension());
        return panel;
    }

    private Dimension getPanelDimension() {
        synchronized (MiniMiserWizardPage.class) {
            if (pageDimension == null) {
                JPanel panel = new JPanel();
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.validate();
                panel.add(fileChooser);
                panel.validate();
                pageDimension = panel.getPreferredSize();
            }
            return pageDimension;
        }
    }
}
