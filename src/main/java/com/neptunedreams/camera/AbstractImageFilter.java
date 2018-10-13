package com.neptunedreams.camera;

import java.awt.image.RGBImageFilter;

/**
 * Formulas taken from http://en.wikipedia.org/wiki/Grayscale, accessed 6/18/2013.
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 1:59 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"WeakerAccess", "CloneableClassWithoutClone"})
public abstract class AbstractImageFilter extends RGBImageFilter {
	private static final int BYTE_MAX = 256;
	private static final double CROSSOVER = 0.04045;
	protected static final int[] gammaToLinear = createGammaToLinear();
	private static final int LOW_BYTE_MASK = 0x000000ff;
	private static final double DOUBLE_MAX = 255.0;

	@SuppressWarnings("MagicNumber")
	private static int[] createGammaToLinear() {
		int[] linear = new int[BYTE_MAX];
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			double normal = ii / DOUBLE_MAX;
			if (normal < CROSSOVER) {
				linear[ii] = (int) Math.round((normal * DOUBLE_MAX) / 12.92);
			} else {
				linear[ii] = (int) Math.round(DOUBLE_MAX * StrictMath.pow((normal + 0.055) / 1.055, 2.4));
			}
		}
		return linear;
	}

	protected static final int[] linearToGamma = createLinearToGamma();

	@SuppressWarnings("MagicNumber")
	private static int[] createLinearToGamma() {
		double reverseCrossover = StrictMath.pow((CROSSOVER + 0.055) / 1.055, 2.4);
		int[] gamma = new int[BYTE_MAX];
		double root = 1.0 / 2.4;
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			double normal = ii / DOUBLE_MAX;
			if (normal < reverseCrossover) {
				gamma[ii] = (int) Math.round(DOUBLE_MAX * normal * 12.92);
			} else {
				gamma[ii] = (int) Math.round(DOUBLE_MAX * ((StrictMath.pow(normal, root) * 1.055) - 0.055));
			}
		}
		return gamma;
	}

	@SuppressWarnings("MagicNumber")
	@Override
	public int filterRGB(final int x, final int y, final int rgb) {
		int alf = (rgb >> 24) & LOW_BYTE_MASK;
		int red = (rgb >> 16) & LOW_BYTE_MASK;
		int grn = (rgb >>  8) & LOW_BYTE_MASK;
		int blu = (rgb        & LOW_BYTE_MASK);
		assert recombine(alf, red, grn, blu) == rgb : 
				String.format("Mismatch from 0x%08x to 0x%02x 0x%02x 0x%02x 0x%02x", rgb, alf, red, grn, blu);

		return process(alf, red, grn, blu);
	}
	
	protected abstract int process(int alpha, int red, int green, int blue);

	private static final int MAX_BYTE = 256;

	@SuppressWarnings("MagicNumber")
	protected static int recombine(int alpha, int red, int green, int blue) {
		assert (alpha >= 0) && (alpha < MAX_BYTE) : String.format("Alpha: 0x%02x = %1$d", alpha);
		assert (red   >= 0) && (red   < MAX_BYTE) : String.format("Red:   0x%02x = %1$d", red);
		assert (green >= 0) && (green < MAX_BYTE) : String.format("Green: 0x%02x = %1$d", green);
		assert (blue  >= 0) && (blue  < MAX_BYTE) : String.format("Blue:  0x%02x = %1$d", blue);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
