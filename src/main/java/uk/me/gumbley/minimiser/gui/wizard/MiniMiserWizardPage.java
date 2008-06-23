package uk.me.gumbley.minimiser.gui.wizard;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardPage;

/**
 * A Wizard Page of the largest size we use - that's big enough to contain a
 * JFileChooser.
 * 
 * @author matt
 *
 */
public abstract class MiniMiserWizardPage extends WizardPage {
    private static final Logger LOGGER = Logger.getLogger(MiniMiserWizardPage.class);
    private static Dimension pageDimension = null;
    
    /**
     * Construct the common wizard page
     */
    public MiniMiserWizardPage() {
        getPanelDimension();
    }

    /**
     * Set the left-hend graphic
     */
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
    
    /**
     * @return a panel big enough to hold a JFileChooser, our largest component
     */
    public JPanel createNicelySizedPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(getPanelDimension());
        return panel;
    }

    public static Dimension getPanelDimension() {
        synchronized (MiniMiserWizardPage.class) {
            if (pageDimension == null) {
                JPanel panel = new JPanel();
                final JFileChooser fileChooser = new JFileChooser(getTempDir());
                fileChooser.validate();
                panel.add(fileChooser);
                panel.validate();
                pageDimension = panel.getPreferredSize();
            }
            return pageDimension;
        }
    }

    @SuppressWarnings("finally")
    private static String getTempDir() {
        String tempDir = null;
        try {
            final File tempFile = File.createTempFile("minimiser", ".tmp");
            tempDir = tempFile.getParent();
            tempFile.deleteOnExit();
            tempFile.delete();
            LOGGER.info("temp dir is " + tempDir);
        } catch (final IOException ioe) {
            // nop
        } finally {
            return tempDir;
        }
    }
}
