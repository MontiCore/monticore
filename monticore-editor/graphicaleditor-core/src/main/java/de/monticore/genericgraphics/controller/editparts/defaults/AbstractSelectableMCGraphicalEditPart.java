/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts.defaults;

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
