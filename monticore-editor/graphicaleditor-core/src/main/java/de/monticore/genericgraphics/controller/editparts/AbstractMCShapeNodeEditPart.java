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
package de.monticore.genericgraphics.controller.editparts;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;

import de.monticore.genericgraphics.view.figures.anchors.SelfReferencingChopboxAnchor;


/**
 * <p>
 * Extension of {@link AbstractMCShapeEditPart AbstractViewElementEditParts}
 * with common functionality of {@link IMCNodeEditPart}.
 * </p>
 * <p>
 * Provides additional common functionality for the following methods:
 * <ul>
 * <li>{@link #getSourceConnectionAnchor(ConnectionEditPart)}</li>
 * <li>{@link #getTargetConnectionAnchor(ConnectionEditPart)}</li>
 * <li>{@link #getSourceConnectionAnchor(Request)}</li>
 * <li>{@link #getTargetConnectionAnchor(Request)}</li>
 * </ul>
 * as follows:<br>
 * If the connections is not self referencing, use a {@link ChopboxAnchor}. If
 * the connection is self-referencing (getTarget() == getSource()), use a
 * {@link SelfReferencingChopboxAnchor}. <br>
 * </p>
 * 
 * @author Tim Enger
 */
public abstract class AbstractMCShapeNodeEditPart extends AbstractMCShapeEditPart implements IMCNodeEditPart {
  
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart con) {
    if (con.getSource() != null && con.getTarget() != null && con.getSource().equals(con.getTarget())) {
      return new SelfReferencingChopboxAnchor(getFigure(), true);
    }
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart con) {
    if (con.getSource() != null && con.getTarget() != null && con.getSource().equals(con.getTarget())) {
      return new SelfReferencingChopboxAnchor(getFigure(), false);
    }
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getSourceConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
  
  @Override
  public ConnectionAnchor getTargetConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
}
