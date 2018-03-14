/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editpolicies.connections;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.monticore.genericgraphics.controller.commands.connections.ConnectionLabelMoveCommand;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.editparts.intern.TextConnectionLabelEditPart;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.se_rwth.commons.logging.Log;


/**
 * A {@link XYLayoutEditPolicy} extension for providing the functionality of
 * moving connection labels.
 * 
 * @author Tim Enger
 */
public class ConnectionLabelMoveEditPolicy2 extends XYLayoutEditPolicy {
  
  @Override
  protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
    if (!(child instanceof IMCViewElementEditPart)) {
      Log.error("0xA1103 ConnectionLabelMoveEditPolicy2> Error! ViewElement is not a IShapeViewElement: " + child);
      return super.createChangeConstraintCommand(request, child, constraint);
    }
    
    IMCViewElementEditPart ep = (IMCViewElementEditPart) child;
    IViewElement ve = ep.getViewElement();
    
    if (!(ve instanceof IShapeViewElement)) {
      Log.error("0xA1104 ConnectionLabelMoveEditPolicy2> Error! EditPart is not a IMCViewElementEditPart: " + child);
      return super.createChangeConstraintCommand(request, child, constraint);
    }
    
    TextConnectionLabelEditPart host = (TextConnectionLabelEditPart) getHost();
    IShapeViewElement sve = host.getViewElement();
    IFigure parentFig = ((GraphicalEditPart) host.getParent()).getFigure();
    
    Point delta = request.getMoveDelta();
    return new ConnectionLabelMoveCommand(sve, parentFig, delta);
  }
  
  @Override
  protected Rectangle getCurrentConstraintFor(GraphicalEditPart child) {
    // see bug:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?format=multiple&id=349042
    IFigure figure = child.getFigure();
    Object constraint = figure.getParent().getLayoutManager().getConstraint(figure);
    return constraint instanceof Rectangle ? (Rectangle) constraint : null;
  }
  
  @Override
  protected Command getCreateCommand(CreateRequest request) {
    return null;
  }
}
