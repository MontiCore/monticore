/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections.locators;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import de.se_rwth.commons.logging.Log;

/**
 * Util class
 * 
 * @author Tim Enger
 */
public class PositionUtil {
  
  /**
   * Compute the position of a {@link Point} relative to a {@link Rectangle}
   * (figure bounds).
   * 
   * @param figBounds The {@link Rectangle}
   * @param location The {@link Point}
   * @return The position of a {@link Point} relative to a {@link Rectangle}
   */
  public static int computePosition(Rectangle figBounds, Point location) {
    int figX = figBounds.x;
    int figY = figBounds.y;
    int figH = figBounds.height;
    int figW = figBounds.width;
    
    int locX = location.x;
    int locY = location.y;
    
    if (locX >= figX && locY <= figY) {
      return PositionConstants.NORTH;
    }
    if (locX >= figX && locY >= figY + figH) {
      return PositionConstants.SOUTH;
    }
    if (locX <= figX && locY >= figY) {
      return PositionConstants.WEST;
    }
    if (locX >= figX + figW && locY >= figY) {
      return PositionConstants.EAST;
    }
    
    // special case, this is the top left corner
    if (locX < figX && locY < figY) {
      return PositionConstants.NORTH;
    }
    
    Log.error("0xA1112 PositionUtil: Should not happen: fig: " + figBounds + " loc: " + location);
    return 0;
  }
  
}
