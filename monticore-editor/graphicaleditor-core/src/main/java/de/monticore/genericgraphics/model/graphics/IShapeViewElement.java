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
package de.monticore.genericgraphics.model.graphics;

/**
 * Interface for positioned view elements.
 * 
 * @author Tim Enger
 */
public interface IShapeViewElement extends IViewElement {
  /**
   * @return The x.
   */
  public int getX();
  
  /**
   * @param x The x to set.
   */
  public void setX(int x);
  
  /**
   * @return The y.
   */
  public int getY();
  
  /**
   * @param y The y to set.
   */
  public void setY(int y);
  
  /**
   * @return The width.
   */
  public int getWidth();
  
  /**
   * @param width The width to set.
   */
  public void setWidth(int width);
  
  /**
   * @return The height.
   */
  public int getHeight();
  
  /**
   * @param height The height to set.
   */
  public void setHeight(int height);
}
