package org.devzendo.minimiser.gui.wizard;

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
import org.devzendo.minimiser.prefs.Prefs;
import org.netbeans.spi.wizard.WizardPage;


/**
 * A Wizard Page of the largest size we use - that's big enough to contain a
 * JFileChooser.
 * 
 * @author matt
 *
 */
public abstract class MiniMiserWizardPage extends WizardPage {
    private static final long serialVersionUID = 8120003372114550270L;
    private static final Logger LOGGER = Logger.getLogger(MiniMiserWizardPage.class);
    private static Dimension pageDimension = null;
    
    /**
     * Construct the common wizard page
     */
    public MiniMiserWizardPage() {
        //getPanelDimension();
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
            try {
                resourceAsStream.close();
            } catch (final IOException e) {
            }
        } catch (final IOException e) {
            LOGGER.warn("Couldn't read coins image: " + e.getMessage(), e);
        }
    }
    
    /**
     * @return a panel big enough to hold a JFileChooser, our largest component
     */
    public final JPanel createNicelySizedPanel() {
        final JPanel panel = new JPanel();
        panel.setPreferredSize(pageDimension);
        return panel;
    }

    /**
     * Return the dimensions of the usual wizard page, calculating and storing
     * these if they haven't been determined and stored before.
     * @param prefs the prefs, to store the dimension in, for future runs
     * @return the dimension of the panel
     */
    public static Dimension getPanelDimension(final Prefs prefs) {
        synchronized (MiniMiserWizardPage.class) {
            if (pageDimension == null) {
                final String wizardPanelSize = prefs.getWizardPanelSize();
                if (wizardPanelSize.equals("")) {
                    LOGGER.debug("Wizard panel size is not yet stored; computing it");
                    final JPanel panel = new JPanel();
                    final JFileChooser fileChooser = new JFileChooser(getTempDir());
                    fileChooser.validate();
                    panel.add(fileChooser);
                    panel.validate();
                    pageDimension = panel.getPreferredSize();
                    final String size = String.format("%d,%d", pageDimension.width, pageDimension.height);
                    LOGGER.debug("Storing the wizard panel size as '" + size + "'");
                    prefs.setWizardPanelSize(size);
                } else {
                    LOGGER.debug("Wizard panel size is stored as '" + wizardPanelSize + "'");
                    final String[] geomNumStrs = wizardPanelSize.split(",");
                    final int[] geomNums = new int[geomNumStrs.length];
                    for (int i = 0; i < geomNumStrs.length; i++) {
                        geomNums[i] = Integer.parseInt(geomNumStrs[i]);
                    }
                    pageDimension = new Dimension(geomNums[0], geomNums[1]);
                }
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
