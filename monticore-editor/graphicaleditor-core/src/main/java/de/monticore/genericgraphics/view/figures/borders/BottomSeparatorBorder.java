/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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
package de.monticore.genericgraphics.view.figures.borders;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * A border extension of {@link MarginBorder} that draws a border at the bottom
 * of a {@link IFigure}.
 * 
 * @author Tim Enger
 */
public class BottomSeparatorBorder extends MarginBorder {
  
  private int width;
  private Color color;
  
  /**
   * Constructor
   * 
   * @param width Line width
   * @param color Line color
   */
  public BottomSeparatorBorder(int width, Color color) {
    this(0, 0, 0, 0, width, color);
  }
  
  /**
   * Constructor
   * 
   * @param t Top padding
   * @param l Left padding
   * @param b Bottom padding
   * @param r Right padding
   * @param width Line width
   * @param color Line color
   */
  public BottomSeparatorBorder(int t, int l, int b, int r, int width, Color color) {
    super(t, l, b, r);
    this.width = width;
    this.color = color;
  }
  
  @Override
  public void paint(IFigure f, Graphics g, Insets i) {
    Rectangle r = getPaintRectangle(f, i);
    r.height--;
    
    g.setLineWidth(width);
    g.setForegroundColor(color);
    g.drawLine(r.x, r.bottom(), r.right(), r.bottom());
  }
}
