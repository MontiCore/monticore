/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
package de.monticore.genericgraphics.view.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * <p>
 * A simple container figure, that arranges its elements in a list.
 * </p>
 * <p>
 * To arrange its children this figure uses a {@link ToolbarLayout}.
 * </p>
 * 
 * @author Tim Enger
 */
public class ListContainer extends Figure {
  
  /**
   * <p>
   * Constructor
   * </p>
   * uses {@link ToolbarLayout#ALIGN_TOPLEFT} as default label alignment.
   */
  public ListContainer() {
    this(ToolbarLayout.ALIGN_TOPLEFT);
  }
  
  /**
   * @param labelAlignment Choose between {@link ToolbarLayout#ALIGN_TOPLEFT},
   *          {@link ToolbarLayout#ALIGN_BOTTOMRIGHT} and
   *          {@link ToolbarLayout#ALIGN_CENTER}.
   */
  public ListContainer(int labelAlignment) {
    // layout
    ToolbarLayout layout = new ToolbarLayout();
    layout.setMinorAlignment(labelAlignment);
    layout.setStretchMinorAxis((true));
    setLayoutManager(layout);
  }
  
  @Override
  public Insets getInsets() {
    // top, left, bottom, right
    return new Insets(0, 0, 0, 0);
  }
}
