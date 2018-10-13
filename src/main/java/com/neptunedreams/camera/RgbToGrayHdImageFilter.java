package com.neptunedreams.camera;

import java.awt.Color;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 3:07 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class RgbToGrayHdImageFilter extends AbstractImageFilter {
	private static final int BYTE_MAX = 256;
	private static final double CROSSOVER = 0.04045;

	private final Color color;
	private final int loss;

	public RgbToGrayHdImageFilter(final Color color) {
		super();
		this.color = color;
		// without compensating for the loss, filtered images get very dark.
		// HD TV
		loss = ((color.getRed() * 54213) + (color.getGreen() * 182376) + (color.getBlue() * 18411)) / 255000;
		// N T S C
//		loss = ((color.getRed() * 76245) + (color.getGreen() * 149685) + (color.getBlue() * 29070)) / 255000;
	}
	
	private RgbToGrayHdImageFilter(RgbToGrayHdImageFilter original) {
		super();
		color = original.color;
		loss = original.loss;
	}

	@Override
	public int process(int red, int grn, int blu, final int alf) {
		red = (red * color.getRed()) / loss;
		grn = (grn * color.getGreen()) / loss;
		blu = (blu * color.getBlue()) / loss;
		// hdtv formula
		//noinspection MagicNumber
		int gray = ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
		// N T S C formula:
//		int gray = ((red * 76245) + (grn * 149685) + (blu * 29070)) / 255000;

		// It occasionally comes to 256, which is too big.
		//noinspection MagicNumber
		gray = linearToGamma[Math.min(gray, 255)];
		return recombine(alf, gray, gray, gray);
	}

	@SuppressWarnings({"MethodDoesntCallSuperMethod", "UseOfClone"})
	@Override
	public RgbToGrayHdImageFilter clone() {
		//noinspection CloneCallsConstructors
		return new RgbToGrayHdImageFilter(this);
	}
}
