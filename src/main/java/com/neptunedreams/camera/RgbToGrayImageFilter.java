package com.neptunedreams.camera;

import java.awt.Color;

/**
 * Formulas taken from http://en.wikipedia.org/wiki/Grayscale, accessed 6/18/2013.
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/18/13
 * <br>Time: 4:11 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("MagicNumber")
public final class RgbToGrayImageFilter extends AbstractImageFilter {

	private final Color color;
	private final int loss;

	@SuppressWarnings("WeakerAccess")
	public RgbToGrayImageFilter(final Color color) {
		super();
		this.color = color;
		// without compensating for the loss, filtered images get very dark.
		// HD TV
//		loss = ((color.getRed() * 54213) + (color.getGreen() * 182376) + (color.getBlue() * 18411)) / 255000;
		// N T S C
		loss = ((color.getRed() * 76245) + (color.getGreen() * 149685) + (color.getBlue() * 29070)) / 255000;
	}
	
	private RgbToGrayImageFilter(RgbToGrayImageFilter original) {
		super();
		this.color = original.color;
		this.loss = original.loss;
	}

	@Override
	public int process(int red, int grn, int blu, final int alf) {
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

	@SuppressWarnings({"MethodDoesntCallSuperMethod", "UseOfClone"})
	@Override
	public RgbToGrayImageFilter clone() {
		return new RgbToGrayImageFilter(this);
	}
}
