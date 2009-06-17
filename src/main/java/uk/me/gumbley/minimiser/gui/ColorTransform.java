package uk.me.gumbley.minimiser.gui;

import java.awt.Color;

/**
 * A ColorTransform is initialised with a start and an end color. It computes
 * intermediate colors between these two given a proportion of the "distance"
 * between them from 0.0 (i.e. the start color) to 1.0 (i.e. the end color).
 * <p>
 * The intermediate values are computed by taking the RGB values of the start
 * and end colors, and using the proportion to "reposition the slider" on each
 * color-component (out of R, G and B).
 * <p>
 * This may not be the color-science proven way to do this, but it gives a
 * range of colors that morph between the start and end, in some sense.   
 * @author matt
 *
 */
public final class ColorTransform {
//    private static final Logger LOGGER = Logger.getLogger(ColorTransform.class);

    private final Color startColor;
    private final Color endColor;
    private final int redDistance;
    private final int greenDistance;
    private final int blueDistance;
    private final int dRed;
    private final int dGreen;
    private final int dBlue;

    /**
     * Initialise the ColorTransform with its start and end colors.
     * @param start the start Color
     * @param end the end Color
     */
    public ColorTransform(final Color start, final Color end) {
        this.startColor = start;
        this.endColor = end;
//        LOGGER.debug("ColorTransform(" + startColor + ", " + endColor + ")");
        // calculate distances
        redDistance = Math.abs(startColor.getRed() - endColor.getRed());
        greenDistance = Math.abs(startColor.getGreen() - endColor.getGreen());
        blueDistance = Math.abs(startColor.getBlue() - endColor.getBlue());
        // proportions are taken from the start and
        // end, but which is lowest, start or end? need to multiply the
        // proportion by a direction
        dRed = startColor.getRed() > endColor.getRed() ? -1 : 1;
        dGreen = startColor.getGreen() > endColor.getGreen() ? -1 : 1;
        dBlue = startColor.getBlue() > endColor.getBlue() ? -1 : 1;
//        LOGGER.debug("Distance: red: " + redDistance + " green: " + greenDistance + " blue: " + blueDistance);
//        LOGGER.debug("Direction: red: " + dRed + " green: " + dGreen + " blue: " + dBlue);
    }

    /**
     * Obtain a color between the start and end colors, derived by moving
     * each of the R, G and B "sliders" toward the end color's values by an
     * amount given by the proportion.
     * @param proportion the proportion of the distance between start and
     * end colors, between 0.0 and 1.0
     * @return the color that's proportional between start and end
     */
    public Color getProportionalColor(final double proportion) {
        if (proportion < 0.0 || proportion > 1.0) {
            throw new IllegalArgumentException("Proportion of " + proportion + " is outside the range [0.0, 1.0]");
        }
//        LOGGER.debug("Color " + proportion + " between " + startColor + " and " + endColor);
        final double redShift = (dRed * proportion * redDistance);
        final double newRed = startColor.getRed() + redShift; 
        final double greenShift = (dGreen * proportion * greenDistance);
        final double newGreen = startColor.getGreen() + greenShift; 
        final double blueShift = (dBlue * proportion * blueDistance);
        final double newBlue = startColor.getBlue() + blueShift;
        final int iNewRed = roundBounded(newRed);
        final int iNewGreen = roundBounded(newGreen);
        final int iNewBlue = roundBounded(newBlue);
//        LOGGER.debug("New red: " + newRed + " = " + iNewRed + "; shift = " + redShift);
//        LOGGER.debug("New green: " + newGreen + " = " + iNewGreen + "; shift = " + greenShift);
//        LOGGER.debug("New blue: " + newBlue + " = " + iNewBlue + "; shift = " + blueShift);
        return new Color(iNewRed, iNewGreen, iNewBlue);
    }

    private int roundBounded(final double newColor) {
        final int iNewColor = Math.round((float) newColor);
        if (iNewColor < 0) {
            return 0;
        } else if (iNewColor > 255) {
            return 255;
        } else {
            return iNewColor;
        }
    }
}
