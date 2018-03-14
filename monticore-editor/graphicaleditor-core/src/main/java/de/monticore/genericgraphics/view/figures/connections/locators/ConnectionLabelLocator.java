/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections.locators;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A {@link Locator} implementation that can handle
 * <ul>
 * <li>a position given as {@link ConnectionLocatorPosition}</li>
 * <li>a offset given as a {@link Point}</li>
 * </ul>
 * for a given {@link PolylineConnection} on which a {@link IFigure} should be
 * placed at the position, with the offset.
 * 
 * @author Tim Enger
 */
public class ConnectionLabelLocator implements Locator {
  
  private ConnectionLocatorPosition position;
  private Point offset;
  private PolylineConnection connection;
  private IFigure figure;
  
  /**
   * Constructor
   * 
   * @param connection The {@link PolylineConnection}
   * @param position The {@link ConnectionLocatorPosition} where the figure
   *          should be located
   * @param figure The {@link IFigure} that should be placed at this location
   * @param offset The offset as {@link Point}
   */
  public ConnectionLabelLocator(PolylineConnection connection, ConnectionLocatorPosition position, Point offset, IFigure figure) {
    this.connection = connection;
    this.position = position;
    this.offset = offset;
    this.figure = figure;
  }
  
  /**
   * Constructor without setting the offset
   * 
   * @param connection The {@link PolylineConnection}
   * @param position The {@link ConnectionLocatorPosition} where the figure
   *          should be located
   * @param figure The {@link IFigure} that should be placed at this location
   */
  public ConnectionLabelLocator(PolylineConnection connection, ConnectionLocatorPosition position, IFigure figure) {
    this.connection = connection;
    this.position = position;
    this.figure = figure;
  }
  
  @Override
  public void relocate(IFigure target) {
    target.setSize(target.getPreferredSize());
    Point location = getLocation();
    
    if (offset != null) {
      location.translate(offset);
    }
    target.setLocation(location);
    target.repaint();
  }
  
  /**
   * Set new offset.
   * 
   * @param offset The offset to set
   */
  public void setOffset(Point offset) {
    this.offset = offset;
  }
  
  /**
   * Set new offset.
   * 
   * @param x The x position
   * @param y The y position
   */
  public void setOffset(int x, int y) {
    setOffset(new Point(x, y));
  }
  
  /**
   * Set new offset by computing the distance to the from the connection
   * location and this point.
   * 
   * @param point The {@link Point} from which the new offset has to be computed
   */
  public void setOffsetFromPoint(Point point) {
    Point location = getLocation();
    
    if (location != null) {
      Dimension diff;
      diff = point.getDifference(location);
      offset = new Point(diff.width, diff.height);
    }
  }
  
  /**
   * Set new offset by computing the distance to the from the connection
   * location and this point, given by x and y.
   * 
   * @param x The x position
   * @param y The y position
   */
  public void setOffsetFromPoint(int x, int y) {
    setOffsetFromPoint(new Point(x, y));
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ConnectionLabelLocator> ");
    
    sb.append("position: ");
    sb.append(position);
    
    sb.append(" offset: ");
    sb.append(offset);
    
    return sb.toString();
  }
  
  private Point getLocation() {
    return getLocation(position, connection, figure);
  }
  
  /**
   * Helper method for returning a {@link Point} that corresponds to the given
   * {@link ConnectionLocatorPosition} on the given {@link PolylineConnection}.
   * 
   * @param position The {@link ConnectionLocatorPosition}
   * @param connection The {@link PolylineConnection}
   * @param figure The {@link IFigure} that should be placed at this location
   * @return The {@link Point} corresponding to the position and connection
   */
  public static Point getLocation(ConnectionLocatorPosition position, PolylineConnection connection, IFigure figure) {
    Point location = null;
    
    PointList points = connection.getPoints();
    Rectangle bounds = figure.getBounds();
    
    switch (position) {
      case SOURCE:
        location = connection.getStart().getCopy();
        break;
      case BEFORE_SOURCE_UP:
        // choose the middle between mid & target
        Point startu = connection.getStart().getCopy();
        Point midsu = points.getPoint(1);
        location = startu.getTranslated(midsu).scale(0.5f);
        location.x -= bounds.width / 2;
        location.y -= bounds.height / 2;
        break;
      case BEFORE_SOURCE_DOWN:
        // choose the middle between mid & target
        Point startd = connection.getStart().getCopy();
        Point midsd = points.getPoint(1);
        location = startd.getTranslated(midsd).scale(0.5f);
        location.x -= bounds.width / 2;
        location.y -= bounds.height / 2;
        break;
      case MIDDLE_DOWN:
        location = connection.getPoints().getMidpoint().getCopy();
        location.x -= bounds.width / 2;
        location.y += bounds.height / 2;
        break;
      case MIDDLE_UP:
        location = connection.getPoints().getMidpoint().getCopy();
        location.x -= bounds.width / 2;
        location.y -= bounds.height / 2;
        break;
      case BEFORE_TARGET_UP:
        // choose the middle between mid & target
        Point endu = connection.getEnd().getCopy();
        Point mideu = points.getPoint(points.size() - 2);
        location = mideu.getTranslated(endu).scale(0.5f);
        location.x -= bounds.width / 2;
        location.y -= bounds.height / 2;
        break;
      case BEFORE_TARGET_DOWN:
        // choose the middle between mid & target
        Point endd = connection.getEnd().getCopy();
        Point mided = points.getPoint(points.size() - 2);
        location = mided.getTranslated(endd).scale(0.5f);
        location.x -= bounds.width / 2;
        location.y -= bounds.height / 2;
        break;
      case TARGET:
        location = connection.getEnd().getCopy();
        break;
      default:
        break;
    }
    
    return location;
  }
}
