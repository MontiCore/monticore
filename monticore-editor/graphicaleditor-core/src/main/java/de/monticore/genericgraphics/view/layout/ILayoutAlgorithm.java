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
package de.monticore.genericgraphics.view.layout;

import java.util.List;

import org.eclipse.gef.GraphicalViewer;

import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * Interface for all layout algorithm.
 * 
 * @author Tim Enger
 */
public interface ILayoutAlgorithm {
  
  /**
   * <p>
   * Layout the given list of {@link IMCViewElementEditPart
   * IMCViewElementEditParts}.
   * </p>
   * <p>
   * For every {@link IMCViewElementEditPart} in the list and recursively for
   * its children and so on:
   * <ul>
   * <li>every {@link IViewElement} is added</li>
   * <li>every {@link IViewElement} of an {@link IMCViewElementEditPart}
   * obtained by {@link IMCViewElementEditPart#getSourceConnections()} is added</li>
   * </ul>
   * </p>
   * <p>
   * This means, that in order to layout a whole diagram, only the top-level
   * elements, as e.g. obtained by {@link GraphicalViewer#getEditPartRegistry()}
   * , must be passed, and the rest is added recursively.<br>
   * <br>
   * It even suffices to pass the content editpart.
   * </p>
   * 
   * @param ves The list of {@link IMCViewElementEditPart
   *          IMCViewElementEditParts} to layout.
   */
  public void layout(List<IMCViewElementEditPart> ves);
  
  // public void layout(List<IMCViewElementEditPart> allVes,
  // List<IMCViewElementEditPart> layoutVes);
  
}
