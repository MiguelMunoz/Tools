package com.neptunedreams.penrose;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import static java.awt.print.Printable.*;

/**
 * Draws only 22 small diamonds. We need it to draw 28 of them.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/9/14
 * <p>Time: 12:46 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "UseOfSystemOutOrSystemErr"})
public final class PenroseDiamonds extends JPanel {

	private static final double DPI = 72; // Dots per inch (1 dot = 1 screen pixel)
	private static final double HT_INCHES = 10.5;
	private static final double HEIGHT = HT_INCHES * DPI;
	private static final double WIDTH = 8 * DPI;
	private static final double diamondHeight = (((HEIGHT * 10) / HT_INCHES) / 4.6);
	private static final double deg18 = toRads(18);
	private static final double deg36 = toRads(36);

	// width/height = tan(18) (degrees)
	private static final double diamondWidth = diamondHeight * StrictMath.tan(deg18);
	private static final double edgeLength = diamondWidth / StrictMath.sin(deg18) / 2.0;
	private static final Color THIN_ARC_COLOR = Color.red;
	private static final Color THICK_ARC_COLOR = Color.blue;

	private double bigDiamondWidth = StrictMath.sin(deg36)*edgeLength*2;
	private double bigDiamondHeight = StrictMath.cos(deg36)*edgeLength*2;
//	private static final double phi = (1.0+ Math.sqrt(5.0))/2.0;
//	private static final double FINE_RADIUS_THIN_DMD = (edgeLength - (edgeLength / phi))/2.0; //5.0; // best
//	private static final double FINE_RADIUS_THIN_DMD = (edgeLength / phi); //5.0; // best
//	private static final double FINE_RADIUS_THIN_DMD = (edgeLength - (edgeLength / phi)) * 0.8; // match
//	private static final double FINE_RADIUS_THIN_DMD = (edgeLength * 0.2); //
	
	// To change the size of the arcs, you may vary the size of this constant from 0.0 to about 45.0.
	// zero produces large thick arcs and no thin ones. 25.0 produces arcs of the same size on the small
	// diamonds, with small thick and large thin arcs on the big ones. The strokes are different thicknesses
	// for colorblind people, but color may be changed using different values for THIN_ARC_COLOR and
	// THICK_ARC_COLOR.
	private static final double FINE_RADIUS_THIN_DMD = 25.0; //
	private static final double FINE_DIAMETER_THIN_DMD = FINE_RADIUS_THIN_DMD * 2.0;
	private final Canvas smallCanvas;
	private final Canvas bigCanvas;

	public static void main(String[] args) {
		System.out.printf("Edge: %5.3f%nRad:  %5.3f%n", edgeLength, FINE_RADIUS_THIN_DMD);
		JFrame frame = new JFrame("Penrose Diamonds");
		PenroseDiamonds diamonds = new PenroseDiamonds();
		frame.add(diamonds);
		diamonds.setup();
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	private Stroke thinStroke = new BasicStroke(1.0f/4.0f);
	private Stroke fineCurve = new BasicStroke(1.0f);
	private Stroke coarseCurve = new BasicStroke(3.0f);
	
	private void setup() {
		// must be called after adding this to a root pane!
		JMenuItem printItem = new JMenuItem("Print");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		//noinspection MagicConstant
		final KeyStroke keyStroke = KeyStroke.getKeyStroke('P', toolkit.getMenuShortcutKeyMaskEx());
		printItem.setAccelerator(keyStroke);
		printItem.addActionListener(makePrintAction());
		
		JMenu menu = new JMenu("File");
		menu.add(printItem);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);

		getRootPane().setJMenuBar(menuBar);
	}

	private ActionListener makePrintAction() {
		return e -> {
			PrinterJob job = PrinterJob.getPrinterJob();
			Printable printable = (graphics, pageFormat, pageIndex) -> {
				if (pageIndex == 0) {
					smallCanvas.print(graphics);
					return PAGE_EXISTS;
				}
				if (pageIndex == 1) {
					bigCanvas.print(graphics);
					return PAGE_EXISTS;
				} else {
					return NO_SUCH_PAGE;
				}
			};
			job.setPrintable(printable);
			boolean doPrint = job.printDialog();
			if (doPrint) {
				try {
					job.print();
				} catch (PrinterException pe) {
					pe.printStackTrace();
				}
			}
		};
	}

	private PenroseDiamonds() {
		super(new GridLayout(1, 0));
		smallCanvas = new Canvas() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.white);
				Rectangle2D page = new Rectangle2D.Double(0.0, 0.0, WIDTH, HEIGHT);
				g2.fill(page);
				g2.translate(0.0, DPI*0.25);
				paintSmallDiamonds(g2);
			}
		};
		smallCanvas.setBackground(Color.white);
		bigCanvas = new Canvas() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.white);
				Rectangle2D page = new Rectangle2D.Double(0.0, 0.0, WIDTH, HEIGHT);
				g2.fill(page);
				g2.translate(0.0, DPI * 0.25);
				paintBigDiamonds(g2);
			}
		};
		bigCanvas.setBackground(Color.white);

		// printing to a pdf file clips the first quarter of an inch, so I shift the bounds 
		// vertically to move past the clipped region. 
		smallCanvas.setBounds(0, (int) DPI / 4, (int) WIDTH, (int) HEIGHT);
		bigCanvas.setBounds(0, (int) DPI / 4, (int) WIDTH, (int) HEIGHT);
		add(smallCanvas);
		add(bigCanvas);
	}
	
	private void paintBigDiamonds(Graphics2D g2) {
		Area area = createDiamond(bigDiamondWidth, bigDiamondHeight);
		AffineTransform savedTransform = g2.getTransform();
		g2.setColor(Color.white);
		Rectangle2D page = new Rectangle2D.Double(0.0, 0.0, WIDTH, HEIGHT);
		g2.fill(page);
		g2.translate(WIDTH / 4.0 , 0.0);
		paintBigDiamondRow(g2, 4, area);
		g2.translate(-bigDiamondWidth/2, bigDiamondHeight/2);
		paintBigDiamondRow(g2, 5, area);
		g2.translate(bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 4, area);
		g2.translate(-bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 5, area);
		g2.translate(bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 4, area);
		g2.translate(-bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 5, area);
		g2.translate(bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 4, area);
		g2.translate(-bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 5, area);
		g2.translate(bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 4, area);
		g2.translate(-bigDiamondWidth / 2, bigDiamondHeight / 2);
		paintBigDiamondRow(g2, 5, area);
		g2.setTransform(savedTransform);
	}
	
	private void paintSmallDiamonds(Graphics2D g2) {
		Area area = createDiamond(diamondWidth, diamondHeight);
		g2.setColor(Color.white);
		Rectangle2D page = new Rectangle2D.Double(0.0, 0.0, WIDTH, HEIGHT);
		g2.fill(page);
		AffineTransform savedTransform = g2.getTransform();
		g2.translate(WIDTH/3.0, 0.0);
		paintThinDiamondRow(g2, 2, area);
		g2.translate(-diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 3, area);
		g2.translate(-diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 4, area);
		g2.translate(-diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 5, area);
		g2.translate(diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 5, area);
		g2.translate(diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 4, area);
		g2.translate(diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 3, area);
		g2.translate(diamondWidth / 2, diamondHeight / 2);
		paintThinDiamondRow(g2, 2, area);
		g2.setTransform(savedTransform);
		
		// paint lines:
		g2.setColor(Color.black);
		g2.setStroke(thinStroke);
		g2.translate((WIDTH / 3.0) - (4 * diamondWidth), 0.0);
		Line2D line = new Line2D.Double(0.0, 0.0, 5*diamondWidth, 5*diamondHeight);
		for (int ii=0; ii<6; ++ii) {
			g2.draw(line);
			g2.translate(diamondWidth, 0.0);
		}
		g2.setTransform(savedTransform);
	}
	
	private void paintThinDiamondRow(Graphics2D g2, int count, Area area) {
		AffineTransform savedTransform = g2.getTransform();
		for (int ii=0; ii<count; ++ii) {
			paintThinDiamond(g2, area);
			g2.translate(diamondWidth, 0.0);
		}
		g2.setTransform(savedTransform);
	}
	
	private void paintBigDiamondRow(Graphics2D g2, int count, Area area) {
		AffineTransform savedTransform = g2.getTransform();
		for (int ii=0; ii<count; ++ii) {
			paintBigDiamond(g2, area);
			g2.translate(bigDiamondWidth, 0.0);
		}
		g2.setTransform(savedTransform);
	}

	private void paintThinDiamond(Graphics2D g2, Area area) {
		AffineTransform diamondBase = g2.getTransform();
		g2.setStroke(thinStroke);
		g2.setColor(Color.white);
		g2.fill(area);
		g2.setColor(Color.black);
		g2.draw(area);

		g2.setTransform(diamondBase);
		g2.clip(area);
		drawSmallDiamondArcs(g2, diamondBase);
		g2.setClip(null);
	}

	private void drawSmallDiamondArcs(Graphics2D g2, AffineTransform pDiamondBase) {
		g2.translate((diamondWidth / 2.0) - FINE_RADIUS_THIN_DMD, (diamondHeight / 2.0) - FINE_RADIUS_THIN_DMD);
		g2.setStroke(fineCurve);
		g2.setColor(THIN_ARC_COLOR);
		Arc2D fineArc = new Arc2D.Double(0, 0, FINE_DIAMETER_THIN_DMD, FINE_DIAMETER_THIN_DMD, 108, 144.0, Arc2D.OPEN);
		g2.draw(fineArc);

		g2.setTransform(pDiamondBase);
		double coarseRadius = diamondWidth - FINE_RADIUS_THIN_DMD - (4.0f / 2); // 4.0 is the sum of the two stroke widths.
		double coarseDiameter = coarseRadius*2.0;
		g2.translate((-diamondWidth / 2.0) + coarseRadius, (diamondHeight / 2.0) + coarseRadius);

		Arc2D coarseArc = new Arc2D.Double(-coarseDiameter, -coarseDiameter, coarseDiameter, coarseDiameter, -72, 144.0, Arc2D.OPEN);
		g2.setStroke(coarseCurve);
		g2.setColor(THICK_ARC_COLOR);
		g2.draw(coarseArc);
		g2.setTransform(pDiamondBase);
	}

	private void paintBigDiamond(Graphics2D g2, Area area) {
		AffineTransform diamondBase = g2.getTransform();
		g2.setStroke(thinStroke);
		g2.setColor(Color.white);
		g2.fill(area);
		g2.setColor(Color.black);
		g2.draw(area);
		
		Graphics2D g2Clip = (Graphics2D) g2.create();
		g2Clip.setClip(area);

		double fineRadius = edgeLength - FINE_RADIUS_THIN_DMD;
		double fineDiameter = fineRadius * 2.0;
		g2Clip.translate(fineRadius, (bigDiamondHeight) + fineRadius);
		Arc2D fineArc = new Arc2D.Double(-fineDiameter, -fineDiameter, fineDiameter, fineDiameter, 90-36, 72.0, Arc2D.OPEN);
		g2Clip.setStroke(fineCurve);
		g2Clip.setColor(THIN_ARC_COLOR);
		g2Clip.draw(fineArc);

		g2Clip.setTransform(diamondBase);
		double coarseRadius = diamondWidth - FINE_RADIUS_THIN_DMD - (4.0f /2); // 4.0 is the sum of the two stroke widths.
		double coarseDiameter = coarseRadius * 2.0;
		g2Clip.translate(coarseRadius, coarseRadius);

		Arc2D coarseArc = new Arc2D.Double(-coarseDiameter, -coarseDiameter, coarseDiameter, coarseDiameter, 270-36, 72, Arc2D.OPEN);
		g2Clip.setStroke(coarseCurve);
		g2Clip.setColor(THICK_ARC_COLOR);
		g2Clip.draw(coarseArc);
		g2Clip.dispose();
		g2.setTransform(diamondBase);
	}

	private static Area createDiamond(double width, double height) {
		GeneralPath path = new GeneralPath();
		path.moveTo(0.0, 0.0);
		path.lineTo(width/2.0, height/2.0);
		path.lineTo(0, height);
		path.lineTo(-width/2.0, height/2.0);
		path.lineTo(0.0, 0.0);
		return new Area(path);
	}

	private static double toRads(double degrees) { return (degrees * Math.PI) / 180.0; }
}
