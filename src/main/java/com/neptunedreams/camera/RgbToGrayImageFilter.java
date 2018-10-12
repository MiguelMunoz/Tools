package com.neptunedreams.camera;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

/**
 * Formulas taken from http://en.wikipedia.org/wiki/Grayscale, accessed 6/18/2013.
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/18/13
 * <br>Time: 4:11 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("MagicNumber")
class RgbToGrayImageFilter extends RGBImageFilter {
	private static final int BYTE_MAX = 256;
	private static final double CROSSOVER = 0.04045;
	private static int[] gammaToLinear = createGammaToLinear();

	private static int[] createGammaToLinear() {
		int[] linear = new int[BYTE_MAX];
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			double normal = ii / 255.0;
			if (normal < CROSSOVER) {
				linear[ii] = (int) Math.round((normal * 255.0) / 12.92);
			} else {
				linear[ii] = (int) Math.round(255.0 * Math.pow((normal + 0.055) / 1.055, 2.4));
			}
		}
		return linear;
	}
	
	private static int[] linearToGamma = createLinearToGamma();

	private static int[] createLinearToGamma() {
		double reverseCrossover = Math.pow((CROSSOVER + 0.055)/1.055, 2.4);
		int[] gamma = new int[BYTE_MAX];
		double root = 1.0/2.4;
		for (int ii=0; ii< BYTE_MAX; ++ii) {
			double normal = ii/255.0;
			if (normal < reverseCrossover) {
				gamma[ii] = (int) Math.round(255.0 * normal * 12.92);
			} else {
				gamma[ii] = (int) Math.round(255.0 * ((Math.pow(normal, root) * 1.055) - 0.055));
			}
		}
		return gamma;
	}

	private final Color color;
	private final int loss;

	@SuppressWarnings("WeakerAccess")
	public RgbToGrayImageFilter(final Color color) {
		this.color = color;
		// without compensating for the loss, filtered images get very dark.
		// HD TV
//		loss = ((color.getRed() * 54213) + (color.getGreen() * 182376) + (color.getBlue() * 18411)) / 255000;
		// N T S C
		loss = ((color.getRed() * 76245) + (color.getGreen() * 149685) + (color.getBlue() * 29070)) / 255000;
	}

	@Override
	public int filterRGB(final int x, final int y, final int rgb) {
		int red = gammaToLinear[(rgb & 0x00ff0000) >> 16];
		int grn = gammaToLinear[(rgb & 0x0000ff00) >> 8];
		int blu = gammaToLinear[(rgb & 0x000000ff)];
		int alf = (rgb & 0xff000000) >> 24;

		// divide by loss instead of 255 to compensate for the brightness loss caused by the filter color.
		red = (red * color.getRed()) / loss;
		grn = (grn * color.getGreen()) / loss;
		blu = (blu * color.getBlue()) / loss;
		// hdtv formula
//					int gray = ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
		// N T S C formula:
		int gray = ((red * 76245) + (grn * 149685) + (blu * 29070)) / 255000;

		// It occasionally comes to 256, which is too big.
		gray = linearToGamma[Math.min(gray, 255)];
		return (alf << 24) | (gray << 16) | (gray << 8) | gray;
	}
}
