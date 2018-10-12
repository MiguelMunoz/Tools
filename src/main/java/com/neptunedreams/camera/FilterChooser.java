package com.neptunedreams.camera;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/18/13
 * <br>Time: 2:28 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class FilterChooser extends JPanel {
	public static final int RADIUS = 100;
	public static final int DIAMETER = RADIUS*2;
	public static final int MARGIN = 10;
	public static final int CENTER = RADIUS + MARGIN;
	public static final int SMALL_RADIUS = 50;
	public static final int arcCount = 144;
	public static final int radiiCount = 32;
	public static final Arc2D colorArc = new Arc2D.Double(-RADIUS, -RADIUS, DIAMETER, DIAMETER, 0.0, -360.0/arcCount, Arc2D.PIE);
	private static final int CONTROL_SIZE = DIAMETER + (2 * MARGIN);
	private static final Shape outerCircle = new Arc2D.Double(0, 0, DIAMETER, DIAMETER, 0.0, 360.0, Arc2D.OPEN);
	private static final Stroke s2 = new BasicStroke(2.0f);
	private static final Shape innerCircle = new Arc2D.Double(0, 0, SMALL_RADIUS*2, SMALL_RADIUS*2, 0.0, 360.0, Arc2D.OPEN);
	
	private Color displayColor = Color.white;
	private List<FilterListener> filterListeners = new LinkedList<FilterListener>();
	private Image controlImage = makeTestImage();
	
	Canvas display = new Canvas() {
		@SuppressWarnings("MethodDoesntCallSuperMethod")
		@Override
		public void paint(final Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			setRenderHints(g2);
			AffineTransform savedTransform = g2.getTransform();
			try {
				g2.translate(MARGIN, MARGIN);
				g2.setColor(Color.white);
				g2.fill(outerCircle);
				g2.setColor(Color.black);
				g2.draw(outerCircle);
				g2.setTransform(savedTransform);
				g2.translate(MARGIN + (RADIUS - SMALL_RADIUS), MARGIN + (RADIUS - SMALL_RADIUS));
				g2.setColor(displayColor);
				g2.fill(innerCircle);
			} finally {
				g2.setTransform(savedTransform);
			}
		}
	};
	
	Canvas control = new Canvas() {
		@Override
		public void paint(final Graphics g) {
			g.drawImage(controlImage, 0, 0, this);
		}
	};

	private Image makeTestImage() {

		int size = CONTROL_SIZE;

		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		setRenderHints(g2);
		final Stroke s2 = new BasicStroke(2.0f);
		g2.setStroke(s2);
		AffineTransform savedTransform = g2.getTransform();

		// move to the center
		g2.setTransform(savedTransform);
		for (int saturation = 0; saturation < radiiCount; ++saturation) {
			g2.translate(MARGIN + RADIUS, MARGIN + RADIUS);
			float sat = (radiiCount - saturation) / (float) radiiCount;
			g2.scale(sat, sat);
			g2.rotate(-Math.PI / arcCount);
			for (int ii = 0; ii < arcCount; ++ii) {
				g2.setColor(Color.getHSBColor((ii / (float) arcCount), sat, 1.0f));
				g2.fill(colorArc);
				g2.rotate((Math.PI * 2) / arcCount);
			}
			g2.setTransform(savedTransform);
		}
		g2.translate(MARGIN, MARGIN);
		g2.setColor(Color.black);
		g2.draw(outerCircle);
//		g2.setTransform(savedTransform);
		g2.dispose();
		return image;
	}

	private static void setRenderHints(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(s2);
	}
	
	public FilterChooser() {
		super(new GridLayout(0, 1));
		int size = CONTROL_SIZE;
		display.setSize(size, size);
		add(display);
		add(control);
		
		// add mouse listener here.

		MouseListener ml = new MouseAdapter() {
			private final Point point=new Point(0, 0);
			@Override
			public void mouseDragged(final MouseEvent e) {
				process(e);
				submit();
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				process(e);
			}

			private void process(final MouseEvent e) {
				point.setLocation(e.getX() - CENTER, e.getY() - CENTER);
				double theta = (Math.atan2(point.y, point.x) / Math.PI / 2.0) + 1.0;
				double r = Math.sqrt((point.y * point.y) + (point.x * point.x)) / RADIUS;
				if (r > 1.0) {
					r = 1.0;
				}
				displayColor = Color.getHSBColor((float) theta, (float) r, 1.0f);
				display.repaint();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				process(e);
				submit();
			}
		};
		control.addMouseListener(ml);
		control.addMouseMotionListener((MouseMotionListener) ml);
		control.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	private void submit() {
		for (FilterListener filterListener: filterListeners) {
			filterListener.filter(displayColor);
		}
	}
	
	public void addFilterListener(FilterListener listener) {
		filterListeners.add(listener);
	}
	
	public void removeFilterListener(FilterListener listener) {
		filterListeners.remove(listener);
	}
	
	public interface FilterListener {
		void filter(Color color);
	}
}
