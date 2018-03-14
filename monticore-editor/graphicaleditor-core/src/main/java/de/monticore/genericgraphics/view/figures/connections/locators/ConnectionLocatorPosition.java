/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections.locators;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.PositionConstants;

/**
 * <p>
 * This enum represents some common positions for figure on a connection.
 * </p>
 * <p>
 * Note: to understand the different locators, imagine the following connection:
 * <br>
 * 
 * <pre>
 * SOURCE 0---1-----2-----3---4 TARGET
 * </pre>
 * 
 * The position are as follows:
 * <ul>
 * <li>0: {@link #SOURCE}</li>
 * <li>1:
 * <ul>
 * <li>above the line: {@link #BEFORE_SOURCE_UP}</li>
 * <li>below the line: {@link #BEFORE_SOURCE_DOWN}</li>
 * </ul>
 * <li>2:
 * <ul>
 * <li>above the line: {@link #MIDDLE_UP}</li>
 * <li>below the line: {@link #MIDDLE_DOWN}</li>
 * </ul>
 * <li>3:
 * <ul>
 * <li>above the line: {@link #BEFORE_TARGET_UP}</li>
 * <li>below the line: {@link #BEFORE_TARGET_DOWN}</li>
 * </ul>
 * </li>
 * <li>4: {@link #TARGET}</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public enum ConnectionLocatorPosition {
  /**
   * In the middle of a connection, above the line
   */
  MIDDLE_UP,
  /**
   * In the middle of a connection, below the line
   */
  MIDDLE_DOWN,
  /**
   * At the source of a connection
   */
  SOURCE,
  /**
   * Before the source of a connection, above the line
   */
  BEFORE_SOURCE_UP,
  /**
   * Before the source of a connection, below the line
   */
  BEFORE_SOURCE_DOWN,
  /**
   * At the target of a connection
   */
  TARGET,
  /**
   * Before the target of a connection, above the line
   */
  BEFORE_TARGET_UP,
  /**
   * Before the target of a connection, below the line
   */
  BEFORE_TARGET_DOWN;
  
  /**
   * Get a {@link Locator} for a {@link Connection} that corresponds to the
   * given {@link ConnectionLocatorPosition} .
   * 
   * @param locatorPosition The {@link ConnectionLocatorPosition}
   * @param con The {@link Connection}
   * @return The {@link Locator} for the {@link Connection} for the given
   *         {@link ConnectionLocatorPosition}.
   */
  public static Locator getLocator(ConnectionLocatorPosition locatorPosition, Connection con) {
    switch (locatorPosition) {
      case BEFORE_SOURCE_UP:
        ConnectionEndpointLocator bsu = new ConnectionEndpointLocator(con, false);
        // distance between the connection's owner and the figure being
        // positioned
        // better understandable:
        // distance between classFigure and the decorator along the line
        bsu.setUDistance(7);
        // distance between the connection and the figure being positioned
        // better understandable:
        // distance between line and the decorator w.r.t. to height
        bsu.setVDistance(-10);
        return bsu;
      case BEFORE_SOURCE_DOWN:
        ConnectionEndpointLocator bsd = new ConnectionEndpointLocator(con, false);
        bsd.setUDistance(7);
        bsd.setVDistance(10);
        return bsd;
      case BEFORE_TARGET_UP:
        ConnectionEndpointLocator btu = new ConnectionEndpointLocator(con, true);
        btu.setUDistance(7);
        btu.setVDistance(-10);
        return btu;
      case BEFORE_TARGET_DOWN:
        ConnectionEndpointLocator btd = new ConnectionEndpointLocator(con, true);
        btd.setUDistance(7);
        btd.setVDistance(10);
        return btd;
      case MIDDLE_DOWN:
        ConnectionLocator md = new ConnectionLocator(con, ConnectionLocator.MIDDLE);
        md.setRelativePosition(PositionConstants.SOUTH);
        return md;
      case MIDDLE_UP:
        ConnectionLocator mu = new ConnectionLocator(con, ConnectionLocator.MIDDLE);
        mu.setRelativePosition(PositionConstants.NORTH);
        return mu;
      case SOURCE:
        ConnectionEndpointLocator s = new ConnectionEndpointLocator(con, false);
        return s;
      case TARGET:
        ConnectionEndpointLocator t = new ConnectionEndpointLocator(con, true);
        return t;
      default:
        break;
    }
    return null;
  }
}
