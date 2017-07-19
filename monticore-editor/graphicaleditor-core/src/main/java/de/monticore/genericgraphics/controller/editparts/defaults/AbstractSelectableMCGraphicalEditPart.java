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
package de.monticore.genericgraphics.controller.editparts.defaults;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import de.monticore.genericgraphics.controller.editparts.AbstractMCGraphicalEditPart;


/**
 * <p>
 * A "selectable" (default) implementation of
 * {@link AbstractMCGraphicalEditPart}.
 * </p>
 * <p>
 * This implementation provides all {@link EditPolicy EditPolicies} needed for
 * an EditPart, whose figure can be selected and provides visual feedback of the
 * selection using a {@link NonResizableEditPolicy}.
 * </p>
 * 
 * @author Tim Enger
 */
public abstract class AbstractSelectableMCGraphicalEditPart extends AbstractMCGraphicalEditPart {
  
  @Override
  protected void createEditPolicies() {
    super.createEditPolicies();
    NonResizableEditPolicy sel = new NonResizableEditPolicy();
    sel.setDragAllowed(false);
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, sel);
  }
}
