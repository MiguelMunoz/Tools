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
@SuppressWarnings("WeakerAccess")
public final class HdGrayFilter extends NtscGrayFilter {
	private final Color color;
//	private final int loss;

	public HdGrayFilter(Color color) {
		super(color);
		this.color = color;
//		loss = super.getLoss();
	}
	
	private HdGrayFilter(HdGrayFilter original) {
		this(original.color);
	}

	@Override
	protected int gray(final int red, final int grn, final int blu) {
		//noinspection MagicNumber
		return ((red * 54213) + (grn * 182376) + (blu * 18411)) / 255000;
	}

	@SuppressWarnings("UseOfClone")
	@Override
	public HdGrayFilter clone() {
		return new HdGrayFilter(this);
	}
}
