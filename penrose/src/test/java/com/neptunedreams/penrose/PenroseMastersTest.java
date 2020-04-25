package com.neptunedreams.penrose;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 4/25/20
 * <p>Time: 3:55 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class PenroseMastersTest {

  private static final double PI_OVER_2 = Math.PI / 2.0;
  private static final double PI_OVER_4 = Math.PI / 4.0;
  private static final double p3_PI_OVER_4 = (3.0 * Math.PI) / 4.0;
  private static final double DELTA = 1.0e-11;
  private static final double PI_OVER_6 = Math.PI / 6.0;

  @SuppressWarnings("MagicNumber")
  @Test
  public void testAngle() {
    Point2D.Double offset  = new Point2D.Double(5.3254, 6.992468);

    Point2D.Double vertex  = add(offset, new Point2D.Double(0.0, 0.0));
    Point2D.Double alpha   = add(offset, new Point2D.Double(0.0, 12.0));
    Point2D.Double bravo   = add(offset, new Point2D.Double(12.0, 12.0));
    Point2D.Double charlie = add(offset, new Point2D.Double(0.0016, 0.0));
    Point2D.Double delta   = add(offset, new Point2D.Double(38.0, -38.0));
    Point2D.Double echo    = add(offset, new Point2D.Double(0, -5.0));
    Point2D.Double foxTrot = add(offset, new Point2D.Double(-63.0, -63.0));
    Point2D.Double golf    = add(offset, new Point2D.Double(-7.0, 0));
    Point2D.Double hotel   = add(offset, new Point2D.Double(-16.5, 16.5));
    Point2D.Double[] points = { alpha, bravo, charlie, delta, echo, foxTrot, golf, hotel, alpha, bravo, charlie, delta };
    
    for (int i=0; i<8; ++i) {
      double angle = PenroseMasters.angle(points[i], vertex, points[i+1]);
      assertEquals(PI_OVER_4, angle, DELTA);
    }
    
    for (int i=0; i<8; ++i) {
      double angle = PenroseMasters.angle(points[i], vertex, points[i+2]);
      assertEquals(PI_OVER_2, angle, DELTA);
    }
    
    for (int i=0; i<8; ++i) {
      double angle = PenroseMasters.angle(points[i], vertex, points[i+3]);
      assertEquals(p3_PI_OVER_4, angle, DELTA);
    }

    for (int i = 0; i < 8; ++i) {
      double angle = PenroseMasters.angle(points[i], vertex, points[i + 4]);
      assertEquals(Math.PI, angle, DELTA);
    }

    arbitraryAngle(alpha, PI_OVER_6);
    arbitraryAngle(bravo, 1.8523);
    arbitraryAngle(charlie, 0.4832);
    arbitraryAngle(delta, 3.0642);
    arbitraryAngle(echo, 1.432158);
    arbitraryAngle(foxTrot, 0.9845);
    arbitraryAngle(golf, 0.243256);
    arbitraryAngle(hotel, 0.00045321); // such a small angle that we needed to drop delta to 1.0e-12
  }

  public void arbitraryAngle(final Point2D.Double alpha, final double rotation) {
    AffineTransform transform = AffineTransform.getRotateInstance(rotation);
    Point2D.Double zero = new Point2D.Double(0.0, 0.0);
    Point2D.Double alphaPrime = new Point2D.Double(0, 0);
    alphaPrime = (Point2D.Double) transform.transform(alpha, alphaPrime);
    double angle = PenroseMasters.angle(alpha, zero, alphaPrime);
    assertEquals(rotation, angle, DELTA);
  }

  private static Point2D.Double add(Point2D.Double a, Point2D.Double b) {
    return new Point2D.Double(a.x + b.x, a.y+b.y);
  }
}