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
package de.monticore.genericgraphics.model;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


/**
 * @author Tim Enger
 */
public interface IFigureConnectionLabel extends IConnectionLabel {
  
  /**
   * @return The {@link IFigure} this label represents.
   */
  public IFigure getFigure();
  
  /**
   * Sets the {@link IFigure} of this label.
   * 
   * @param figure The {@link IFigure} of this label.
   */
  public void setFigure(IFigure figure);
  
  /**
   * <p>
   * The children will be handled as model objects, which are passed to the
   * {@link EditPartFactory} to create {@link EditPart EditsParts}.
   * </p>
   * <p>
   * They will be added as children of the connection label, and their graphical
   * representation is added to the figure defined in this class.
   * </p>
   * 
   * @return The list of children of this object.
   */
  public List<Object> getChildren();
  
  /**
   * <p>
   * The children will be handled as model objects, which are passed to the
   * {@link EditPartFactory} to create {@link EditPart EditsParts}.
   * </p>
   * <p>
   * They will be added as children of the connection label, and their graphical
   * representation is added to the figure defined in this class.
   * </p>
   * 
   * @param children The list of children objects.
   */
  public void setChildren(List<Object> children);
  
}
