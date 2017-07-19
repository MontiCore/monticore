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
package de.monticore.genericgraphics.controller.editpolicies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.monticore.genericgraphics.controller.commands.ResizeMoveShapeCommand;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.se_rwth.commons.logging.Log;


/**
 * <p>
 * {@link EditPolicy} responsible for
 * <ul>
 * <li>resizing</li>
 * <li>moving</li>
 * </ul>
 * of {@link IShapeViewElement IShapeViewElements}.
 * </p>
 * 
 * @author Tim Enger
 */
public class ResizeMoveShapeEditPolicy extends XYLayoutEditPolicy {
  
  @Override
  protected Command getCreateCommand(CreateRequest request) {
    // we don't create anything
    return null;
  }
  
  @Override
  protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
    if (!(child instanceof IMCViewElementEditPart)) {
      Log.error("0xA1105 ResizeMoveShapeViewElementEditPolicy> Error! ViewElement is not a IShapeViewElement: " + child);
      return super.createChangeConstraintCommand(request, child, constraint);
    }
    
    IMCViewElementEditPart ep = (IMCViewElementEditPart) child;
    IViewElement ve = ep.getViewElement();
    
    if (!(ve instanceof IShapeViewElement)) {
      Log.error("0xA1106 ResizeMoveShapeViewElementEditPolicy> Error! EditPart is not a IMCViewElementEditPart: " + child);
      return super.createChangeConstraintCommand(request, child, constraint);
    }
    
    Rectangle box = (Rectangle) constraint;
    return new ResizeMoveShapeCommand((IShapeViewElement) ve, box.x, box.y, box.width, box.height, ep.getModel().toString());
  }
  
  @Override
  protected Rectangle getCurrentConstraintFor(GraphicalEditPart child) {
    // see bug:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?format=multiple&id=349042
    IFigure figure = child.getFigure();
    Object constraint = figure.getParent().getLayoutManager().getConstraint(figure);
    return constraint instanceof Rectangle ? (Rectangle) constraint : null;
  }
  
}
