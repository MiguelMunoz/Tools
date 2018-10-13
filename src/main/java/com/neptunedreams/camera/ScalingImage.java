package com.neptunedreams.camera;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/18/13
 * <br>Time: 12:39 AM
 * 
 * TODO: Repaint double-buffered. This is not a Swing component, so it doesn't do its own double-buffering.
 * 
 * TODO: Using the BiCubic rendering hint causes it to paint twice, and often incompletely. Create a test case
 * todo  and report it to Sun.
 * 
 * TODO: Give the user the option to re-render in color, using the gray-scale's value as a multiplier
 * 
 * TODO: let the user specify rendering hints. 
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("WeakerAccess")
public class ScalingImage extends Canvas {
	private Image image;
	public ScalingImage(Image image) {
		super();
		this.image = image;

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				getParent().revalidate();
			}
		});
	}
	
	public void setImage(Image image) {
		this.image = image;
		repaint();
	}

	@Override
	public void paint(final Graphics g) {
//		long start = System.currentTimeMillis();
		
		// In principle, all of this up to setting scaledHeight may be moved to the code that responds to resize.
		// I doubt this will speed things up much, but it's cleaner.
		if (image==null) { return; }
		int displayWidth = getWidth();
		int imageWidth = image.getWidth(this);
		double widthScale = ((double) displayWidth) / imageWidth;
		int displayHeight = getHeight();
		int imageHeight = image.getHeight(this);
		double heightScale = ((double) displayHeight) / imageHeight;

		boolean useWidth;
		double scale;
		if (widthScale < heightScale) {
			useWidth = true;
			scale = widthScale;
		} else {
			useWidth = false;
			scale = heightScale;
		}
		int scaledWidth = (int)Math.round(scale*imageWidth);
		int scaledHeight = (int)Math.round(scale*imageHeight);

		Graphics2D g2 = (Graphics2D) g;
		setRenderHints(g2);
		if (useWidth) {
			long ht = Math.round(scaledHeight);
			int deltaY = (int) ((displayHeight - ht)/2);
			g2.drawImage(image, 0, deltaY, scaledWidth, scaledHeight, this);
		} else {
			long wd = Math.round(scaledWidth);
			int deltaX = (int) ((displayWidth - wd)/2);
			g2.drawImage(image, deltaX, 0, scaledWidth, scaledHeight, this);
		}
//		long end = System.currentTimeMillis();
//		System.out.printf("Print in %d ms\n", (end - start));
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	private static void setRenderHints(Graphics2D g2) {
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	}

	public Image getImage() { return image; } 
}
