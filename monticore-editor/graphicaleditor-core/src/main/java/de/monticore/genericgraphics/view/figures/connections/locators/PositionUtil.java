/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.view.figures.connections.locators;

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
