package com.neptunedreams.penrose;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;

import static javax.swing.SwingConstants.*;

/**
 * This was a failed attempt to produce a graphic for Wikipedia. It was replaced by an svg file. I still don't 
 * understand why my red border doesn't show up.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/23/19
 * <p>Time: 7:42 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "UseOfSystemOutOrSystemErr"})
public class PenroseGraphic extends JPanel {
  
  private static final double MARGIN_FACTOR = 1.1;
  private static final int initialWidth = 400;
  private static final int initialHeight = 2 * initialWidth;
  private static final double INITIAL_THETA = 5.0;
  private static final int SLIDER_MAX = 1300;
  private static final int SLIDER_SCALE = 100;
  private PenroseCanvas dualCanvas;
  private final JLabel thetaLabel = new JLabel("");

  public static void main(String[] args) {
    JFrame frame = new JFrame("Penrose Graphic");
    frame.setLocationByPlatform(true);
    frame.add(new PenroseGraphic());
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
  
  PenroseGraphic() {
    super(new BorderLayout());
    add(makePieces(), BorderLayout.CENTER);
//    add(makeFlowers(), BorderLayout.PAGE_END);
  }
  
  private JComponent makePieces() {
    PNumeric2 pNumeric2 = new PNumeric2(INITIAL_THETA);
    final PNumeric2.BezierPath leftPath = pNumeric2.makeLeftPath();
    final PNumeric2.BezierPath rightPath = pNumeric2.makeRightPath();
    PenroseMasters.ClosedShape leaf = PenroseMasters.makeBezierLeaf(leftPath, rightPath);
    PenroseMasters.ClosedShape petal = PenroseMasters.makeBezierPetal(leftPath, rightPath);
    Rectangle2D leafBounds = leaf.getPath().getBounds2D();
    final double canvasWidth = leafBounds.getWidth() * MARGIN_FACTOR;
    dualCanvas = new PenroseCanvas(petal.getPath(), leaf.getPath(), canvasWidth, Color.blue);

    thetaLabel.setHorizontalAlignment(CENTER);
    setThetaLabel(INITIAL_THETA);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setMinimumSize(new Dimension(initialWidth, initialHeight));
    panel.add(dualCanvas, BorderLayout.CENTER);

    JSlider slider = new JSlider(HORIZONTAL, 0, SLIDER_MAX, (int) INITIAL_THETA* SLIDER_SCALE);
    panel.add(packNS(slider, thetaLabel), BorderLayout.PAGE_END);
    slider.addChangeListener(this::doTheta);

    return panel;
  }
  
  private void doTheta(ChangeEvent e) {
    JSlider slider = (JSlider) e.getSource();
    int value = slider.getValue();
    double theta = value/ (double) SLIDER_SCALE;
    PNumeric2 pNumeric2 = new PNumeric2(theta);
    final PNumeric2.BezierPath leftPath = pNumeric2.makeLeftPath();
    final PNumeric2.BezierPath rightPath = pNumeric2.makeRightPath();
    Shape leafShape = PenroseMasters.makeBezierLeaf(leftPath, rightPath).getPath();
    Shape petalShape = PenroseMasters.makeBezierPetal(leftPath, rightPath).getPath();
    dualCanvas.setShapes(leafShape, petalShape);
    setThetaLabel(theta);
  }
  
  private void setThetaLabel(double theta) {
    thetaLabel.setText(String.format("Theta = %5.2f", theta));
  }
  
  private JComponent makeFlowers() {
    URL flowersUrl = getClass().getResource("Flowers-B.png");
    ImageIcon icon = new ImageIcon(flowersUrl) {
      @Override
      public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        System.out.printf("paintIcon(..., %d, %d) align=%d%n", x, y, ((JLabel)c).getHorizontalAlignment());
        // TODO: Write .paintIcon()
        ImageObserver imageObserver = getImageObserver();
        int width = c.getWidth();
        int height = c.getHeight();
        Image image = getImage();
        if (imageObserver == null) {
          imageObserver = c;
        }
        g.drawImage(image, x, y, width, height, imageObserver);
      }
    };
    final JLabel jLabel = new JLabel(icon);
    jLabel.setHorizontalAlignment(LEFT);
    jLabel.setVerticalAlignment(BOTTOM);
    return jLabel;
  }
  
  private static class PenroseCanvas extends Canvas {
    private static final double sizeRatio = 0.95;
    private static final double marginRatio = (1.0 - sizeRatio)/2.0;

    private final Color bColor;
    private Shape petalShape;
    private Shape leafShape;
    private final double masterWidth;
    private final double aspectRatio;
    private final double initialSegmentLength;

    PenroseCanvas(Shape petalShape, Shape leafShape, double masterWidth, Color bColor) {
      super();
      this.masterWidth = masterWidth;
      this.petalShape = petalShape;
      this.leafShape = leafShape;
      this.bColor = bColor;
      initialSegmentLength = segmentLength();
      
      @SuppressWarnings("MagicNumber")
      double tempSize = 1000.0;
      AffineTransform scaledTransform = AffineTransform.getScaleInstance(tempSize, tempSize);
      Shape tempPetalShape = scaledTransform.createTransformedShape(petalShape);
      Shape tempLeafShape = scaledTransform.createTransformedShape(leafShape);

      Rectangle leafBounds = tempLeafShape.getBounds();
      Rectangle petalBounds = tempPetalShape.getBounds();
      double margin = leafBounds.getWidth() * marginRatio;
      aspectRatio = (leafBounds.getHeight() + petalBounds.getHeight() + (5 * margin)) / (leafBounds.getWidth() + (2 * margin));
      double masterHeight = aspectRatio*masterWidth;
      System.out.printf("Setting initial size to %s from %3.2f, %3.2f%n", getSize(), masterWidth, masterHeight);
      setSize(initialWidth, (int) (initialWidth*aspectRatio));
      setBackground(Color.WHITE);
    }

    private void setShapes(Shape leafShape, Shape petalShape) {
      this.leafShape = leafShape;
      this.petalShape = petalShape;
      repaint();
    }

    @Override
    public void update(final Graphics g) {
      paint(g); // don't clear the rectangle. This prevents screen flicker.
    }

    @Override
    public Dimension getPreferredSize() {
      Dimension prefSize = super.getPreferredSize();
      int width = Math.max(prefSize.width, initialWidth);
      return new Dimension(width, (int) (width * aspectRatio));
    }

    @Override
    public Dimension getSize() {
      return getPreferredSize();
    }

    @Override
    public void paint(final Graphics g) {
      Image image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = (Graphics2D) image.getGraphics();
      
//      createBufferStrategy(2);
      g2.setBackground(getBackground());
      g2.fillRect(0, 0, getWidth(), getHeight());

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      double width = getWidth();
      double scale = (width * sizeRatio) /masterWidth;
      double segmentLength = segmentLength();
//      System.out.printf("s Length: %6.2f%n", segmentLength);
      scale = (scale * initialSegmentLength) / segmentLength;
      double margin = marginRatio * width;
      AffineTransform savedTransform = g2.getTransform();
      AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
      Shape scaledLeaf = scaleTransform.createTransformedShape(leafShape);
      g2.setColor(Color.blue);

      final double TWO = 2.0;
      double halfWidth = width/ TWO;
      g2.translate(halfWidth, TWO *margin);
      g2.fill(scaledLeaf);
      double gap = (scale / initialSegmentLength) * segmentLength;
      g2.translate(0.0, gap);

      Shape scaledPetal = scaleTransform.createTransformedShape(petalShape);
      g2.fill(scaledPetal);

      g2.setTransform(savedTransform);

      g2.setStroke(new BasicStroke(1.0f));
      g2.setColor(bColor);
      g2.drawRect(0, 0, getWidth()-1, getHeight()-1);

      g.drawImage(image, 0, 0, null);
    }

    public double segmentLength() {
      Path2D.Double leafPath = (Path2D.Double) leafShape;
      PathIterator leafIterator = leafPath.getPathIterator(AffineTransform.getScaleInstance(1.0, 1.0));
      double[] empty = new double[6];
      int opType = leafIterator.currentSegment(empty);
      assert opType == PathIterator.SEG_MOVETO;
      Point2D start = new Point2D.Double(empty[0], empty[1]);
      leafIterator.next();
      double[] empty2 = new double[6];
      leafIterator.currentSegment(empty2);
      Point2D end = new Point2D.Double(empty2[2], empty2[3]);
      return start.distance(end);
    }
  }

  private static JPanel packNS(JComponent north, JComponent south) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(north, BorderLayout.PAGE_START);
    panel.add(south, BorderLayout.PAGE_END);
    return panel;
  }
}
