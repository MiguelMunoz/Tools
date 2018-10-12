package com.neptunedreams.camera;

/**
 * This filter completely desaturates the color, while preserving the gray-level. This lets you convert colored
 * anti-aliased text into gray-scale text, without lightening the text at all. 
 * <p>
 * IT was inspired by efforts to print a PDF file in which the user-entered text was in light blue, which printed out
 * as light gray, making it barely readable.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/11/18
 * <p>Time: 10:48 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class DeSaturationFilter extends AbstractFilter {
	@Override
	protected int process(final int alpha, final int red, final int green, final int blue) {
		int min = Math.min(red, Math.min(green, blue));
		return recombine(alpha, min, min, min);
	}
}
