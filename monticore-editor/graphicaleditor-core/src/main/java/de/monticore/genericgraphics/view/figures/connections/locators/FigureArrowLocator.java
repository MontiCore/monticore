/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections.locators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ArrowLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <p>
 * A {@link ArrowLocator} implementation for {@link Connection Connections} that
 * use a {@link EndPointFigureLocator}. This Locator implementation sets the
 * arrow location to the {@link IFigure} used and not directly at the source
 * anchor position.
 * </p>
 * <p>
 * Note: Works only for {@link ConnectionLocator#SOURCE} and
 * {@link ConnectionLocator#TARGET} as location.
 * 
 * @author Tim Enger
 */
public class FigureArrowLocator extends ArrowLocator {
  
  private IFigure figure;
  
  /**
   * Constructor
   * 
   * @param connection The {@link Connection} associated with the locator
   * @param location The location of the arrow decoration. Only
   *          {@link ConnectionLocator#SOURCE} and
   *          {@link ConnectionLocator#TARGET} permitted.
   * @param figure The {@link IFigure} before which the arrow should be drawn
   */
  public FigureArrowLocator(Connection connection, int location, IFigure figure) {
    super(connection, location);
    this.figure = figure;
  }
  
  /**
   * Relocates the passed in figure (which must be a {@link RotatableDecoration}
   * ) at either the start or end of the connection.
   * 
   * @param target The RotatableDecoration to relocate
   */
  @Override
  public void relocate(IFigure target) {
    PointList points = getConnection().getPoints();
    RotatableDecoration arrow = (RotatableDecoration) target;
    
    if (figure != null) {
      // use the point, where the connection intersects with the figure
      Point inter = getIntersectionPoint();
      arrow.setLocation(inter);
    }
    else {
      arrow.setLocation(getLocation(points));
    }
    
    if (getAlignment() == SOURCE) {
      arrow.setReferencePoint(points.getPoint(1));
    }
    else if (getAlignment() == TARGET) {
      arrow.setReferencePoint(points.getPoint(points.size() - 2));
    }
  }
  
  /**
   * @return The intersection point of the connection with the figure
   */
  private Point getIntersectionPoint() {
    PointList points = getConnection().getPoints();
    
    Rectangle figB = figure.getBounds();
    
    ConnectionAnchor anchor;
    Point anchorPoint;
    if (isSource()) {
      anchor = getConnection().getSourceAnchor();
      anchorPoint = points.getFirstPoint();
    }
    else {
      anchor = getConnection().getTargetAnchor();
      anchorPoint = points.getLastPoint();
    }
    
    // compute intersections with all 3 edges of the figure
    // that could possibly intersect with the connection
    
    // therefore first compute where the figure is relative to the anchor figure
    int position = PositionUtil.computePosition(anchor.getOwner().getBounds(), anchorPoint);
    
    switch (position) {
      case PositionConstants.NORTH:
        // figure is above, so check top, right & left border
        return checkBorders(figB, true, true, false, true, PositionConstants.NORTH);
      case PositionConstants.EAST:
        // figure is right, so check top, bottom & right border
        return checkBorders(figB, true, true, true, false, PositionConstants.EAST);
      case PositionConstants.SOUTH:
        // figure is bottom, so check bottom, right & left border
        return checkBorders(figB, false, true, true, true, PositionConstants.SOUTH);
      case PositionConstants.WEST:
        // figure is right, so check top, bottom & right border
        return checkBorders(figB, true, false, true, true, PositionConstants.WEST);
    }
    
    assert false : "FigureArrowLocator: This should not happen: invalid PositionConstant";
    return new Point(0, 0);
  }
  
  private Point checkBorders(Rectangle figB, boolean checkTop, boolean checkRight, boolean checkBottom, boolean checkLeft, int position) {
    List<Point> points = new ArrayList<Point>();
    
    if (checkTop) {
      // check if it is on the top border
      Point top = getIntersectionPointWithLabel(PositionConstants.NORTH);
      if (top.y == figB.getTopLeft().y && top.x >= figB.x && top.x <= figB.x + figB.width) {
        points.add(top);
      }
    }
    
    if (checkRight) {
      // check if it is on the right border
      Point right = getIntersectionPointWithLabel(PositionConstants.EAST);
      if (right.x == figB.getTopRight().x && right.y >= figB.y && right.y <= figB.y + figB.height) {
        points.add(right);
      }
    }
    
    if (checkBottom) {
      Point bottom = getIntersectionPointWithLabel(PositionConstants.SOUTH);
      if (bottom.y == figB.getBottomLeft().y && bottom.x >= figB.x && bottom.x <= figB.x + figB.width) {
        points.add(bottom);
      }
    }
    
    if (checkLeft) {
      Point left = getIntersectionPointWithLabel(PositionConstants.WEST);
      if (left.x == figB.getTopLeft().x && left.y >= figB.y && left.y <= figB.y + figB.height) {
        points.add(left);
      }
    }
    
    // if only one point is found,
    // we're happy, so just return it
    if (points.size() == 1) {
      return points.get(0);
    }
    
    // if there are two points
    // this means, the connection intersect with the
    // left & right side or bottom & top side
    if (points.size() == 2) {
      Point p1 = points.get(0);
      Point p2 = points.get(1);
      
      switch (position) {
        case PositionConstants.NORTH:
          // the figure is placed on the top side
          // so chose the upper of both position
          int minY = Math.min(p1.y, p2.y);
          return minY == p1.y ? p1 : p2;
        case PositionConstants.EAST:
          // the figure is placed on the right side
          // so chose the most right of both position
          int maxX = Math.max(p1.x, p2.x);
          return maxX == p1.x ? p1 : p2;
        case PositionConstants.SOUTH:
          // the figure is placed on the bottom side
          // so chose the lower of both position
          int maxY = Math.max(p1.y, p2.y);
          return maxY == p1.y ? p1 : p2;
        case PositionConstants.WEST:
          // the figure is placed on the left side
          // so chose the most left of both position
          int minX = Math.min(p1.x, p2.x);
          return minX == p1.x ? p1 : p2;
      }
    }
    
    assert false : "FigureArrowLocator: This should not happen: no intersection found";
    return new Point(0, 0);
  }
  
  private boolean isSource() {
    if (getAlignment() == SOURCE) {
      return true;
    }
    return false;
  }
  
  private Point getIntersectionPointWithLabel(int line) {
    PointList points = getConnection().getPoints();
    Rectangle l = figure.getBounds();
    
    int x1 = 0;
    int y1 = 0;
    int x2 = 0;
    int y2 = 0;
    
    switch (line) {
      case PositionConstants.NORTH:
        // take the top border line of the label
        x1 = l.x;
        y1 = l.y;
        x2 = l.x + l.width;
        y2 = l.y;
        break;
      case PositionConstants.EAST:
        // the the right border line of the label
        x1 = l.x + l.width;
        y1 = l.y;
        x2 = l.x + l.width;
        y2 = l.y + l.height;
        break;
      case PositionConstants.SOUTH:
        // take the bottom border line of the label
        x1 = l.x;
        y1 = l.y + l.height;
        x2 = l.x + l.width;
        y2 = l.y + l.height;
        break;
      case PositionConstants.WEST:
        // the the left border line of the label
        x1 = l.x;
        y1 = l.y;
        x2 = l.x;
        y2 = l.y + l.height;
        break;
    }
    
    int x3 = getLocation(points).x;
    int y3 = getLocation(points).y;
    
    int x4, y4;
    Point p;
    if (isSource()) {
      p = points.getPoint(1);
    }
    else {
      p = points.getPoint(points.size() - 2);
    }
    
    x4 = p.x;
    y4 = p.y;
    
    return getIntersection(x1, y1, x2, y2, x3, y3, x4, y4);
  }
  
  /**
   * Computes the point of intersection between the two line l1 -> l2 and l3 ->
   * l4 where the points are given by
   * <ul>
   * <li>l1: (x1,y1)</li>
   * <li>l2: (x2,y2)</li>
   * <li>l1: (x3,y3)</li>
   * <li>l1: (x4,y4)</li>
   * </ul>
   * Return 0 if the lines are parallel.<br>
   * Note: the lines are not limited in this computation, so you'll always get
   * an intersection point, if not parallel.
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @param x3
   * @param y3
   * @param x4
   * @param y4
   * @return The intersecting point.
   */
  private Point getIntersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
    double px1 = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
    double px2 = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    
    double px = px1 / px2;
    
    double py1 = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
    double py2 = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    
    double py = py1 / py2;
    
    return new Point((int) Math.round(px), (int) Math.round(py));
  }
}
