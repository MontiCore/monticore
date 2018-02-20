/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections.locators;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <p>
 * A {@link ConnectionLocator} implementation, that places a figure at the end
 * of a connection, either source or target.
 * </p>
 * <p>
 * This figure is moved around the source or target figure of the connection,
 * according to the position of the connection.
 * </p>
 * <p>
 * A gap for each side (North, South, East, West) can be set, to adjust to
 * possible borders (like ShadowBorder) or similar.<br>
 * <br>
 * Defaults values for the gaps are:
 * <ul>
 * <li>North = 1</li>
 * <li>East = -2</li>
 * <li>South = 5</li>
 * <li>West = 2</li>
 * </ul>
 * </p>
 * <p>
 * For example: A qualified association from class diagrams.<br>
 * The figure of this locator would a simple label with text, used as qualifier.
 * <br>
 * Using this locator the label would always be positioned at the border of the
 * figure.
 * </p>
 * 
 * @author Tim Enger
 */
public class EndPointFigureLocator extends ConnectionLocator {
  
  private int northGap = 1;
  private int eastGap = -2;
  private int southGap = 5;
  private int westGap = 2;
  
  private IFigure figure;
  
  /**
   * Constructor
   * 
   * @param connection The {@link Connection} this locator should be used for.
   * @param align The alignment of the locator. Only if
   *          {@link ConnectionLocator#SOURCE} and
   *          {@link ConnectionLocator#TARGET} will change the behavior. If
   *          {@link ConnectionLocator#MIDDLE} is passed, the default behavior
   *          of {@link ConnectionLocator} is used.
   * @param figure The {@link IFigure} that should be placed at the Endpoint of
   *          the connection.
   */
  public EndPointFigureLocator(Connection connection, int align, IFigure figure) {
    super(connection, align);
    this.figure = figure;
  }
  
  @Override
  protected Point getLocation(PointList points) {
    // size of figure
    Dimension prefsLabel = figure.getPreferredSize();
    int lHeight = prefsLabel.height;
    int lWidth = prefsLabel.width;
    
    Connection con = getConnection();
    
    Point point;
    IFigure fig;
    
    if (getAlignment() == SOURCE) {
      ConnectionAnchor sAnchor = con.getSourceAnchor();
      fig = sAnchor.getOwner();
      if (fig == null) {
        return super.getLocation(points);
      }
      point = points.getPoint(Point.SINGLETON, 0);
    }
    else {// TARGET:
      ConnectionAnchor tAnchor = con.getTargetAnchor();
      fig = tAnchor.getOwner();
      if (fig == null) {
        return super.getLocation(points);
      }
      point = points.getPoint(Point.SINGLETON, points.size() - 1);
    }
    
    Rectangle bounds = fig.getBounds();
    switch (PositionUtil.computePosition(bounds, point)) {
      case PositionConstants.EAST:
        point = new Point(point.x + lWidth / 2 + eastGap, point.y);
        break;
      case PositionConstants.NORTH:
        point = new Point(point.x, point.y - lHeight / 2 + northGap);
        break;
      case PositionConstants.SOUTH:
        point = new Point(point.x, point.y + southGap);
        break;
      case PositionConstants.WEST:
        point = new Point(point.x - lWidth / 2 + westGap, point.y);
        break;
    }
    return point;
  }
  
  /**
   * @return The eastGap
   */
  public int getEastGap() {
    return eastGap;
  }
  
  /**
   * @param eastGap The eastGap to set
   */
  public void setEastGap(int eastGap) {
    this.eastGap = eastGap;
  }
  
  /**
   * @return The westGap
   */
  public int getWestGap() {
    return westGap;
  }
  
  /**
   * @param westGap The westGap to set
   */
  public void setWestGap(int westGap) {
    this.westGap = westGap;
  }
  
  /**
   * @return The northGap
   */
  public int getNorthGap() {
    return northGap;
  }
  
  /**
   * @param northGap The northGap to set
   */
  public void setNorthGap(int northGap) {
    this.northGap = northGap;
  }
  
  /**
   * @return The southGap
   */
  public int getSouthGap() {
    return southGap;
  }
  
  /**
   * @param southGap The southGap to set
   */
  public void setSouthGap(int southGap) {
    this.southGap = southGap;
  }
}
