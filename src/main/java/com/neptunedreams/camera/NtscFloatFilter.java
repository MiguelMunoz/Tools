package com.neptunedreams.camera;

import java.awt.Color;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 2:11 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class NtscFloatFilter extends AbstractFilter {
	private static final int BYTE_MAX = 256;
	private static final double CROSSOVER = 0.04045;

	private final Color color;
	private final double loss;
	private static final double root = 1.0 / 2.4;
	private static final double reverseCrossover = Math.pow((CROSSOVER + 0.055) / 1.055, 2.4);
	private static final double lumRed = 0.299 * 255.0;
	private static final double lumGreen = 0.587 * 255.0;
	private static final double lumBlue = 0.114 * 255.0;

	public NtscFloatFilter(final Color color) {
		this.color = color;
		loss = gray(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	protected int process(final int alpha, final int pRed, final int green, final int blue) {
		double red = dblGammaToLinear[pRed];
		double grn = dblGammaToLinear[green];
		double blu = dblGammaToLinear[blue];
//		int alf = (rgb & 0xff000000) >> 24;

		// divide by loss instead of 255 to compensate for the brightness loss caused by the filter color.
		red = (red * color.getRed()) / loss;
		grn = (grn * color.getGreen()) / loss;
		blu = (blu * color.getBlue()) / loss;
		// hdtv formula
//					int gray = ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
		// N T S C formula:
		double gray = gray(red, grn, blu);

		// It occasionally comes to 256, which is too big.
		int iGray = (int) Math.round(dblLinearToGamma(gray));
		return recombine(alpha, iGray, iGray, iGray);
	}

	private double gray(final double red, final double grn, final double blu) {
		return ((red * lumRed) + (grn * lumGreen) + (blu * lumBlue)) / 255.0;
	}

	protected static final double[] dblGammaToLinear = createGammaToLinear();

	private static double[] createGammaToLinear() {
		double [] linear = new double[BYTE_MAX];
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			linear[ii] = dblGammaToLinear(ii);
		}
		return linear;
	}

	private static double dblGammaToLinear(final int ii) {
		double normal = ii / 255.0;
		if (normal < CROSSOVER) {
			return (normal * 255.0) / 12.92;
		} else {
			return 255.0 * Math.pow((normal + 0.055) / 1.055, 2.4);
		}
	}

	protected static final double[] dblLinearToGamma = createLinearToGamma();

	private static double[] createLinearToGamma() {
		double [] gamma = new double[BYTE_MAX];
		for (int ii = 0; ii < BYTE_MAX; ++ii) {
			double normal = ii / 255.0;
			gamma[ii] = dblLinearToGamma(normal);
		}
		return gamma;
	}

	private static double dblLinearToGamma(final double normal) {
		if (normal < reverseCrossover) {
			return 255.0 * normal * 12.92;
		} else {
			return 255.0 * ((Math.pow(normal, root) * 1.055) - 0.055);
		}
	}

}
