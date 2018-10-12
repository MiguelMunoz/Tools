package com.neptunedreams.camera;

import java.awt.Color;

/**
 * It's been a while since I've looked at this code, but I believe it duplicates a standard industry filter to convert
 * High-Def color data to gray-scale data. (NTSC uses a different formula)
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 2:10 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class HdGrayFilter extends AbstractFilter {
	private final Color color;
	private final int loss;

	public HdGrayFilter(Color color) {
		this.color = color;
		loss = gray(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	protected int process(final int alpha, final int pRed, final int green, final int blue) {
		int red = gammaToLinear[pRed];
		int grn = gammaToLinear[green];
		int blu = gammaToLinear[blue];
//		int alf = (rgb & 0xff000000) >> 24;

		// divide by loss instead of 255 to compensate for the brightness loss caused by the filter color.
		red = (red * color.getRed()) / loss;
		grn = (grn * color.getGreen()) / loss;
		blu = (blu * color.getBlue()) / loss;

		// hdtv formula
		int gray = gray(red, grn, blu);

		// It occasionally comes to 256, which is too big.
		gray = linearToGamma[Math.min(gray, 255)];
		return recombine(alpha, gray, gray, gray);
	}

	private int gray(final int red, final int grn, final int blu) {
		return ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
	}
}
