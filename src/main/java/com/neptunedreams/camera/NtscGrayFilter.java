package com.neptunedreams.camera;

import java.awt.Color;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/20/13
 * <br>Time: 2:06 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class NtscGrayFilter extends AbstractImageFilter {

	private static final int MAX_BYTE = 255;
	private final Color color;
	private final int loss;

	@SuppressWarnings("WeakerAccess")
	public NtscGrayFilter(Color color) {
		super();
		this.color = color;
		loss = gray(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	private NtscGrayFilter(NtscGrayFilter original) {
		super();
		color = original.color;
		loss = original.loss;
	}
	
	@SuppressWarnings("WeakerAccess")
	protected int getLoss() { return loss; }

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
//					int gray = ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
		// N T S C formula:
		int gray = gray(red, grn, blu);

		// It occasionally comes to 256, which is too big.
		gray = linearToGamma[Math.min(gray, MAX_BYTE)];
		return recombine(alpha, gray, gray, gray);
	}
	
	protected int gray(int red, int grn, int blu) {
		//noinspection MagicNumber
		return ((red * 76245) + (grn * 149685) + (blu * 29070)) / 255000;
	}

	@SuppressWarnings({"MethodDoesntCallSuperMethod", "UseOfClone"})
	@Override
	public NtscGrayFilter clone() {
		//noinspection CloneCallsConstructors
		return new NtscGrayFilter(this);
	}
}
