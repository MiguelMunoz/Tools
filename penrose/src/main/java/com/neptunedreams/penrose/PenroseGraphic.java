package com.neptunedreams.penrose;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

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
  public static void main(String[] args) {
    JFrame frame = new JFrame("Penrose Graphic");
    frame.setLocationByPlatform(true);
    frame.add(new PenroseGraphic());
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//    frame.pack();
    frame.setSize(200, 400);
    frame.setVisible(true);
  }
  
  PenroseGraphic() {
    super(new BorderLayout());
    add(makePieces(), BorderLayout.CENTER);
//    add(makeFlowers(), BorderLayout.PAGE_END);
  }
  
  private JComponent makePieces() {
    PNumeric2 pNumeric2 = new PNumeric2(5.0);
    final PNumeric2.BezierPath leftPath = pNumeric2.makeLeftPath();
    final PNumeric2.BezierPath rightPath = pNumeric2.makeRightPath();
    PenroseMasters.ClosedShape leaf = PenroseMasters.makeBezierLeaf(leftPath, rightPath);
    PenroseMasters.ClosedShape petal = PenroseMasters.makeBezierPetal(leftPath, rightPath);
    final double leafSize = leaf.getPath().getBounds2D().getWidth();
    AffineTransform identity = AffineTransform.getRotateInstance(0.0);
    final Shape paths = leaf.getPath();
//    Point2D leafStart = paths.getPathIterator(identity)
//    final double leafWidth = paths.getPathIterator(identity)
    
    Component leafCanvas = new PenroseCanvas(leaf.getPath(), leafSize, Color.blue);
    Component petalCanvas = new PenroseCanvas(petal.getPath(), leafSize, Color.ORANGE);

    JPanel panel = new JPanel();
    final BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
    panel.setLayout(layout);
    panel.setMinimumSize(new Dimension(200, 400));
    panel.add(leafCanvas, BorderLayout.PAGE_START);
    panel.add(petalCanvas, BorderLayout.CENTER);
    return panel;
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
  
  @SuppressWarnings({"MagicNumber", "HardcodedFileSeparator"})
  private class PenroseCanvas extends JPanel {
    private final Shape shape;
    private final double masterWidth;
    private final Color bColor;

    PenroseCanvas(Shape shape, double masterWidth, Color bColor) {
      super();
      this.masterWidth = masterWidth;
      this.shape = shape;
      this.bColor = bColor;
//      setMinimumSize(new Dimension(200, 200));
//      setSize(new Dimension(200, 200));
      
//      ComponentListener cl = new ComponentAdapter() {
//        @Override
//        public void componentResized(final ComponentEvent e) {
//          Thread.dumpStack();
//        }
//      };
//      addComponentListener(cl);
      setBorder(BorderFactory.createMatteBorder(10, 10, 10,10, Color.red));
//      setPreferredSize(new Dimension(500, adjustHeightFromWidth(500, 500)));
    }

    @Override
    public void setBorder(final Border border) {
      System.out.printf("setBorder(%s)%n", border);
      super.setBorder(border);
    }

    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
      final Rectangle2D bounds2D = shape.getBounds2D();
      double bWidth = bounds2D.getWidth();
      double bHeight = bounds2D.getHeight();

      float newHeight = (float) ((width * bHeight) / bWidth) * 1.1f;
      final int roundedHeight = Math.round(newHeight + 0.5f);
      super.setBounds(x, y, width, roundedHeight); // round up.
      System.out.printf("Adjusting size from (%d, %d) to (%d, %d)%n", width , height, width, roundedHeight);
    }

//    public int adjustHeightFromWidth(final int width, final double bWidth, final double bHeight) {
//      float newHeight = (float)((width * bHeight) / bWidth) * 1.1f;
//      return Math.round(newHeight + 0.5f);
//    }

    @Override
    public void paint(final Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      double width = getWidth();
      double scale = (width - 5.0) /masterWidth;
//      System.out.printf("scale %8.2f = %8.2f / %8.2f from bounds %s%n", scale, width, masterWidth, shape.getBounds2D());
      AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
      Shape scaledShape = scaleTransform.createTransformedShape(shape);
      AffineTransform savedTransform = g2.getTransform();
//      g2.transform(scaleTransform);
      
      Stroke lineStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
      g2.setColor(Color.black);
      
      double halfWidth = getWidth()/2.0;
//      double halfHeight = getHeight()/2.0;
      g2.translate(halfWidth, getHeight()/10.0);
      
//      double tScale = 0.05;
//      g2.transform(AffineTransform.getScaleInstance(tScale, tScale));
      g2.setStroke(lineStroke);
      g2.draw(scaledShape);
      
      g2.setTransform(savedTransform);
      
      g2.setStroke(new BasicStroke(1.0f));
      g2.setColor(bColor);
      g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
    }

//    @Override
//    public int getWidth() {
//      // TODO: Write PenroseCanvas.getWidth()
//      return super.getWidth();
//      throw new AssertionError("PenroseCanvas.getWidth() is not yet implemented");
//    }
  }
}
