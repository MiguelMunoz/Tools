package com.neptunedreams.camera;
import java.awt.Color;
import java.awt.image.ImageFilter;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/12/18
 * <p>Time: 1:13 AM
 *
 * @author Miguel Mu\u00f1oz
 */
interface FilterDestination {
	void applyFilter(ImageFilter filter);
	
	void applyColor(Color color);
}
