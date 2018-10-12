package com.neptunedreams.camera;

import java.awt.image.RGBImageFilter;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 1:59 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public abstract class AbstractFilter extends RGBImageFilter {
	private static final int BYTE_MAX = 256;
	private static final double CROSSOVER = 0.04045;
	protected static final int[] gammaToLinear = createGammaToLinear();

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

	protected static final int[] linearToGamma = createLinearToGamma();

	private static int[] createLinearToGamma() {
		double reverseCrossover = Math.pow((CROSSOVER + 0.055) / 1.055, 2.4);
		int[] gamma = new int[BYTE_MAX];
		double root = 1.0 / 2.4;
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			double normal = ii / 255.0;
			if (normal < reverseCrossover) {
				gamma[ii] = (int) Math.round(255.0 * normal * 12.92);
			} else {
				gamma[ii] = (int) Math.round(255.0 * ((Math.pow(normal, root) * 1.055) - 0.055));
			}
		}
		return gamma;
	}

	@Override
	public int filterRGB(final int x, final int y, final int rgb) {
		int red = (rgb & 0x00ff0000) >> 16;
		int grn = (rgb & 0x0000ff00) >> 8;
		int blu = (rgb & 0x000000ff);
		int alf = (rgb & 0xff000000) >> 24;
		assert recombine(alf, red, grn, blu) == rgb : String.format("Mismatch from 0x%08x to 0x%02x 0x%02x 0x%02x 0x%02x", rgb, alf, red, grn, blu);

		return process(alf, red, grn, blu);
	}
	
	protected abstract int process(int alpha, int red, int green, int blue);

	final private static int MAX_BYTE = 256;

	@SuppressWarnings("MagicNumber")
	protected static int recombine(int alpha, int red, int green, int blue) {
		assert alpha >= 0 && alpha < MAX_BYTE;
		assert red >= 0 && red < MAX_BYTE;
		assert green >= 0 && green < MAX_BYTE;
		assert blue >= 0 && blue < MAX_BYTE;
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
