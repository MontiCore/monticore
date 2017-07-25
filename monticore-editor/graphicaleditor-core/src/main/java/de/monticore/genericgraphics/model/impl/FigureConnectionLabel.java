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
package de.monticore.genericgraphics.model.impl;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.model.IFigureConnectionLabel;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * <p>
 * A connection label that represents a figure.
 * </p>
 * <p>
 * It consists of a {@link ConnectionLocatorPosition position}, a
 * {@link IFigure} and a list of children object.
 * </p>
 * <p>
 * The connection label has its own {@link EditPart} implementation, which is
 * used to add the figure to the connection as child. Therefore, this list of
 * children is returned in <code>getModelChildren()</code> and corresponding
 * {@link EditPart EditParts} are created, and their figures are added as
 * children, to the figure specified by this label.
 * </p>
 * 
 * @author Tim Enger
 */
public class FigureConnectionLabel extends ConnectionLabel implements IFigureConnectionLabel {
  
  private IFigure figure;
  private List<Object> children;
  
  /**
   * Constructor
   * 
   * @param position The position.
   * @param figure The {@link IFigure} of this label
   * @param children The list of children objects
   */
  public FigureConnectionLabel(ConnectionLocatorPosition position, IFigure figure, List<Object> children) {
    super(position);
    this.figure = figure;
    this.children = children;
  }
  
  @Override
  public IFigure getFigure() {
    return figure;
  }
  
  @Override
  public void setFigure(IFigure figure) {
    this.figure = figure;
    
  }
  
  @Override
  public List<Object> getChildren() {
    return children;
  }
  
  @Override
  public void setChildren(List<Object> children) {
    this.children = children;
  }
  
}
